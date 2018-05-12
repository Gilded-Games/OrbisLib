package com.gildedgames.orbis_api.core;

import com.gildedgames.orbis_api.block.BlockDataContainer;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.processing.IBlockAccessExtended;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public interface PlacementCondition
{

	boolean canPlace(BlueprintData data, IBlockAccessExtended world, BlockPos placedAt, IBlockState block, BlockPos pos);

	/** Should return true by default **/
	boolean canPlaceCheckAll(BlueprintData data, IBlockAccessExtended world, BlockPos placedAt, BlockDataContainer blocks);

}