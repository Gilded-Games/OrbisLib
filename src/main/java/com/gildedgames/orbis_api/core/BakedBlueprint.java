package com.gildedgames.orbis_api.core;

import com.gildedgames.orbis_api.block.*;
import com.gildedgames.orbis_api.core.tree.ConditionLink;
import com.gildedgames.orbis_api.core.tree.INode;
import com.gildedgames.orbis_api.core.tree.LayerLink;
import com.gildedgames.orbis_api.core.util.BlueprintUtil;
import com.gildedgames.orbis_api.core.variables.conditions.IGuiCondition;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.region.Region;
import com.gildedgames.orbis_api.data.schedules.IScheduleLayer;
import com.gildedgames.orbis_api.data.schedules.PostGenReplaceLayer;
import com.gildedgames.orbis_api.data.schedules.ScheduleRegion;
import com.gildedgames.orbis_api.processing.DataPrimer;
import com.gildedgames.orbis_api.util.OrbisTuple;
import com.gildedgames.orbis_api.util.RegionHelp;
import com.gildedgames.orbis_api.util.RotationHelp;
import com.gildedgames.orbis_api.util.mc.NBTHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.List;
import java.util.Map;

public class BakedBlueprint
{
	private BlockDataChunk[] chunks;

	private Map<ChunkPos, List<PlacedEntity>> placedEntities = Maps.newHashMap();

	private List<ScheduleRegion> bakedScheduleRegions = Lists.newArrayList();

	private List<INode<IScheduleLayer, LayerLink>> bakedScheduleLayerNodes = Lists.newArrayList();

	private BlueprintData blueprintData;

	private ICreationData<?> data;

	private boolean hasBaked;

	public BakedBlueprint(BlueprintData blueprintData, ICreationData<?> data)
	{
		this.blueprintData = blueprintData;
		this.data = data;
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

	private void bakeScheduleLayers()
	{
		List<INode<IScheduleLayer, LayerLink>> layers = Lists.newArrayList();

		outer:
		for (INode<IScheduleLayer, LayerLink> node : this.blueprintData.getScheduleLayerTree().getNodes())
		{
			IScheduleLayer layer = node.getData();

			if (layer.getConditionNodeTree().isEmpty())
			{
				layers.add(node);
			}
			else if (layer.getConditionNodeTree().getProminentRoot() != null)
			{
				List<INode<IGuiCondition, ConditionLink>> allChildren = Lists.newArrayList();

				allChildren.add(layer.getConditionNodeTree().getProminentRoot());

				layer.getConditionNodeTree().getProminentRoot().fetchAllChildren(allChildren);

				for (INode<IGuiCondition, ConditionLink> conditionNode : allChildren)
				{
					IGuiCondition condition = conditionNode.getData();

					boolean resolved = condition.resolve(this.data.getRandom());

					//TODO: Implement actual condition link logic - AND and OR logic. Currently just goes through all checks if all are resolved.

					if (!resolved)
					{
						continue outer;
					}
				}

				layers.add(node);
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

						ChunkPos p = new ChunkPos(this.data.getPos().getX() >> 4, this.data.getPos().getZ() >> 4);

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
		final BlockDataContainer blocks = this.blueprintData.getBlockDataContainer().clone();

		for (INode<IScheduleLayer, LayerLink> node : this.bakedScheduleLayerNodes)
		{
			IScheduleLayer layer = node.getData();

			for (BlockFilter filter : layer.getFilterRecord().getData())
			{
				filter.apply(layer.getFilterRecord().getPositions(filter, BlockPos.ORIGIN), blocks, this.data, layer.getOptions());
			}
		}

		final ChunkPos[] chunksOccupied = BlueprintUtil.getChunksInsideTemplate(this.blueprintData, this.data);

		this.chunks = new BlockDataChunk[chunksOccupied.length];

		final BlockPos min = this.data.getPos();
		BlockPos max = new BlockPos(min.getX() + blocks.getWidth() - 1, min.getY() + blocks.getHeight() - 1,
				min.getZ() + blocks.getLength() - 1);

		final Region region = new Region(new BlockPos(0, 0, 0), new BlockPos(blocks.getWidth() - 1, blocks.getHeight() - 1, blocks.getLength() - 1));

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

			filter.apply(region.createShapeData(), blocks, this.data, postGenReplaceLayer.getOptions());
		}

		final int startChunkX = min.getX() >> 4;
		final int startChunkZ = min.getZ() >> 4;

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

		final int rotAmount = Math.abs(RotationHelp.getRotationAmount(this.data.getRotation(), Rotation.NONE));

		if (rotAmount != 0)
		{
			for (final OrbisTuple<BlockPos.MutableBlockPos, BlockPos.MutableBlockPos> tuple : RotationHelp
					.getAllInBoxRotated(min, max, this.data.getRotation(), null))
			{
				final BlockPos.MutableBlockPos beforeRot = tuple.getFirst();
				BlockPos.MutableBlockPos rotated = tuple.getSecond();

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
							this.chunks[i] = new BlockDataChunk(p, new BlockDataContainer(16, blocks.getHeight(), 16));
						}

						index = i;
						break;
					}
				}

				final BlockDataChunk chunk = this.chunks[index];

				if (chunk != null)
				{
					chunk.getContainer().copyBlockFrom(blocks, beforeRot.getX() - min.getX(), beforeRot.getY() - min.getY(), beforeRot.getZ() - min.getZ(),
							(rotated.getX() + xDif) % 16, rotated.getY() - min.getY(), (rotated.getZ() + zDif) % 16);
				}
			}
		}
		else
		{
			for (final BlockPos.MutableBlockPos iterPos : region.getMutableBlockPosInRegion())
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
							this.chunks[i] = new BlockDataChunk(p, new BlockDataContainer(16, blocks.getHeight(), 16));
						}

						index = i;
						break;
					}
				}

				final BlockDataChunk chunk = this.chunks[index];

				if (chunk != null)
				{
					chunk.getContainer()
							.copyBlockFrom(blocks, iterPos.getX(), iterPos.getY(), iterPos.getZ(), (iterPos.getX() + xDif) % 16, iterPos.getY(),
									(iterPos.getZ() + zDif) % 16);
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

	public Map<ChunkPos, List<PlacedEntity>> getPlacedEntities()
	{
		return this.placedEntities;
	}

	public BlockDataChunk[] getDataChunks()
	{
		return this.chunks;
	}

}
