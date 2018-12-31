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
	private final byte[] mask = new byte[16 * 16 * 16];

	private final int x, y, z;

	public ChunkMask(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setBlock(int x, int y, int z, int b)
	{
		if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15)
		{
			throw new IllegalArgumentException("Out of bounds");
		}

		this.mask[x << 8 | z << 4 | y] = (byte) b;
	}

	public int getBlock(int x, int y, int z)
	{
		if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15)
		{
			throw new IllegalArgumentException("Out of bounds");
		}

		return this.mask[x << 8 | z << 4 | y];
	}

	public int getTopBlock(int x, int z)
	{
		for (int y = 16; y > 0; y--)
		{
			if (this.getBlock(x, y, z) > 0)
			{
				return y;
			}
		}

		return -1;
	}

	public ChunkPrimer createChunk(ChunkPrimer primer, IChunkMaskTransformer func)
	{
		int offsetY = this.y * 16;

		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				for (int y = 0; y < 16; y++)
				{
					int raw = this.getBlock(x, y, z);

					IBlockState state = func.remapBlock(raw);

					primer.setBlockState(x, y + offsetY, z, state);
				}
			}
		}

		return primer;
	}

	public int getX()
	{
		return this.x;
	}

	public int getY()
	{
		return this.y;
	}

	public int getZ()
	{
		return this.z;
	}
}
