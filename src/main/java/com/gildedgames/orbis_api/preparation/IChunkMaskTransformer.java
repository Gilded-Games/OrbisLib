package com.gildedgames.orbis_api.preparation;

import net.minecraft.block.state.IBlockState;

public interface IChunkMaskTransformer
{
	IBlockState getBlockState(int key);

	int getBlockCount();
}
