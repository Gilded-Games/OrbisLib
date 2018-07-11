package com.gildedgames.orbis_api.core;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.block.*;
import com.gildedgames.orbis_api.core.exceptions.OrbisMissingDataException;
import com.gildedgames.orbis_api.core.exceptions.OrbisMissingProjectException;
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
import com.gildedgames.orbis_api.data.management.IDataIdentifier;
import com.gildedgames.orbis_api.data.region.IDimensions;
import com.gildedgames.orbis_api.data.region.Region;
import com.gildedgames.orbis_api.data.schedules.IScheduleLayer;
import com.gildedgames.orbis_api.data.schedules.PostGenReplaceLayer;
import com.gildedgames.orbis_api.data.schedules.ScheduleRegion;
import com.gildedgames.orbis_api.processing.DataPrimer;
import com.gildedgames.orbis_api.util.OrbisTuple;
import com.gildedgames.orbis_api.util.RegionHelp;
import com.gildedgames.orbis_api.util.RotationHelp;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.gildedgames.orbis_api.util.mc.NBTHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BakedBlueprint implements IDimensions
{
	private BlockDataChunk[] chunks;

	private Map<ChunkPos, List<PlacedEntity>> placedEntities = Maps.newHashMap();

	private List<ScheduleRegion> bakedScheduleRegions = Lists.newArrayList();

	private List<INode<IScheduleLayer, LayerLink>> bakedScheduleLayerNodes = Lists.newArrayList();

	private BlueprintData blueprintData;

	private NodeTree<BlueprintVariable, NBT> bakedBlueprintVariables;

	private ICreationData<?> data;

	private boolean hasBaked;

	private BlockDataContainer rawDataContainer;

	private int width, height, length;

	private BlockPos bakedMin;

	private BakedBlueprint()
	{

	}

	public BakedBlueprint(BlueprintData blueprintData, ICreationData<?> data)
	{
		this.blueprintData = blueprintData;
		this.data = data;
	}

	public BlockPos getBakedMin()
	{
		return this.bakedMin;
	}

	@Override
	public BakedBlueprint clone()
	{
		BakedBlueprint clone = new BakedBlueprint();

		NBTTagCompound tag = new NBTTagCompound();

		this.write(tag);
		clone.read(tag);

		return clone;
	}

	public ICreationData<?> getCreationData()
	{
		return this.data;
	}

	public List<ScheduleRegion> getBakedScheduleRegions()
	{
		return this.bakedScheduleRegions;
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

		boolean resolved = condition.resolve(this.data.getRandom());

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

	private void bakeScheduleLayers()
	{
		this.bakedBlueprintVariables = this.blueprintData.getVariableTree().deepClone();

		List<INode<IScheduleLayer, LayerLink>> layers = Lists.newArrayList();

		List<INode<IScheduleLayer, LayerLink>> nodes = Lists.newArrayList(this.blueprintData.getScheduleLayerTree().getNodes());

		Collections.shuffle(nodes, this.data.getRandom());

		for (INode<IScheduleLayer, LayerLink> node : nodes)
		{
			IScheduleLayer layer = node.getData();

			if (layer.getConditionNodeTree().isEmpty())
			{
				layers.add(node);

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

					action.getData().resolve(this.data.getRandom());
				}
			}
			else if (layer.getConditionNodeTree().getProminentRoot() != null)
			{
				if (!this.resolveChildrenConditions(layer.getConditionNodeTree().getProminentRoot()))
				{
					continue;
				}

				layers.add(node);

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

					action.getData().resolve(this.data.getRandom());
				}
			}
		}

		List<INode<IScheduleLayer, LayerLink>> finalLinkResolvedNodes = Lists.newArrayList();

		for (INode<IScheduleLayer, LayerLink> node : layers)
		{
			// TODO: MAKE SURE PARENTS/LINKS BETWEEN NODES CANNOT CREATE A CLOSED LOOP/INFINITE LOOP
			if (!this.areParentsResolved(node, layers))
			{
				continue;
			}

			finalLinkResolvedNodes.add(node);
		}

		this.bakedScheduleLayerNodes = finalLinkResolvedNodes;
	}

	private boolean areParentsResolved(INode<IScheduleLayer, LayerLink> node, List<INode<IScheduleLayer, LayerLink>> resolvedNodes)
	{
		for (INode<IScheduleLayer, LayerLink> parent : node.getTree().get(node.getParentsIds()))
		{
			if (!resolvedNodes.contains(parent) || !this.areParentsResolved(parent, resolvedNodes))
			{
				return false;
			}
		}

		return true;
	}

	private void bakeScheduleRegions()
	{
		for (INode<IScheduleLayer, LayerLink> node : this.bakedScheduleLayerNodes)
		{
			IScheduleLayer layer = node.getData();

			layer.getScheduleRecord().getSchedules(ScheduleRegion.class).forEach(s ->
			{
				ScheduleRegion c = NBTHelper.clone(s);

				RegionHelp.translate(c.getBounds(), this.data.getPos().getX(), this.data.getPos().getY(),
						this.data.getPos().getZ());

				this.bakedScheduleRegions.add(c);
			});
		}
	}

	private void bakeEntities()
	{
		for (INode<IScheduleLayer, LayerLink> node : this.bakedScheduleLayerNodes)
		{
			IScheduleLayer layer = node.getData();

			for (ScheduleRegion s : layer.getScheduleRecord().getSchedules(ScheduleRegion.class))
			{
				for (int i = 0; i < s.getSpawnEggsInventory().getSizeInventory(); i++)
				{
					ItemStack stack = s.getSpawnEggsInventory().getStackInSlot(i);

					if (stack.getItem() instanceof ItemMonsterPlacer)
					{
						BlockPos pos = this.data.getPos().add(s.getBounds().getMin());
						pos.add(this.data.getRandom().nextInt(s.getBounds().getWidth()), 0,
								this.data.getRandom().nextInt(s.getBounds().getHeight()));

						PlacedEntity placedEntity = new PlacedEntity(stack, pos);

						ChunkPos p = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);

						if (!this.placedEntities.containsKey(p))
						{
							this.placedEntities.put(p, Lists.newArrayList());
						}

						this.placedEntities.get(p).add(placedEntity);
					}
				}
			}
		}
	}

	private void bakeChunks()
	{
		this.rawDataContainer = this.blueprintData.getBlockDataContainer().clone();

		for (INode<IScheduleLayer, LayerLink> node : this.bakedScheduleLayerNodes)
		{
			IScheduleLayer layer = node.getData();

			for (BlockFilter filter : layer.getFilterRecord().getData())
			{
				filter.apply(layer.getFilterRecord().getPositions(filter, BlockPos.ORIGIN), this.rawDataContainer, this.data, layer.getOptions());
			}
		}

		final ChunkPos[] chunksOccupied = BlueprintUtil.getChunksInsideTemplate(this.rawDataContainer, this.data);

		this.chunks = new BlockDataChunk[chunksOccupied.length];

		BlockPos min = this.data.getPos();
		BlockPos max = new BlockPos(min.getX() + this.rawDataContainer.getWidth() - 1, min.getY() + this.rawDataContainer.getHeight() - 1,
				min.getZ() + this.rawDataContainer.getLength() - 1);

		Region boundsBeforeRotateAtOrigin = new Region(new BlockPos(0, 0, 0), new BlockPos(this.rawDataContainer.getWidth() - 1,
				this.rawDataContainer.getHeight() - 1, this.rawDataContainer.getLength() - 1));

		for (PostGenReplaceLayer postGenReplaceLayer : this.blueprintData.getPostGenReplaceLayers().values())
		{
			if (postGenReplaceLayer.getReplaced().isEmpty() || postGenReplaceLayer.getRequired().isEmpty())
			{
				continue;
			}

			final BlockFilterLayer layer = new BlockFilterLayer();

			layer.setFilterType(BlockFilterType.ONLY);

			layer.setRequiredBlocks(BlockFilterHelper.convertToBlockData(BlockFilterHelper.getBlocksFromStack(postGenReplaceLayer.getRequired())));
			layer.setReplacementBlocks(BlockFilterHelper.convertToBlockData(BlockFilterHelper.getBlocksFromStack(postGenReplaceLayer.getReplaced())));

			BlockFilter filter = new BlockFilter(layer);

			filter.apply(boundsBeforeRotateAtOrigin.createShapeData(), this.rawDataContainer, this.data, postGenReplaceLayer.getOptions());
		}

		this.chunkUpBlocks(min, max, chunksOccupied, boundsBeforeRotateAtOrigin, this.data.getRotation());
	}

	private void chunkUpBlocks(BlockPos min, BlockPos max, ChunkPos[] chunksOccupied, Region boundsBeforeRotateAtOrigin, Rotation rotation)
	{
		final int rotAmount = Math.abs(RotationHelp.getRotationAmount(rotation, Rotation.NONE));

		if (rotAmount != 0)
		{
			Region rotatedRegion = (Region) RotationHelp.rotate(new Region(min, max), this.data.getRotation());

			this.width = rotatedRegion.getWidth();
			this.height = rotatedRegion.getHeight();
			this.length = rotatedRegion.getLength();

			BlockPos m = new BlockPos(Math.min(rotatedRegion.getMin().getX(), rotatedRegion.getMax().getX()),
					Math.min(rotatedRegion.getMin().getY(), rotatedRegion.getMax().getY()),
					Math.min(rotatedRegion.getMin().getZ(), rotatedRegion.getMax().getZ()));

			this.bakedMin = m;

			int startChunkX = m.getX() >> 4;
			int startChunkZ = m.getZ() >> 4;

			int xDif = m.getX() % 16;
			int zDif = m.getZ() % 16;

			if (xDif < 0)
			{
				xDif = 16 - Math.abs(xDif);
			}

			if (zDif < 0)
			{
				zDif = 16 - Math.abs(zDif);
			}

			for (final OrbisTuple<BlockPos.MutableBlockPos, BlockPos.MutableBlockPos> tuple : RotationHelp
					.getAllInBoxRotated(min, max,
							this.data.getRotation(), null))
			{
				BlockPos beforeRot = tuple.getFirst();
				BlockPos rotated = tuple.getSecond();

				final int chunkX = ((rotated.getX()) >> 4) - startChunkX;
				final int chunkZ = ((rotated.getZ()) >> 4) - startChunkZ;

				int index = 0;

				for (int i = 0; i < chunksOccupied.length; i++)
				{
					final ChunkPos p = chunksOccupied[i];

					if (p.x - startChunkX == chunkX && p.z - startChunkZ == chunkZ)
					{
						if (this.chunks[i] == null)
						{
							this.chunks[i] = new BlockDataChunk(p, new BlockDataContainer(16, this.rawDataContainer.getHeight(), 16));
						}

						index = i;
						break;
					}
				}

				final BlockDataChunk chunk = this.chunks[index];

				if (chunk != null)
				{
					try
					{
						int xIndex = (rotated.getX() - m.getX() + xDif) % 16;
						int zIndex = (rotated.getZ() - m.getZ() + zDif) % 16;

						chunk.getContainer()
								.copyBlockFrom(this.rawDataContainer, beforeRot.getX() - min.getX(), beforeRot.getY() - min.getY(),
										beforeRot.getZ() - min.getZ(),
										xIndex, rotated.getY() - m.getY(),
										zIndex);

						IBlockState original = chunk.getContainer().getBlockState(xIndex, rotated.getY() - m.getY(), zIndex);
						chunk.getContainer().setBlockState(original.withRotation(this.data.getRotation()), xIndex, rotated.getY() - m.getY(), zIndex);
					}
					catch (ArrayIndexOutOfBoundsException e)
					{
						OrbisAPI.LOGGER.error(e);
					}
				}
			}
		}
		else
		{
			this.width = boundsBeforeRotateAtOrigin.getWidth();
			this.height = boundsBeforeRotateAtOrigin.getHeight();
			this.length = boundsBeforeRotateAtOrigin.getLength();

			int startChunkX = min.getX() >> 4;
			int startChunkZ = min.getZ() >> 4;

			int xDif = min.getX() % 16;
			int zDif = min.getZ() % 16;

			if (xDif < 0)
			{
				xDif = 16 - Math.abs(xDif);
			}

			if (zDif < 0)
			{
				zDif = 16 - Math.abs(zDif);
			}

			this.bakedMin = min;

			for (final BlockPos.MutableBlockPos iterPos : boundsBeforeRotateAtOrigin.getMutableBlockPosInRegion())
			{
				final int chunkX = ((min.getX() + iterPos.getX()) >> 4) - startChunkX;
				final int chunkZ = ((min.getZ() + iterPos.getZ()) >> 4) - startChunkZ;

				int index = 0;

				for (int i = 0; i < chunksOccupied.length; i++)
				{
					final ChunkPos p = chunksOccupied[i];

					if (p.x - startChunkX == chunkX && p.z - startChunkZ == chunkZ)
					{
						if (this.chunks[i] == null)
						{
							this.chunks[i] = new BlockDataChunk(p, new BlockDataContainerDefaultVoid(16, this.rawDataContainer.getHeight(), 16));
						}

						index = i;
						break;
					}
				}

				final BlockDataChunk chunk = this.chunks[index];

				if (chunk != null)
				{
					try
					{

						chunk.getContainer()
								.copyBlockFrom(this.rawDataContainer, iterPos.getX(), iterPos.getY(), iterPos.getZ(), (iterPos.getX() + xDif) % 16,
										iterPos.getY(),
										(iterPos.getZ() + zDif) % 16);
					}
					catch (ArrayIndexOutOfBoundsException e)
					{
						OrbisAPI.LOGGER.error(e);
					}
				}
			}
		}
	}

	public void spawnEntitiesInChunk(DataPrimer primer, ChunkPos chunkPos)
	{
		if (this.getPlacedEntities().containsKey(chunkPos))
		{
			List<PlacedEntity> placed = this.getPlacedEntities().get(chunkPos);

			for (final PlacedEntity e : placed)
			{
				e.spawn(primer);
			}

			this.getPlacedEntities().remove(chunkPos);
		}
	}

	public void bake()
	{
		if (!this.hasBaked)
		{
			this.bakeScheduleLayers();
			this.bakeChunks();
			this.bakeEntities();
			this.bakeScheduleRegions();

			this.hasBaked = true;
		}
	}

	public void rebake(BlockPos pos)
	{
		if (!this.hasBaked)
		{
			this.data.pos(pos);

			this.bake();

			return;
		}

		int relocateX = -(this.getBakedMin().getX() - pos.getX());
		int relocateY = -(this.getBakedMin().getY() - pos.getY());
		int relocateZ = -(this.getBakedMin().getZ() - pos.getZ());

		// REBAKE CHUNKS
		this.rawDataContainer = new BlockDataContainer(this.width, this.height, this.length);

		BlockPos bakedMin = this.bakedMin;
		BlockPos bakedMax = bakedMin.add(this.width - 1, this.height - 1, this.length - 1);

		for (BlockDataChunk chunk : this.chunks)
		{
			BlockDataContainer container = chunk.getContainer();

			final Region region = new Region(new BlockPos(0, 0, 0),
					new BlockPos(container.getWidth() - 1, container.getHeight() - 1, container.getLength() - 1));

			BlockPos chunkMin = chunk.getPos().getBlock(0, this.bakedMin.getY(), 0);

			for (final BlockPos.MutableBlockPos p : region.getMutableBlockPosInRegion())
			{
				int origX = p.getX();
				int origY = p.getY();
				int origZ = p.getZ();

				int newX = origX + chunkMin.getX();
				int newY = origY + chunkMin.getY();
				int newZ = origZ + chunkMin.getZ();

				if (newX < bakedMin.getX() || newY < bakedMin.getY() || newZ < bakedMin.getZ() || newX > bakedMax.getX()
						|| newY > bakedMax.getY()
						|| newZ > bakedMax.getZ())
				{
					continue;
				}

				final IBlockState state = container.getBlockState(origX, origY, origZ);

				if (state == null)
				{
					continue;
				}

				try
				{
					this.rawDataContainer.setBlockState(state, newX - bakedMin.getX(), newY - bakedMin.getY(), newZ - bakedMin.getZ());
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					OrbisAPI.LOGGER.info(e);
				}
			}
		}

		/*this.chunks = new BlockDataChunk[1];

		this.chunks[0] = new BlockDataChunk(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4), this.rawDataContainer);*/

		this.data.pos(pos);

		BlockPos bakedMinRelocated = bakedMin.add(relocateX, relocateY, relocateZ);

		final ChunkPos[] chunksOccupied = BlueprintUtil
				.getChunksInsideTemplate(this.rawDataContainer, new CreationData().pos(bakedMinRelocated).rotation(Rotation.NONE));

		this.chunks = new BlockDataChunk[chunksOccupied.length];

		Region boundsBeforeRotateAtOrigin = new Region(new BlockPos(0, 0, 0), new BlockPos(this.rawDataContainer.getWidth() - 1,
				this.rawDataContainer.getHeight() - 1, this.rawDataContainer.getLength() - 1));

		this.chunkUpBlocks(bakedMinRelocated, bakedMax.add(relocateX, relocateY, relocateZ), chunksOccupied,
				boundsBeforeRotateAtOrigin, Rotation.NONE);

		// RELOCATE PLACED ENTITIES
		Map<ChunkPos, List<PlacedEntity>> rebakedPlacedEntities = Maps.newHashMap();

		for (Map.Entry<ChunkPos, List<PlacedEntity>> entry : this.placedEntities.entrySet())
		{
			ChunkPos chunkPos = entry.getKey();
			List<PlacedEntity> entities = entry.getValue();

			int newPosX = (chunkPos.x * 16) + relocateX;
			int newPosZ = (chunkPos.z * 16) + relocateZ;

			chunkPos = new ChunkPos(newPosX >> 4, newPosZ >> 4);

			for (PlacedEntity entity : entities)
			{
				entity.setPos(entity.getPos().add(relocateX, relocateY, relocateZ));
			}

			rebakedPlacedEntities.put(chunkPos, entities);
		}

		this.placedEntities.clear();
		this.placedEntities.putAll(rebakedPlacedEntities);

		List<Runnable> relocates = Lists.newArrayList();

		// RELOCATE BAKED SCHEDULE REGIONS
		for (ScheduleRegion scheduleRegion : this.bakedScheduleRegions)
		{
			RegionHelp.translate(scheduleRegion.getBounds(), relocateX, relocateY, relocateZ);

			relocates.add(() -> this.bakedScheduleRegions.add(scheduleRegion));
		}

		this.bakedScheduleRegions.clear();

		relocates.forEach(Runnable::run);
	}

	public Map<ChunkPos, List<PlacedEntity>> getPlacedEntities()
	{
		return this.placedEntities;
	}

	public BlockDataChunk[] getDataChunks()
	{
		return this.chunks;
	}

	@Override
	public int getWidth()
	{
		return this.width;
	}

	@Override
	public int getHeight()
	{
		return this.height;
	}

	@Override
	public int getLength()
	{
		return this.length;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		tag.setInteger("width", this.width);
		tag.setInteger("height", this.height);
		tag.setInteger("length", this.length);

		funnel.setArray("chunks", this.chunks);
		funnel.setList("bakedScheduleRegions", this.bakedScheduleRegions);
		funnel.setList("bakedScheduleLayerNodes", this.bakedScheduleLayerNodes);
		funnel.setMap("placedEntities", this.placedEntities, NBTFunnel.CHUNK_POS_SETTER, NBTFunnel.listSetter());

		funnel.set("data", this.getCreationData());
		funnel.set("bakedBlueprintVariables", this.bakedBlueprintVariables);
		funnel.set("rawDataContainer", this.rawDataContainer);

		tag.setBoolean("hasBaked", this.hasBaked);

		funnel.setPos("bakedMin", this.bakedMin);

		funnel.set("blueprintId", this.blueprintData.getMetadata().getIdentifier());
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.width = tag.getInteger("width");
		this.height = tag.getInteger("height");
		this.length = tag.getInteger("length");

		this.chunks = funnel.getArray("chunks", BlockDataChunk.class);
		this.bakedScheduleRegions = funnel.getList("bakedScheduleRegions");
		this.bakedScheduleLayerNodes = funnel.getList("bakedScheduleLayerNodes");
		this.placedEntities = funnel.getMap("placedEntities", NBTFunnel.CHUNK_POS_GETTER, NBTFunnel.listGetter());

		this.data = funnel.get("data");
		this.bakedBlueprintVariables = funnel.get("bakedBlueprintVariables");
		this.rawDataContainer = funnel.get("rawDataContainer");

		this.hasBaked = tag.getBoolean("hasBaked");

		this.bakedMin = funnel.getPos("bakedMin");

		try
		{
			final IDataIdentifier id = funnel.get("blueprintId");

			this.blueprintData = OrbisAPI.services().getProjectManager().findData(id);
		}
		catch (final OrbisMissingDataException | OrbisMissingProjectException e)
		{
			OrbisAPI.LOGGER.error("Missing in " + this.getClass().getName() + " : ", e);
		}
	}
}
