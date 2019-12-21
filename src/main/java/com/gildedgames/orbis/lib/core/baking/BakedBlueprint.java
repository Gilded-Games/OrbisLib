package com.gildedgames.orbis.lib.core.baking;

import com.gildedgames.orbis.lib.block.BlockDataContainer;
import com.gildedgames.orbis.lib.core.BlueprintDefinition;
import com.gildedgames.orbis.lib.core.CreationData;
import com.gildedgames.orbis.lib.core.ICreationData;
import com.gildedgames.orbis.lib.core.tree.INode;
import com.gildedgames.orbis.lib.core.tree.LayerLink;
import com.gildedgames.orbis.lib.core.util.BlueprintUtil;
import com.gildedgames.orbis.lib.core.variables.post_resolve_actions.IPostResolveAction;
import com.gildedgames.orbis.lib.data.IDataUser;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintDataPalette;
import com.gildedgames.orbis.lib.data.region.IRegion;
import com.gildedgames.orbis.lib.data.region.Region;
import com.gildedgames.orbis.lib.data.schedules.*;
import com.gildedgames.orbis.lib.util.RotationHelp;
import com.gildedgames.orbis.lib.util.mc.BlockUtil;
import com.gildedgames.orbis.lib.util.mc.NBT;
import com.gildedgames.orbis.lib.util.mc.NBTHelper;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.gildedgames.orbis.lib.util.RotationHelp.transformedBlockPos;

public class BakedBlueprint
{
	private List<ScheduleRegion> bakedScheduleRegions = Lists.newArrayList();

	private List<BakedBlueprint> bakedBlueprintChildren = Lists.newArrayList();

	private BlockDataContainer bakedBlocks;

	private Region bakedRegion;

	private BlueprintData blueprintData;

	private ICreationData<?> creationData;

	private BlueprintDefinition definition;

	private BakedScheduleLayers bakedScheduleLayers;

	public BakedBlueprint(BlueprintDefinition definition, ICreationData<?> creationData)
	{
		this(definition.getData(), creationData);
		this.definition = definition;
	}

	public BakedBlueprint(BlueprintData data, ICreationData<?> creationData)
	{
		this(data, new BakedScheduleLayers(data, creationData.getRandom()), creationData);
	}

	public BakedBlueprint(BlueprintData data, BakedScheduleLayers bakedScheduleLayers, ICreationData<?> creationData)
	{
		this.blueprintData = data;
		this.bakedScheduleLayers = bakedScheduleLayers;
		this.creationData = creationData;

		this.bake();
	}

	public ICreationData<?> getCreationData()
	{
		return this.creationData;
	}

	public ScheduleRegion getScheduleFromTriggerID(String triggerId)
	{
		for (ScheduleRegion s : this.bakedScheduleRegions)
		{
			if (s.getTriggerId().equals(triggerId))
			{
				return s;
			}
		}

		return null;
	}

	private void updateBlueprintChildren()
	{
		for (INode<IScheduleLayer, LayerLink> node : this.bakedScheduleLayers.getScheduleLayerNodes())
		{
			IScheduleLayer layer = node.getData();

			for (ScheduleBlueprint s : layer.getScheduleRecord().getSchedules(ScheduleBlueprint.class))
			{
				BlockPos pos = s.getBounds().getMin();
				BlueprintDataPalette palette = s.getPalette();

				if (palette.getData().size() > 0)
				{
					BlueprintData data = palette.fetchRandom(this.creationData.getWorld(), this.creationData.getRandom());
					ICreationData creationData = new CreationData(this.creationData.getWorld(), this.creationData.getRandom().nextLong())
							.pos(this.creationData.getPos().add(pos))
							.rotation(s.getRotation())
							.placesAir(this.creationData.placeAir());

					BakedBlueprint baked = new BakedBlueprint(data, creationData);

					this.bakedBlueprintChildren.add(baked);
				}
			}
		}
	}

	public List<IBakedPosAction> getBakedPositionActions()
	{
		Region boundsBeforeRotateAtOrigin = new Region(new BlockPos(0, 0, 0), new BlockPos(this.blueprintData.getWidth() - 1,
				this.blueprintData.getHeight() - 1, this.blueprintData.getLength() - 1));

		for (INode<IScheduleLayer, LayerLink> node : this.bakedScheduleLayers.getScheduleLayerNodes())
		{
			IScheduleLayer layer = node.getData();

			for (ScheduleRegion s : layer.getScheduleRecord().getSchedules(ScheduleRegion.class))
			{
				IRegion rotatedBounds = RotationHelp.rotate(s.getBounds(), boundsBeforeRotateAtOrigin, this.creationData.getRotation());

				if (!s.getConditionNodeTree().isEmpty() && !this.bakedScheduleLayers.resolveChildrenConditions(s.getConditionNodeTree().getRootNode()))
				{
					continue;
				}

				IRegion bounds = new Region(this.creationData.getPos().add(rotatedBounds.getMin()), this.creationData.getPos().add(rotatedBounds.getMax()));

				for (INode<IPostResolveAction, NBT> actionNode : s.getPostResolveActionNodeTree().getNodes())
				{
					if (actionNode.getData() instanceof IDataUser)
					{
						IDataUser dataUser = (IDataUser) actionNode.getData();

						if (dataUser.getDataIdentifier().equals("scheduleRegion"))
						{
							dataUser.setUsedData(s);
						}
					}

					actionNode.getData().resolve(this.creationData.getRandom());

					if (actionNode.getData() instanceof IPosActionBaker)
					{
						IPosActionBaker baker = (IPosActionBaker) actionNode.getData();

						return baker.bakeActions(bounds, this.creationData.getRandom(), this.creationData.getRotation());
					}
				}
			}
		}

		return new ArrayList<>();
	}

	private void updatePostGenReplaceLayers()
	{
		for (PostGenReplaceLayer postGenReplaceLayer : this.blueprintData.getPostGenReplaceLayers().values())
		{
			if (postGenReplaceLayer.getFilterLayer().getRequiredBlocks().isEmpty() || postGenReplaceLayer.getFilterLayer().getReplacementBlocks().isEmpty())
			{
				continue;
			}

			boolean choosesPerBlockOld = postGenReplaceLayer.getOptions().getChoosesPerBlockVar().getData();

			postGenReplaceLayer.getOptions().getChoosesPerBlockVar()
					.setData(this.blueprintData.getBlueprintMetadata().getChoosePerBlockOnPostGenVar().getData());

			int width = this.bakedBlocks.getWidth(),
					height = this.bakedBlocks.getHeight(),
					length = this.bakedBlocks.getLength();

			BlockPos min = new BlockPos(0, 0, 0);
			BlockPos max = new BlockPos(width - 1, height - 1, length - 1);

			Region region = new Region(min, max);

			postGenReplaceLayer.getFilter()
					.apply(region.createShapeData(), this.bakedBlocks, this.creationData, postGenReplaceLayer.getOptions());

			postGenReplaceLayer.getOptions().getChoosesPerBlockVar()
					.setData(choosesPerBlockOld);
		}
	}

	private void updateBlocks()
	{
		BlockDataContainer blocks = this.blueprintData.getBlockDataContainer();

		Rotation rotation = this.creationData.getRotation();

		this.bakedBlocks = this.rotateBlocksAndApplyLayers(blocks, rotation);

		BlockPos dimensions = new BlockPos(blocks.getWidth() - 1, blocks.getHeight() - 1, blocks.getLength() - 1);

		this.bakedRegion = new Region(BlockPos.ORIGIN, transformedBlockPos(dimensions, rotation));
		this.bakedRegion.add(this.creationData.getPos());
	}

	public BlockDataContainer rotateBlocksAndApplyLayers(BlockDataContainer origBlocks, Rotation rotation)
	{
		final RotationHandler rotater;

		switch (rotation)
		{
			case NONE:
				rotater = new RotationHandlerIdentity();
				break;
			case CLOCKWISE_90:
				rotater = new RotationHandlerClockwise90();
				break;
			case CLOCKWISE_180:
				rotater = new RotationHandlerClockwise180();
				break;
			case COUNTERCLOCKWISE_90:
				rotater = new RotationHandlerCounterclockwise90();
				break;
			default:
				throw new IllegalArgumentException("Unsupported rotation");
		}

		final BlockPos dim = new BlockPos(origBlocks.getWidth(), origBlocks.getHeight(), origBlocks.getLength());
		final BlockPos rotatedDim = rotater.getDimensions(dim);

		final BlockDataContainer rotatedBlocks = new BlockDataContainer(origBlocks, rotatedDim.getX(), rotatedDim.getY(), rotatedDim.getZ());

		final BlockPos.MutableBlockPos rotatedPos = new BlockPos.MutableBlockPos();

		for (int z = 0; z < origBlocks.getLength(); z++)
		{
			for (int y = 0; y < origBlocks.getHeight(); y++)
			{
				for (int x = 0; x < origBlocks.getWidth(); x++)
				{
					rotater.applyTransformation(rotatedPos.setPos(x, y, z), dim);

					rotatedBlocks.copyBlockStateWithRotation(origBlocks, x, y, z, rotatedPos.getX(), rotatedPos.getY(), rotatedPos.getZ(), rotation);
				}
			}
		}

		for (final INode<IScheduleLayer, LayerLink> node : this.bakedScheduleLayers.getScheduleLayerNodes())
		{
			final IScheduleLayer layer = node.getData();

			if (layer.getStateRecord().getData().length == 0)
			{
				continue;
			}

			for (BlockPos pos : layer.getStateRecord().getRegion())
			{
				final IBlockState layerState = layer.getStateRecord().get(pos.getX(), pos.getY(), pos.getZ());

				for (IBlockState predicate : layer.getStateRecord().getData())
				{
					if (predicate == layerState)
					{
						rotater.applyTransformation(rotatedPos.setPos(pos), dim);

						if (layer.getOptions().getReplacesSolidBlocksVar().getData() || !BlockUtil.isSolid(rotatedBlocks.getBlockState(rotatedPos)))
						{
							rotatedBlocks.setBlockState(predicate, rotatedPos);
						}
					}
				}
			}
		}

		for (final BlockDataContainer.TileEntityEntry entry : origBlocks.getTileEntityEntries())
		{
			rotater.applyTransformation(rotatedPos.setPos(entry.pos), dim);

			rotatedBlocks.setTileEntity(entry.data.copy(), rotatedPos);
		}

		return rotatedBlocks;
	}

	private void updateScheduleRegions()
	{
		final Rotation rotation = this.creationData.getRotation();

		for (INode<IScheduleLayer, LayerLink> node : this.bakedScheduleLayers.getScheduleLayerNodes())
		{
			IScheduleLayer layer = node.getData();

			for (ScheduleRegion s : layer.getScheduleRecord().getSchedules(ScheduleRegion.class))
			{
				ScheduleRegion c = NBTHelper.clone(s);

				BlockPos min = transformedBlockPos(c.getBounds().getMin(), rotation);
				BlockPos max = transformedBlockPos(c.getBounds().getMax(), rotation);

				c.getBounds().setBounds(min, max);

				this.bakedScheduleRegions.add(c);
			}
		}
	}

	private void bake()
	{
		this.updateBlocks();
		this.updateBlueprintChildren();
		this.updateScheduleRegions();

		this.updatePostGenReplaceLayers();
	}

	public ChunkPos[] getOccupiedChunks(BlockPos offset)
	{
		return BlueprintUtil.getChunksInsideTemplate(this.bakedBlocks, this.bakedRegion.getMin().add(offset), Rotation.NONE);
	}

	public int getWidth()
	{
		return this.bakedRegion.getWidth();
	}

	public int getHeight()
	{
		return this.bakedRegion.getHeight();
	}

	public int getLength()
	{
		return this.bakedRegion.getLength();
	}

	public BlockDataContainer getBlockData()
	{
		return this.bakedBlocks;
	}

	public Region getBakedRegion()
	{
		return this.bakedRegion;
	}

	public List<BakedBlueprint> getBakedBlueprintChildren()
	{
		return this.bakedBlueprintChildren;
	}

	public BakedScheduleLayers getScheduleLayers()
	{
		return this.bakedScheduleLayers;
	}

	@Nullable
	public BlueprintDefinition getDefinition()
	{
		return this.definition;
	}

	private interface RotationHandler
	{
		BlockPos.MutableBlockPos applyTransformation(BlockPos.MutableBlockPos pos, BlockPos dimensions);

		BlockPos getDimensions(BlockPos dimensions);
	}

	private static class RotationHandlerClockwise90 implements RotationHandler
	{
		@Override
		public BlockPos.MutableBlockPos applyTransformation(BlockPos.MutableBlockPos pos, BlockPos dimensions)
		{
			int x = dimensions.getZ() - pos.getZ() - 1;
			int z = pos.getX();

			return pos.setPos(x, pos.getY(), z);
		}

		@Override
		public BlockPos getDimensions(BlockPos dimensions)
		{
			return new BlockPos(dimensions.getZ(), dimensions.getY(), dimensions.getX());
		}
	}

	private static class RotationHandlerCounterclockwise90 implements RotationHandler
	{
		@Override
		public BlockPos.MutableBlockPos applyTransformation(BlockPos.MutableBlockPos pos, BlockPos dimensions)
		{
			int x = pos.getZ();
			int z = dimensions.getX() - pos.getX() - 1;

			return pos.setPos(x, pos.getY(), z);
		}

		@Override
		public BlockPos getDimensions(BlockPos dimensions)
		{
			return new BlockPos(dimensions.getZ(), dimensions.getY(), dimensions.getX());
		}
	}

	private static class RotationHandlerClockwise180 implements RotationHandler
	{

		@Override
		public BlockPos.MutableBlockPos applyTransformation(BlockPos.MutableBlockPos pos, BlockPos dimensions)
		{
			int x = dimensions.getX() - pos.getX() - 1;
			int z = dimensions.getZ() - pos.getZ() - 1;

			return pos.setPos(x, pos.getY(), z);
		}

		@Override
		public BlockPos getDimensions(BlockPos dimensions)
		{
			return dimensions;
		}
	}

	private static class RotationHandlerIdentity implements RotationHandler
	{
		@Override
		public BlockPos.MutableBlockPos applyTransformation(BlockPos.MutableBlockPos pos, BlockPos dimensions)
		{
			return pos;
		}

		@Override
		public BlockPos getDimensions(BlockPos dimensions)
		{
			return dimensions;
		}
	}
}
