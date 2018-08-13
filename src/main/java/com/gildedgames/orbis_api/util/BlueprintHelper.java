package com.gildedgames.orbis_api.util;

import com.gildedgames.orbis_api.block.BlockDataContainer;
import com.gildedgames.orbis_api.block.BlockDataContainerDefault;
import com.gildedgames.orbis_api.data.region.IMutableRegion;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.data.region.IShape;
import com.gildedgames.orbis_api.data.region.Region;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlueprintHelper
{

	public static BlockDataContainer fetchBlocksInside(final IShape shape, final World world)
	{
		return fetchBlocksInside(shape, world, null);
	}

	public static BlockDataContainer fetchBlocksInside(final IShape shape, final World world, @Nullable BlockPos overridePos)
	{
		IRegion bb = overridePos != null ? new Region(shape.getBoundingBox()) : shape.getBoundingBox();

		if (overridePos != null)
		{
			RegionHelp.translate((IMutableRegion) bb, overridePos);
		}

		final BlockDataContainer container = new BlockDataContainerDefault(bb);

		final BlockPos min = shape.getBoundingBox().getMin();

		for (final BlockPos pos : shape.createShapeData())
		{
			final BlockPos translated = pos.add(-min.getX(), -min.getY(), -min.getZ());

			container.setBlockState(world.getBlockState(pos), translated);

			TileEntity entity = world.getTileEntity(pos);

			if (entity != null)
			{
				container.setTileEntity(entity, translated);
			}
		}

		return container;
	}

}
