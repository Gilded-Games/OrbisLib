package com.gildedgames.orbis.lib.preparation;

import net.minecraft.block.BlockState;

public interface IChunkMaskTransformer
{
	BlockState getBlockState(int key);

	int getBlockCount();
}
