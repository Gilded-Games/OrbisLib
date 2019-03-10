package com.gildedgames.orbis.lib.core;

import com.gildedgames.orbis.lib.core.baking.BakedBlueprint;
import com.gildedgames.orbis.lib.processing.IBlockAccessExtended;
import net.minecraft.util.math.BlockPos;

public interface PlacementCondition
{

	boolean validate(IBlockAccessExtended access, BakedBlueprint blueprint, BlockPos offset);

	int getPriority();
}