package com.gildedgames.orbis.lib.processing;

import com.gildedgames.orbis.lib.block.BlockDataContainer;
import com.gildedgames.orbis.lib.core.ICreationData;
import com.gildedgames.orbis.lib.core.PlacedBlueprint;
import com.gildedgames.orbis.lib.core.PlacementCondition;
import com.gildedgames.orbis.lib.core.baking.BakedBlueprint;
import com.gildedgames.orbis.lib.core.baking.IBakedPosAction;
import com.gildedgames.orbis.lib.data.region.IRegion;
import com.gildedgames.orbis.lib.data.region.Region;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DataPrimer
{
	private final World access;

	public DataPrimer(final World primer)
	{
		this.access = primer;
	}

	@Nullable
	public World getWorld()
	{
		return this.access.getWorld();
	}

	public void spawn(Entity entity)
	{
		this.access.addEntity(entity);
	}

	public boolean canGenerate(BakedBlueprint blueprint, BlockPos offset)
	{
		Region region = new Region(blueprint.getBakedRegion());
		region.add(offset);

		if (!this.access.isAreaLoaded(region.getMin().getX() - 2, region.getMin().getY() - 2, region.getMin().getZ() - 2,
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

	private void setBlockInWorld(final BlockState state, final BlockDataContainer.TileEntityEntry entity, final BlockPos pos, final ICreationData<?> creationData)
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

		if (entity != null && this.access.getWorld() != null)
		{
			this.access.setTileEntity(pos, TileEntity.create(entity.data));
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

			final BlockState state = blocks.getBlockState(x, y, z);

			final BlockDataContainer.TileEntityEntry entity;

			if (state.getBlock().hasTileEntity(state))
			{
				entity = blocks.getTileEntity(x, y, z);
			}
			else
			{
				entity = null;
			}

			this.setBlockInWorld(state, entity, pos, data);
		}

		return true;
	}

}
