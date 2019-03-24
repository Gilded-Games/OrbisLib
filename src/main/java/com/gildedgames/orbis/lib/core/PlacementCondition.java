package com.gildedgames.orbis.lib.core;

import com.gildedgames.orbis.lib.core.baking.BakedBlueprint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface PlacementCondition
{

	boolean validate(IBlockReader access, BakedBlueprint blueprint, BlockPos offset);

	int getPriority();
}