package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.preparation.IChunkMaskTransformer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * Very minimal alternative to chunks for terrain generation.
 *
 * This allows mods with very expensive terrain generation to quickly compose the general structure of blocks
 * inside a chunk without referring to specific block types or performing costly {@link IBlockState} operations.
 *
 * After the general structure has been created, a very quick conversion of the blocks contained in the mask can
 * be performed to create a {@link ChunkPrimer}.
 *
 * Block 0 is assumed to always be air.
 */
public class ChunkMask
{
	private byte[] mask = new byte[16 * 256 * 16];

	public void setBlock(int x, int y, int z, int b)
	{
		this.mask[x + 16 * (y + 256 * z)] = (byte) b;
	}

	public int getBlock(int x, int y, int z)
	{
		return this.mask[x + 16 * (y + 256 * z)];
	}

	public ChunkPrimer createChunk(ChunkPrimer primer, IChunkMaskTransformer func)
	{
		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				for (int y = 0; y < 256; y++)
				{
					int raw = this.getBlock(x, y, z);

					IBlockState state = func.remapBlock(raw);
					primer.setBlockState(x, y, z, state);
				}
			}
		}

		return primer;
	}
}
