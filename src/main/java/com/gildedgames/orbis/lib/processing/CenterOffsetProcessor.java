package com.gildedgames.orbis.lib.processing;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public interface CenterOffsetProcessor
{

	BlockPos process(Rotation rotation);

}