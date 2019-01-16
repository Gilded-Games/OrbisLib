package com.gildedgames.orbis_api.core.baking;

import com.gildedgames.orbis_api.block.BlockDataContainer;
import com.gildedgames.orbis_api.core.BlueprintDefinition;
import com.gildedgames.orbis_api.core.ICreationData;
import com.gildedgames.orbis_api.core.tree.ConditionLink;
import com.gildedgames.orbis_api.core.tree.INode;
import com.gildedgames.orbis_api.core.tree.LayerLink;
import com.gildedgames.orbis_api.core.tree.NodeTree;
import com.gildedgames.orbis_api.core.util.BlueprintUtil;
import com.gildedgames.orbis_api.core.variables.conditions.IGuiCondition;
import com.gildedgames.orbis_api.core.variables.post_resolve_actions.IPostResolveAction;
import com.gildedgames.orbis_api.data.IDataUser;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.blueprint.BlueprintVariable;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.data.region.Region;
import com.gildedgames.orbis_api.data.schedules.IPosActionBaker;
import com.gildedgames.orbis_api.data.schedules.IScheduleLayer;
import com.gildedgames.orbis_api.data.schedules.PostGenReplaceLayer;
import com.gildedgames.orbis_api.data.schedules.ScheduleRegion;
import com.gildedgames.orbis_api.util.RotationHelp;
import com.gildedgames.orbis_api.util.mc.BlockUtil;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.gildedgames.orbis_api.util.mc.NBTHelper;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BakedBlueprint
{
	private List<ScheduleRegion> bakedScheduleRegions = Lists.newArrayList();

	private LinkedList<INode<IScheduleLayer, LayerLink>> bakedScheduleLayerNodes = Lists.newLinkedList();

	private BlockDataContainer bakedBlocks;

	private Region bakedRegion;

	private BlueprintData blueprintData;

	private NodeTree<BlueprintVariable, NBT> bakedBlueprintVariables;

	private ICreationData<?> creationData;

	private BlueprintDefinition definition;

	public BakedBlueprint(BlueprintDefinition definition, ICreationData<?> creationData)
	{
		this.definition = definition;
		this.blueprintData = definition.getData();

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

	private boolean resolveChildrenConditions(INode<IGuiCondition, ConditionLink> parent)
	{
		IGuiCondition condition = parent.getData();

		if (condition instanceof IDataUser)
		{
			IDataUser user = (IDataUser) condition;

			if (user.getDataIdentifier().equals("blueprintVariables"))
			{
				user.setUsedData(this.bakedBlueprintVariables);
			}
		}

		boolean resolved = condition.resolve(this.creationData.getRandom());

		boolean result = false;

		if (resolved)
		{
			result = true;
		}

		for (INode<IGuiCondition, ConditionLink> child : parent.getTree().get(parent.getChildrenIds()))
		{
			//TODO: Implement actual condition link logic - AND and OR logic. Currently just goes through all checks if all are resolved.
			if (this.resolveChildrenConditions(child))
			{
				result = true;
			}
		}

		return result;
	}

	private void fetchValidLayers(INode<IScheduleLayer, LayerLink> root, List<INode<IScheduleLayer, LayerLink>> addValidTo,
			List<INode<IScheduleLayer, LayerLink>> visited)
	{
		if (root == null)
		{
			return;
		}

		if (visited.contains(root))
		{
			return;
		}

		visited.add(root);

		for (INode<IScheduleLayer, LayerLink> parent : root.getTree().get(root.getParentsIds()))
		{
			if (!visited.contains(parent))
			{
				this.fetchValidLayers(parent, addValidTo, visited);
			}

			if (!addValidTo.contains(parent))
			{
				return;
			}
		}

		IScheduleLayer layer = root.getData();

		if (layer.getConditionNodeTree().isEmpty())
		{
			addValidTo.add(root);

			for (INode<IPostResolveAction, NBT> action : layer.getPostResolveActionNodeTree().getNodes())
			{
				if (action.getData() instanceof IDataUser)
				{
					IDataUser user = (IDataUser) action.getData();

					if (user.getDataIdentifier().equals("blueprintVariables"))
					{
						user.setUsedData(this.bakedBlueprintVariables);
					}
				}

				action.getData().resolve(this.creationData.getRandom());
			}
		}
		else if (layer.getConditionNodeTree().getRootNode() != null)
		{
			if (!this.resolveChildrenConditions(layer.getConditionNodeTree().getRootNode()))
			{
				return;
			}

			addValidTo.add(root);

			for (INode<IPostResolveAction, NBT> action : layer.getPostResolveActionNodeTree().getNodes())
			{
				if (action.getData() instanceof IDataUser)
				{
					IDataUser user = (IDataUser) action.getData();

					if (user.getDataIdentifier().equals("blueprintVariables"))
					{
						user.setUsedData(this.bakedBlueprintVariables);
					}
				}

				action.getData().resolve(this.creationData.getRandom());
			}
		}

		List<INode<IScheduleLayer, LayerLink>> children = Lists.newArrayList(root.getTree().get(root.getChildrenIds()));

		Collections.shuffle(children, this.creationData.getRandom());

		for (INode<IScheduleLayer, LayerLink> child : children)
		{
			this.fetchValidLayers(child, addValidTo, visited);
		}
	}

	private void refresh()
	{
		this.bakedBlueprintVariables = this.blueprintData.getVariableTree().deepClone();

		this.bakedScheduleLayerNodes.clear();

		this.fetchValidLayers(this.blueprintData.getScheduleLayerTree().getRootNode(), this.bakedScheduleLayerNodes, Lists.newArrayList());
	}

	private void updateScheduleRegions()
	{
		final Rotation rotation = this.creationData.getRotation();

		for (INode<IScheduleLayer, LayerLink> node : this.bakedScheduleLayerNodes)
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

	public List<IBakedPosAction> getBakedPositionActions()
	{
		Region boundsBeforeRotateAtOrigin = new Region(new BlockPos(0, 0, 0), new BlockPos(this.blueprintData.getWidth() - 1,
				this.blueprintData.getHeight() - 1, this.blueprintData.getLength() - 1));

		for (INode<IScheduleLayer, LayerLink> node : this.bakedScheduleLayerNodes)
		{
			IScheduleLayer layer = node.getData();

			for (ScheduleRegion s : layer.getScheduleRecord().getSchedules(ScheduleRegion.class))
			{
				IRegion rotatedBounds = RotationHelp.rotate(s.getBounds(), boundsBeforeRotateAtOrigin, this.creationData.getRotation());

				if (!s.getConditionNodeTree().isEmpty() && !this.resolveChildrenConditions(s.getConditionNodeTree().getRootNode()))
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

	private void updateLayers()
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
		Rotation rotation = this.creationData.getRotation();

		BlockDataContainer blocks = this.blueprintData.getBlockDataContainer().clone();

		// TODO: Perform only once
		for (INode<IScheduleLayer, LayerLink> node : this.bakedScheduleLayerNodes)
		{
			IScheduleLayer layer = node.getData();

			if (layer.getStateRecord().getData().length == 0)
			{
				continue;
			}

			for (BlockPos.MutableBlockPos pos : layer.getStateRecord().getRegion())
			{
				IBlockState layerState = layer.getStateRecord().get(pos.getX(), pos.getY(), pos.getZ());

				for (IBlockState predicate : layer.getStateRecord().getData())
				{
					if (predicate == layerState)
					{
						if (layer.getOptions().getReplacesSolidBlocksVar().getData() || !BlockUtil.isSolid(blocks.getBlockState(pos)))
						{
							blocks.setBlockState(predicate, pos);
						}
					}
				}
			}
		}

		switch (rotation)
		{
			case CLOCKWISE_90:
				this.bakedBlocks = blocks.rotateClockwise90();
				break;
			case COUNTERCLOCKWISE_90:
				this.bakedBlocks = blocks.rotateCounterclockwise90();
				break;
			case CLOCKWISE_180:
				this.bakedBlocks = blocks.rotateClockwise180();
				break;
			default:
				this.bakedBlocks = blocks;
				break;
		}

		BlockPos dimensions = new BlockPos(blocks.getWidth() - 1, blocks.getHeight() - 1, blocks.getLength() - 1);

		this.bakedRegion = new Region(BlockPos.ORIGIN, transformedBlockPos(dimensions, rotation));
		this.bakedRegion.add(this.creationData.getPos());
	}

	private static BlockPos transformedBlockPos(BlockPos pos, Rotation rotation)
	{
		switch (rotation)
		{
			case COUNTERCLOCKWISE_90:
				return new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
			case CLOCKWISE_90:
				return new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
			case CLOCKWISE_180:
				return new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
			default:
				return new BlockPos(pos.getX(), pos.getY(), pos.getZ());
		}
	}

	private void bake()
	{
		this.refresh();

		this.updateBlocks();
		this.updateScheduleRegions();

		this.updateLayers();
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

	public BlueprintDefinition getDefinition()
	{
		return this.definition;
	}
}
