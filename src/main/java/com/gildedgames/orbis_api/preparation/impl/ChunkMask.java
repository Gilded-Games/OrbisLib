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
	private final byte[] blocks = new byte[16 * 256 * 16];

	private final int x, z;

	private boolean touched;

	public ChunkMask(int x, int z)
	{
		this.x = x;
		this.z = z;
	}

	public void setBlock(int x, int y, int z, int b)
	{
		this.blocks[x << 12 | z << 8 | y] = (byte) b;

		this.touched = true;
	}

	public int getBlock(int x, int y, int z)
	{
		return this.blocks[x << 12 | z << 8 | y];
	}

	public int getX()
	{
		return this.x;
	}

	public int getZ()
	{
		return this.z;
	}

	public boolean wasTouched()
	{
		return this.touched;
	}
}
