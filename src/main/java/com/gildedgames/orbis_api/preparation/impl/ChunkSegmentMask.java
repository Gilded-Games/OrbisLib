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
public class ChunkSegmentMask
{
	private final byte[] blocks = new byte[16 * 16 * 16];

	private final int x, y, z;

	private boolean touched;

	public ChunkSegmentMask(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setBlock(int x, int y, int z, int b)
	{
		this.blocks[x << 8 | z << 4 | y] = (byte) b;

		this.touched = true;
	}

	public int getBlock(int x, int y, int z)
	{
		return this.blocks[x << 8 | z << 4 | y];
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

	public boolean wasTouched()
	{
		return this.touched;
	}
}
