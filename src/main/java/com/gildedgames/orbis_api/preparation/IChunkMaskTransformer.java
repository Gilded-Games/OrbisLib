package com.gildedgames.orbis_api.preparation;

import net.minecraft.block.state.IBlockState;

public interface IChunkMaskTransformer
{
	IBlockState getBlockState(int val);

	int getBlockID(int val);

	int getBlockMeta(int val);
}
