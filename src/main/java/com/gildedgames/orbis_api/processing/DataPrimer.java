package com.gildedgames.orbis_api.processing;

import com.gildedgames.orbis_api.block.BlockDataContainer;
import com.gildedgames.orbis_api.block.BlockInstance;
import com.gildedgames.orbis_api.core.*;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.blueprint.BlueprintDataPalette;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.data.region.Region;
import com.gildedgames.orbis_api.util.OrbisTuple;
import com.gildedgames.orbis_api.util.RotationHelp;
import com.gildedgames.orbis_api.util.mc.BlockUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class DataPrimer
{
	/**
	 * If enabled, will generate Emerald version of structure above checked area if canGenerate returns true.
	 *
	 * Can be used to check/make sure that the checked area is the same as the generate area for a structure.
	 */
	public static boolean CAN_GENERATE_DEBUG = true;

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

	public boolean canGenerate(BakedBlueprint baked, List<PlacementCondition> conditions, final boolean checkAreaLoaded)
	{
		return this.canGenerate(baked, conditions, baked.getCreationData().getPos(), checkAreaLoaded);
	}

	public boolean canGenerate(BakedBlueprint baked, List<PlacementCondition> conditions, BlockPos relocateTo, final boolean checkAreaLoaded)
	{
		CAN_GENERATE_DEBUG = false;

		BlockPos bakedMin = baked.getBakedMin();
		BlockPos bakedMax = bakedMin.add(baked.getWidth() - 1, baked.getHeight() - 1, baked.getLength() - 1);

		int relocateX = -(baked.getBakedMin().getX() - relocateTo.getX());
		int relocateY = -(baked.getBakedMin().getY() - relocateTo.getY());
		int relocateZ = -(baked.getBakedMin().getZ() - relocateTo.getZ());

		BlockPos minReloc = bakedMin.add(relocateX, relocateY, relocateZ);
		BlockPos maxReloc = bakedMax.add(relocateX, relocateY, relocateZ);

		if (checkAreaLoaded)
		{
			if (!this.access.canAccess(minReloc.getX(), minReloc.getY(), minReloc.getZ(), maxReloc.getX(), maxReloc.getY(), maxReloc.getZ()))
			{
				return false;
			}
		}

		for (final PlacementCondition condition : conditions)
		{
			if (!condition.prePlacementResolve(this.access, minReloc))
			{
				return false;
			}
		}

		int yDif = -bakedMin.getY() + minReloc.getY();

		for (BlockDataChunk chunk : baked.getDataChunks())
		{
			BlockDataContainer container = chunk.getContainer();

			final Region region = new Region(new BlockPos(0, 0, 0),
					new BlockPos(container.getWidth() - 1, container.getHeight() - 1, container.getLength() - 1));

			BlockPos chunkMin = chunk.getPos().getBlock(0, bakedMin.getY(), 0);

			for (final BlockPos.MutableBlockPos iterPos : region.getMutableBlockPosInRegion())
			{
				int origX = iterPos.getX();
				int origY = iterPos.getY();
				int origZ = iterPos.getZ();

				int newX = origX + chunkMin.getX();
				int newY = origY + chunkMin.getY();
				int newZ = origZ + chunkMin.getZ();

				if (newX < bakedMin.getX() || newY < bakedMin.getY() || newZ < bakedMin.getZ() || newX > bakedMax.getX()
						|| newY > bakedMax.getY()
						|| newZ > bakedMax.getZ())
				{
					continue;
				}

				final IBlockState block = container.getBlockState(iterPos.getX(), iterPos.getY(), iterPos.getZ());

				if (block == null)
				{
					continue;
				}

				BlockPos newPos = new BlockPos(newX + relocateX, newY + yDif, newZ + relocateZ);

				for (final PlacementCondition condition : conditions)
				{
					if (!this.access.canAccess(newPos) || !condition.canPlace(this.access, minReloc, block, newPos))
					{
						return false;
					}
				}
			}
		}

		if (CAN_GENERATE_DEBUG)
		{
			for (BlockDataChunk chunk : baked.getDataChunks())
			{
				BlockDataContainer container = chunk.getContainer();

				final Region region = new Region(new BlockPos(0, 0, 0),
						new BlockPos(container.getWidth() - 1, container.getHeight() - 1, container.getLength() - 1));

				BlockPos chunkMin = chunk.getPos().getBlock(0, bakedMin.getY(), 0);

				for (final BlockPos.MutableBlockPos iterPos : region.getMutableBlockPosInRegion())
				{
					int origX = iterPos.getX();
					int origY = iterPos.getY();
					int origZ = iterPos.getZ();

					int newX = origX + chunkMin.getX();
					int newY = origY + chunkMin.getY();
					int newZ = origZ + chunkMin.getZ();

					if (newX < bakedMin.getX() || newY < bakedMin.getY() || newZ < bakedMin.getZ() || newX > bakedMax.getX()
							|| newY > bakedMax.getY()
							|| newZ > bakedMax.getZ())
					{
						continue;
					}

					final IBlockState state = container.getBlockState(iterPos.getX(), iterPos.getY(), iterPos.getZ());

					if (state == null || BlockUtil.isAir(state) || BlockUtil.isVoid(state))
					{
						BlockPos newPos = new BlockPos(newX + relocateX, newY + yDif + baked.getHeight(), newZ + relocateZ);

						this.getWorld().setBlockState(newPos, Blocks.GLASS.getDefaultState());

						continue;
					}

					BlockPos newPos = new BlockPos(newX + relocateX, newY + yDif + baked.getHeight(), newZ + relocateZ);

					this.getWorld().setBlockState(newPos, Blocks.EMERALD_BLOCK.getDefaultState());
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
		/*final ChangeBlockEvent changeBlockEvent = new ChangeBlockEvent(creationData.getWorld(), min, creationData.getCreator());
		MinecraftForge.EVENT_BUS.post(changeBlockEvent);*/
	}

	public void create(BlueprintDataPalette palette, ICreationData<?> data)
	{
		final BlueprintData b = palette.fetchRandom(data.getWorld(), data.getRandom());

		final Rotation rotation = data.getRotation();

		final IRegion region = RotationHelp.regionFromCenter(data.getPos(), b, rotation);

		BakedBlueprint baked = new BakedBlueprint(b, data.clone().pos(region.getMin()));

		baked.bake();

		this.create(baked);
	}

	public void create(final BlockDataContainer container, final ICreationData<?> data)
	{
		this.create(null, container, data, null);
	}

	public void create(PlacedBlueprint blueprint)
	{
		this.create(blueprint.getBaked());
	}

	public void create(BakedBlueprint baked)
	{
		for (BlockDataChunk chunk : baked.getDataChunks())
		{
			if (chunk == null)
			{
				continue;
			}

			this.create(null, chunk.getContainer(),
					baked.getCreationData().clone().rotation(Rotation.NONE).pos(chunk.getPos().getBlock(0, baked.getCreationData().getPos().getY(), 0)),
					null);
		}

		for (List<PlacedEntity> l : baked.getPlacedEntities().values())
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
