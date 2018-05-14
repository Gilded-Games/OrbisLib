package com.gildedgames.orbis_api.processing;

import com.gildedgames.orbis_api.block.BlockDataContainer;
import com.gildedgames.orbis_api.block.BlockFilter;
import com.gildedgames.orbis_api.block.BlockInstance;
import com.gildedgames.orbis_api.core.*;
import com.gildedgames.orbis_api.core.util.BlueprintUtil;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.blueprint.BlueprintDataPalette;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.data.region.Region;
import com.gildedgames.orbis_api.data.schedules.IPositionRecord;
import com.gildedgames.orbis_api.data.schedules.ISchedule;
import com.gildedgames.orbis_api.data.schedules.IScheduleLayer;
import com.gildedgames.orbis_api.data.shapes.IterablePosShape;
import com.gildedgames.orbis_api.util.OrbisTuple;
import com.gildedgames.orbis_api.util.RotationHelp;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class DataPrimer
{

	private final IBlockAccessExtended access;

	public DataPrimer(final IBlockAccessExtended primer)
	{
		this.access = primer;
	}

	@Nullable
	public World getWorld()
	{
		return this.access.getWorld();
	}

	public IBlockAccessExtended getAccess()
	{
		return this.access;
	}

	public void spawn(Entity entity)
	{
		this.access.spawnEntity(entity);
	}

	public void createChunk(final ChunkPos chunk, final World world, final BlueprintData def, final ICreationData<?> data)
	{
		if (def.getBlockDataContainer().getWidth() >= 1 && def.getBlockDataContainer().getHeight() >= 1 && def.getBlockDataContainer().getLength() >= 1)
		{
			final int minX = chunk.x * 16;
			final int minY = 0;
			final int minZ = chunk.z * 16;

			final int maxX = minX + 15;
			final int maxY = world.getActualHeight();
			final int maxZ = minZ + 15;

			final IRegion chunkBB = new Region(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));

			this.create(null, def.getBlockDataContainer(), data, chunkBB);
		}
	}

	public boolean canGenerate(final BlueprintDefinition def, final ICreationData<?> data)
	{
		final BlockPos pos = data.getPos();

		final IRegion bb = BlueprintUtil.getRegionFromDefinition(def.getData(), data);

		if ((!this.access
				.canAccess(bb.getMin().getX(), bb.getMin().getY(), bb.getMin().getZ(), bb.getMax().getX(), bb.getMax().getY(), bb.getMax().getZ()))
				|| bb.getMax().getY() > 256)
		{
			return false;
		}

		final BlockDataContainer blocks = def.getData().getBlockDataContainer();

		final int rotAmount = Math.abs(RotationHelp.getRotationAmount(data.getRotation(), Rotation.NONE));

		if (rotAmount != 0)
		{
			final BlockPos min = data.getPos();
			final BlockPos max = new BlockPos(min.getX() + blocks.getWidth() - 1, min.getY() + blocks.getHeight() - 1,
					min.getZ() + blocks.getLength() - 1);

			for (final OrbisTuple<BlockPos.MutableBlockPos, BlockPos.MutableBlockPos> tuple : RotationHelp.getAllInBoxRotated(min, max, data.getRotation()))
			{
				final BlockPos beforeRot = tuple.getFirst();
				final BlockPos rotated = tuple.getSecond();

				final IBlockState block = blocks
						.getBlockState(beforeRot.getX() - min.getX(), beforeRot.getY() - min.getY(), beforeRot.getZ() - min.getZ());

				for (final PlacementCondition condition : def.getConditions())
				{
					if (!this.access.canAccess(rotated) || !condition.canPlace(def.getData(), this.access, pos, block, rotated))
					{
						return false;
					}
				}
			}

			// TODO: Do check for canPlaceCheckAll as well
		}
		else
		{
			BlockPos.MutableBlockPos xyz = new BlockPos.MutableBlockPos();

			for (int index = 0; index < blocks.getVolume(); index++)
			{
				final int x = def.getData().getBlockDataContainer().getX(index) + pos.getX();
				final int y = def.getData().getBlockDataContainer().getY(index) + pos.getY();
				final int z = def.getData().getBlockDataContainer().getZ(index) + pos.getZ();

				xyz.setPos(x, y, z);

				for (final PlacementCondition condition : def.getConditions())
				{
					if (!this.access.canAccess(xyz) || !condition.canPlace(def.getData(), this.access, pos, blocks.getBlockState(index), xyz))
					{
						return false;
					}
				}
			}

			for (final PlacementCondition condition : def.getConditions())
			{
				if (!condition.canPlaceCheckAll(def.getData(), this.access, pos, blocks))
				{
					return false;
				}
			}
		}

		return true;
	}

	public boolean canGenerate(final World world, final BlueprintDefinition def, final ICreationData<?> data)
	{
		return this.canGenerate(world, def, data, true);
	}

	public boolean canGenerateWithoutAreaCheck(final World world, final BlueprintDefinition def, final ICreationData<?> data)
	{
		return this.canGenerate(world, def, data, false);
	}

	private boolean canGenerate(final World world, final BlueprintDefinition def, final ICreationData<?> data, final boolean checkAreaLoaded)
	{
		final BlockPos pos = data.getPos();

		final IRegion bb = BlueprintUtil.getRegionFromDefinition(def.getData(), data);

		if ((checkAreaLoaded && !this.access
				.canAccess(bb.getMin().getX(), bb.getMin().getY(), bb.getMin().getZ(), bb.getMax().getX(), bb.getMax().getY(), bb.getMax().getZ()))
				|| bb.getMax().getY() > world.getActualHeight())
		{
			return false;
		}

		final BlockDataContainer blocks = def.getData().getBlockDataContainer();

		final int rotAmount = Math.abs(RotationHelp.getRotationAmount(data.getRotation(), Rotation.NONE));

		if (rotAmount != 0)
		{
			final BlockPos min = data.getPos();
			final BlockPos max = new BlockPos(min.getX() + blocks.getWidth() - 1, min.getY() + blocks.getHeight() - 1,
					min.getZ() + blocks.getLength() - 1);

			for (final OrbisTuple<BlockPos.MutableBlockPos, BlockPos.MutableBlockPos> tuple : RotationHelp.getAllInBoxRotated(min, max, data.getRotation()))
			{
				final BlockPos beforeRot = tuple.getFirst();
				final BlockPos rotated = tuple.getSecond();

				final IBlockState block = blocks
						.getBlockState(beforeRot.getX() - min.getX(), beforeRot.getY() - min.getY(), beforeRot.getZ() - min.getZ());

				for (final PlacementCondition condition : def.getConditions())
				{
					if (!this.access.canAccess(rotated) || !condition.canPlace(def.getData(), this.access, pos, block, rotated))
					{
						return false;
					}
				}
			}
		}
		else
		{
			for (int index = 0; index < blocks.getVolume(); index++)
			{
				final int x = def.getData().getBlockDataContainer().getX(index) + pos.getX();
				final int y = def.getData().getBlockDataContainer().getY(index) + pos.getY();
				final int z = def.getData().getBlockDataContainer().getZ(index) + pos.getZ();

				BlockPos xyz = new BlockPos(x, y, z);

				for (final PlacementCondition condition : def.getConditions())
				{
					if (!this.access.canAccess(xyz) || !condition.canPlace(def.getData(), this.access, pos, blocks.getBlockState(index), xyz))
					{
						return false;
					}
				}

			}

			for (final PlacementCondition condition : def.getConditions())
			{
				if (!condition.canPlaceCheckAll(def.getData(), this.access, pos, blocks))
				{
					return false;
				}
			}
		}

		return true;
	}

	public void create(final BlockInstance instance, final ICreationData<?> data)
	{
		this.create(instance.getBlockState(), instance.getEntity(), instance.getPos(), data);
	}

	public void create(final IBlockState blockData, final NBTTagCompound entityData, final BlockPos pos, final ICreationData<?> creationData)
	{
		if (blockData == null)
		{
			return;
		}

		if (blockData.getMaterial() == Material.AIR && !creationData.placeAir())
		{
			return;
		}

		if (!creationData.shouldCreate(blockData, pos))
		{
			return;
		}

		if (blockData.getBlock() != Blocks.STRUCTURE_VOID || creationData.placesVoid())
		{
			final IBlockState rotated = blockData.withRotation(creationData.getRotation());

			this.access.setBlockState(pos, rotated, 2);

			if (entityData != null && this.access.getWorld() != null)
			{
				TileEntity te = TileEntity.create(this.access.getWorld(), entityData);

				this.access.setTileEntity(pos, te);
			}
		}

		// TODO: Re-enable event.
		/*final ChangeBlockEvent changeBlockEvent = new ChangeBlockEvent(creationData.getWorld(), pos, creationData.getCreator());
		MinecraftForge.EVENT_BUS.post(changeBlockEvent);*/
	}

	public void create(BlueprintDataPalette palette, ICreationData<?> data)
	{
		final BlueprintData b = palette.fetchRandom(data.getWorld(), data.getRandom());

		final Rotation rotation = data.getRotation();

		final IRegion region = RotationHelp.regionFromCenter(data.getPos(), b, rotation);

		this.create(region, b, data.clone().pos(region.getMin()));
	}

	public void create(final BlockDataContainer container, final ICreationData<?> data)
	{
		this.create(null, container, data, null);
	}

	public void create(IRegion relocateTo, BlueprintData bData, ICreationData<?> data)
	{
		this.create(relocateTo, bData.getBlockDataContainer(), data, null);

		for (IScheduleLayer layer : bData.getScheduleLayers().values())
		{
			for (BlockFilter filter : layer.getFilterRecord().getData())
			{
				IPositionRecord<BlockFilter> r = layer.getFilterRecord();

				filter.apply(relocateTo, new IterablePosShape(r.getPositions(filter, data.getPos()), data.getPos(), r.getWidth(), r.getHeight(), r.getLength()),
						data, layer.getOptions());
			}

			layer.getScheduleRecord().getSchedules(ISchedule.class).forEach(s -> s.onGenerateLayer(this, data));
		}

		if (data.spawnsEntities())
		{
			BlueprintData.spawnEntities(this, bData, data.getPos());
		}
	}

	public void create(PlacedBlueprint blueprint, ICreationData<?> data)
	{
		for (BlockDataChunk chunk : blueprint.getDataChunks())
		{
			this.create(null, chunk.getContainer(), data.clone().pos(chunk.getPos().getBlock(0, 0, 0)), null);
		}

		for (List<PlacedEntity> l : blueprint.getPlacedEntities().values())
		{
			l.forEach(p -> p.spawn(this));
		}
	}

	public void create(IRegion relocateTo, final BlockDataContainer container, final ICreationData<?> data, final IRegion insideRegion)
	{
		final BlockPos min = data.getPos();
		BlockPos max = new BlockPos(min.getX() + container.getWidth() - 1, min.getY() + container.getHeight() - 1,
				min.getZ() + container.getLength() - 1);

		final int rotAmount = Math.abs(RotationHelp.getRotationAmount(data.getRotation(), Rotation.NONE));

		if (rotAmount != 0)
		{
			for (final OrbisTuple<BlockPos.MutableBlockPos, BlockPos.MutableBlockPos> tuple : RotationHelp
					.getAllInBoxRotated(min, max, data.getRotation(), relocateTo))
			{
				final BlockPos.MutableBlockPos beforeRot = tuple.getFirst();
				BlockPos.MutableBlockPos rotated = tuple.getSecond();

				if (insideRegion == null || insideRegion.contains(rotated))
				{
					final IBlockState toCreate = container
							.getBlockState(beforeRot.getX() - min.getX(), beforeRot.getY() - min.getY(), beforeRot.getZ() - min.getZ());
					final NBTTagCompound entity = container
							.getTileEntity(beforeRot.getX() - min.getX(), beforeRot.getY() - min.getY(), beforeRot.getZ() - min.getZ());

					this.create(toCreate, entity, rotated, data);
				}
			}
		}
		else
		{
			final Region region = new Region(new BlockPos(0, 0, 0),
					new BlockPos(container.getWidth() - 1, container.getHeight() - 1, container.getLength() - 1));

			for (final BlockPos.MutableBlockPos iterPos : region.getMutableBlockPosInRegion())
			{
				if (insideRegion == null || insideRegion.contains(iterPos.getX() + min.getX(), iterPos.getY() + min.getY(),
						iterPos.getZ() + min.getZ()))
				{
					final IBlockState block = container.getBlockState(iterPos.getX(), iterPos.getY(), iterPos.getZ());
					final NBTTagCompound entity = container.getTileEntity(iterPos.getX(), iterPos.getY(), iterPos.getZ());

					if (block != null)
					{
						this.create(block, entity, iterPos.toImmutable().add(min.getX(), min.getY(), min.getZ()), data);
					}
				}
			}
		}
	}

}
