package com.gildedgames.orbis_api.preparation;

import net.minecraft.block.state.IBlockState;

public interface IChunkMaskTransformer
{
	IBlockState remapBlock(int val);
}
