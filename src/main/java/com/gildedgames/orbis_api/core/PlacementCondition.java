package com.gildedgames.orbis_api.core;

import com.gildedgames.orbis_api.processing.IBlockAccessExtended;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public interface PlacementCondition
{

	boolean canPlace(IBlockAccessExtended world, BlockPos placedAt, IBlockState block, BlockPos pos);

	default boolean prePlacementResolve(IBlockAccessExtended world, BlockPos placedAt)
	{
		return true;
	}

}