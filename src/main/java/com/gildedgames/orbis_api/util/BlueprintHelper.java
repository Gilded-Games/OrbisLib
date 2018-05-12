package com.gildedgames.orbis_api.util;

import com.gildedgames.orbis_api.block.BlockDataContainer;
import com.gildedgames.orbis_api.data.region.IShape;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlueprintHelper
{

	public static BlockDataContainer fetchBlocksInside(final IShape shape, final World world)
	{
		final BlockDataContainer container = new BlockDataContainer(shape.getBoundingBox());

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
