package com.gildedgames.orbis_api.processing;

import com.gildedgames.orbis_api.block.BlockDataContainer;
import com.gildedgames.orbis_api.core.ICreationData;
import com.gildedgames.orbis_api.core.PlacedBlueprint;
import com.gildedgames.orbis_api.core.PlacementCondition;
import com.gildedgames.orbis_api.core.baking.BakedBlueprint;
import com.gildedgames.orbis_api.core.baking.IBakedPosAction;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.data.region.Region;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Iterator;
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

	public boolean canGenerate(BakedBlueprint baked, List<PlacementCondition> conditions, BlockPos offset, final boolean checkAreaLoaded)
	{
		Region bakedRegion = baked.getBakedRegion();

		BlockPos minReloc = bakedRegion.getMin().add(offset);
		BlockPos maxReloc = bakedRegion.getMax().add(offset);

		if (checkAreaLoaded)
		{
			if (!this.access.canAccess(minReloc.getX() - 2, minReloc.getY() - 2, minReloc.getZ() - 2,
					maxReloc.getX() + 2, maxReloc.getY() + 2, maxReloc.getZ() + 2))
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

		BlockDataContainer container = baked.getBlockData();

		BlockPos.MutableBlockPos mutatedPos = new BlockPos.MutableBlockPos();

		for (final BlockPos pos : bakedRegion.getMutableBlockPosInRegion())
		{
			int thisX = pos.getX() - baked.getBakedRegion().getMin().getX();
			int thisY = pos.getY() - baked.getBakedRegion().getMin().getY();
			int thisZ = pos.getZ() - baked.getBakedRegion().getMin().getZ();

			IBlockState block = container.getBlockState(thisX, thisY, thisZ);

			mutatedPos.setPos(pos.getX() + offset.getX(), pos.getY() + offset.getY(), pos.getZ() + offset.getZ());

			for (final PlacementCondition condition : conditions)
			{
				if (!condition.canPlace(this.access, minReloc, block, mutatedPos))
				{
					return false;
				}
			}
		}

		return true;
	}

	public void setBlockInWorld(final IBlockState state, final NBTTagCompound entity, final BlockPos pos, final ICreationData<?> creationData)
	{
		if (state.getMaterial() == Material.AIR && !creationData.placeAir())
		{
			return;
		}

		if (!creationData.shouldCreate(state, pos))
		{
			return;
		}

		if (state.getBlock() != Blocks.STRUCTURE_VOID || creationData.placesVoid())
		{
			this.access.setBlockState(pos, state, 2);

			if (entity != null && this.access.getWorld() != null)
			{
				TileEntity te = TileEntity.create(this.access.getWorld(), entity);

				this.access.setTileEntity(pos, te);
			}
		}

		// TODO: Re-enable event.
		/*final ChangeBlockEvent changeBlockEvent = new ChangeBlockEvent(creationData.getWorld(), min, creationData.getCreator());
		MinecraftForge.EVENT_BUS.post(changeBlockEvent);*/
	}

	public void place(BakedBlueprint baked, BlockPos relocateTo)
	{
		if (!this.copyBlocksIntoWorld(relocateTo, baked, null, baked.getCreationData()))
		{
			return;
		}

		for (IBakedPosAction action : baked.getBakedPosActions())
		{
			action.call(this);
		}
	}

	public void place(PlacedBlueprint placed, IRegion region, boolean createsEntities)
	{
		BakedBlueprint baked = placed.getBaked();

		BlockPos offset = placed.getCreationData().getPos();

		if (!this.copyBlocksIntoWorld(offset, baked, region, placed.getCreationData()))
		{
			return;
		}

		if (createsEntities)
		{
			Region intersection = placed.getRegion().fromIntersection(region);

			Iterator<IBakedPosAction> it = placed.getPendingPosActions().iterator();

			while (it.hasNext())
			{
				IBakedPosAction action = it.next();

				if (intersection.contains(action.getPos()))
				{
					action.call(this);

					it.remove();
				}
			}
		}
	}

	private boolean copyBlocksIntoWorld(BlockPos offset, BakedBlueprint blueprint, IRegion bounds, ICreationData<?> data)
	{
		BlockDataContainer blocks = blueprint.getBlockData();

		final Region region = new Region(blueprint.getBakedRegion());
		region.add(offset);

		final Region intersection;

		if (bounds != null)
		{
			if (!region.intersectsWith(bounds))
			{
				return false;
			}

			intersection = region.fromIntersection(bounds);
		}
		else
		{
			intersection = region;
		}

		for (BlockPos pos : BlockPos.getAllInBox(intersection.getMin(), intersection.getMax()))
		{
			int x = pos.getX() - region.getMin().getX();
			int y = pos.getY() - region.getMin().getY();
			int z = pos.getZ() - region.getMin().getZ();

			final IBlockState block = blocks.getBlockState(x, y, z);
			final NBTTagCompound entity = blocks.getTileEntity(x, y, z);

			this.setBlockInWorld(block, entity, pos, data);
		}


		return true;
	}

}
