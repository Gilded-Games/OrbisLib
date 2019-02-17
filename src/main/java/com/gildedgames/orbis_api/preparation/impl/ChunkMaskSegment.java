package com.gildedgames.orbis_api.preparation.impl;

import java.util.Arrays;

/**
 * 16x8x16 segment of ChunkMask blocks.
 */
public class ChunkMaskSegment
{
	private final byte[] blocks = new byte[16 * 8 * 16];

	public void setBlock(int x, int y, int z, int b)
	{
		this.blocks[x << 7 | z << 3 | y] = (byte) b;
	}

	public int getBlock(int x, int y, int z)
	{
		return this.blocks[x << 7 | z << 3 | y];
	}

	public void fill(int b)
	{
		Arrays.fill(this.blocks, (byte) b);
	}
}
