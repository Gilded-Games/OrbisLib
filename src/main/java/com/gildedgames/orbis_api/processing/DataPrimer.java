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

	public boolean canGenerate(BakedBlueprint blueprint, BlockPos offset)
	{
		Region region = new Region(blueprint.getBakedRegion());
		region.add(offset);

		if (!this.access.canAccess(region.getMin().getX() - 2, region.getMin().getY() - 2, region.getMin().getZ() - 2,
				region.getMax().getX() + 2, region.getMax().getY() + 2, region.getMax().getZ() + 2))
		{
			return false;
		}

		for (final PlacementCondition condition : blueprint.getDefinition().getConditions())
		{
			if (!condition.validate(this.access, blueprint, region.getMin()))
			{
				return false;
			}
		}

		return true;
	}

	private void setBlockInWorld(final IBlockState state, final NBTTagCompound entityNBT, final BlockPos pos, final ICreationData<?> creationData)
	{
		if (state.getMaterial() == Material.AIR && !creationData.placeAir())
		{
			return;
		}

		if (state.getBlock() == Blocks.STRUCTURE_VOID && !creationData.placesVoid())
		{
			return;
		}

		this.access.setBlockState(pos, state, 2 | 16);

		if (entityNBT != null && this.access.getWorld() != null)
		{
			TileEntity te = TileEntity.create(this.access.getWorld(), entityNBT);

			this.access.setTileEntity(pos, te);
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

		for (IBakedPosAction action : baked.getBakedPositionActions())
		{
			action.call(this);
		}
	}

	public void place(PlacedBlueprint placed, IRegion region)
	{
		BakedBlueprint baked = placed.getBaked();

		BlockPos offset = placed.getCreationData().getPos();

		if (!this.copyBlocksIntoWorld(offset, baked, region, placed.getCreationData()))
		{
			return;
		}

		Region intersection = placed.getRegion().fromIntersection(region);

		for (IBakedPosAction action : placed.getPendingPosActions())
		{
			if (intersection.contains(action.getPos()))
			{
				action.call(this);
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

		for (BlockPos pos : BlockPos.getAllInBoxMutable(intersection.getMin(), intersection.getMax()))
		{
			if (!data.shouldCreate(pos))
			{
				continue;
			}

			int x = pos.getX() - region.getMin().getX();
			int y = pos.getY() - region.getMin().getY();
			int z = pos.getZ() - region.getMin().getZ();

			final IBlockState block = blocks.getBlockState(x, y, z);

			final NBTTagCompound entity;

			if (block.getBlock().hasTileEntity(block))
			{
				entity = blocks.getTileEntity(x, y, z);
			}
			else
			{
				entity = null;
			}

			this.setBlockInWorld(block, entity, pos, data);
		}

		return true;
	}

}
