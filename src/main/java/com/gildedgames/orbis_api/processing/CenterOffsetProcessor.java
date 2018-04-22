package com.gildedgames.orbis_api.processing;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public interface CenterOffsetProcessor
{

	BlockPos process(Rotation rotation);

}