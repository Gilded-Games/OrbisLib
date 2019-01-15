package com.gildedgames.orbis_api.core;

import com.gildedgames.orbis_api.core.baking.BakedBlueprint;
import com.gildedgames.orbis_api.processing.IBlockAccessExtended;
import net.minecraft.util.math.BlockPos;

public interface PlacementCondition
{

	boolean validate(IBlockAccessExtended access, BakedBlueprint blueprint, BlockPos offset);

}