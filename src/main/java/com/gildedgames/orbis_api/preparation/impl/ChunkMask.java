package com.gildedgames.orbis_api.preparation.impl;

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
	private final ChunkMaskSegment[] segments = new ChunkMaskSegment[32];

	private final int x, z;

	public ChunkMask(int x, int z)
	{
		this.x = x;
		this.z = z;
	}

	public void setBlock(int x, int y, int z, int b)
	{
		ChunkMaskSegment segment = this.segments[y >> 3];

		if (segment == null)
		{
			this.segments[y >> 3] = segment = new ChunkMaskSegment();
		}

		segment.setBlock(x, y & 7, z, b);
	}

	public int getBlock(int x, int y, int z)
	{
		ChunkMaskSegment segment = this.segments[y >> 3];

		if (segment == null)
		{
			return 0;
		}

		return segment.getBlock(x, y & 7, z);
	}

	public int getX()
	{
		return this.x;
	}

	public int getZ()
	{
		return this.z;
	}

	public ChunkMaskSegment getSegment(int y)
	{
		return this.segments[y];
	}

	public int getMaxYSegment()
	{
		for (int chunkY = 31; chunkY >= 0; chunkY--)
		{
			ChunkMaskSegment segment = this.segments[chunkY];

			if (segment == null)
			{
				continue;
			}

			return (chunkY * 8);
		}

		return -1;
	}

	public int getMinYSegment()
	{
		for (int chunkY = 0; chunkY < 32; chunkY++)
		{
			ChunkMaskSegment segment = this.segments[chunkY];

			if (segment == null)
			{
				continue;
			}

			return (chunkY * 8);
		}

		return -1;
	}

	public void fill(int b)
	{
		for (int chunkY = 0; chunkY < 32; chunkY++)
		{
			ChunkMaskSegment segment = this.segments[chunkY];

			if (segment == null)
			{
				this.segments[chunkY] = segment = new ChunkMaskSegment();
			}

			segment.fill(b);
		}
	}
}
