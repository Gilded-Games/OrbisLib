package com.gildedgames.orbis_api.util;

import com.gildedgames.orbis_api.block.BlockDataContainer;
import com.gildedgames.orbis_api.data.region.IMutableRegion;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.data.region.IShape;
import com.gildedgames.orbis_api.data.region.Region;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlueprintHelper
{

	/**
	 * Both states must be the same dimensions.
	 * @param oldState
	 * @param newState
	 * @throws IllegalArgumentException If you both states are not the same dimensions.
	 * @return
	 */
	public static BlockDataContainer fetchDifferenceBetween(BlockDataContainer oldState, BlockDataContainer newState)
	{
		if (oldState.getWidth() != newState.getWidth() || oldState.getHeight() != newState.getHeight() || oldState.getLength() != newState.getLength())
		{
			throw new IllegalArgumentException("The two states passed through to 'BlueprintHelper.fetchDifferenceBetween' must be the same dimensions");
		}

		BlockDataContainer difference = new BlockDataContainer(Blocks.STRUCTURE_VOID.getDefaultState(), oldState.getWidth(), oldState.getHeight(),
				oldState.getLength());

		for (BlockPos.MutableBlockPos pos : BlockPos
				.getAllInBoxMutable(BlockPos.ORIGIN, new BlockPos(oldState.getWidth() - 1, oldState.getHeight() - 1, oldState.getLength() - 1)))
		{
			IBlockState oldBlock = oldState.getBlockState(pos);
			IBlockState newBlock = newState.getBlockState(pos);

			if (oldBlock != newBlock)
			{
				difference.setBlockState(newBlock, pos);
			}
		}

		return difference;
	}

	public static BlockDataContainer fetchBlocksInside(final IShape shape, final World world)
	{
		return fetchBlocksInside(shape, world, null);
	}

	public static BlockDataContainer fetchBlocksInside(final IShape shape, final World world, @Nullable BlockPos overridePos)
	{
		return fetchBlocksInside(Blocks.AIR.getDefaultState(), shape, world, overridePos);
	}

	public static BlockDataContainer fetchBlocksInside(IBlockState defaultState, final IShape shape, final World world, @Nullable BlockPos overridePos)
	{
		IRegion bb = overridePos != null ? new Region(shape.getBoundingBox()) : shape.getBoundingBox();

		if (overridePos != null)
		{
			RegionHelp.translate((IMutableRegion) bb, overridePos);
		}

		final BlockDataContainer container = new BlockDataContainer(defaultState, bb);

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
