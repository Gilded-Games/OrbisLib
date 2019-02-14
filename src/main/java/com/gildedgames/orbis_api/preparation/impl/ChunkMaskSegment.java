package com.gildedgames.orbis_api.preparation.impl;

import java.util.Arrays;

public class ChunkMaskSegment
{
	private final byte[] blocks = new byte[16 * 16 * 16];

	public void setBlock(int x, int y, int z, int b)
	{
		this.blocks[x << 8 | z << 4 | y] = (byte) b;
	}

	public int getBlock(int x, int y, int z)
	{
		return this.blocks[x << 8 | z << 4 | y];
	}

	public void fill(int b)
	{
		Arrays.fill(this.blocks, (byte) b);
	}
}
