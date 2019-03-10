package com.gildedgames.orbis.lib.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class WorldSlice
{
	private final World world;

	private final Chunk[] chunks;

	private final ExtendedBlockStorage[] sections;

	private final int offsetX, offsetZ;

	private final IBlockState defaultBlockState = Blocks.AIR.getDefaultState();

	public WorldSlice(World world, ChunkPos pos)
	{
		this.world = world;

		this.chunks = new Chunk[3 * 3];
		this.sections = new ExtendedBlockStorage[this.chunks.length * 16];

		this.offsetX = (pos.x * 16) - 16;
		this.offsetZ = (pos.z * 16) - 16;

		for (int x = 0; x < 3; x++)
		{
			for (int z = 0; z < 3; z++)
			{
				int chunkX = pos.x + x - 1;
				int chunkZ = pos.z + z - 1;

				Chunk chunk = world.getChunkProvider().getLoadedChunk(chunkX, chunkZ);

				if (chunk == null)
				{
					continue;
				}

				this.chunks[(x * 3) + z] = chunk;

				for (int y = 0; y < 16; y++)
				{
					this.sections[z + y * 3 + x * 16 * 3] = chunk.getBlockStorageArray()[y];
				}

			}
		}
	}

	public IBlockState getBlockState(BlockPos pos)
	{
		return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
	}

	public IBlockState getBlockState(int x, int y, int z)
	{
		if (y >= 0  && y < 256)
		{
			int chunkX = (x - this.offsetX) >> 4;
			int chunkZ = (z - this.offsetZ) >> 4;

			ExtendedBlockStorage section = this.sections[chunkZ + (y >> 4) * 3 + chunkX * 16 * 3];

			if (section != null)
			{
				return section.get(x & 15, y & 15, z & 15);
			}
		}

		return this.defaultBlockState;
	}

	/**
	 * This method is intended to be used when you are doing "check and replace" generation. Rather than
	 * performing an additional fetch operation, we can avoid it entirely. Care must be taken to ensure
	 * that {@param before} is the block previously queried at the same {@param pos} using
	 * {@link WorldSlice#getBlockState(BlockPos)}.
	 *
	 * It is also possible to control whether or not replacing the block should prompt a lighting
	 * update.
	 *
	 * You should NEVER use this method if you are replacing an air block. Only use it if the block
	 * you are replacing is non-air.
	 *
	 * @param pos The {@link BlockPos} of the block to replace
	 * @param after The state of the block at {@link BlockPos} after it is replaced
	 */
	public void replaceBlockState(BlockPos pos, IBlockState after)
	{
		if (pos.getY() >= 0  && pos.getY() < 256)
		{
			int chunkX = (pos.getX() - this.offsetX) >> 4;
			int chunkZ = (pos.getZ() - this.offsetZ) >> 4;

			ExtendedBlockStorage section = this.sections[chunkZ + (pos.getY() >> 4) * 3 + chunkX * 16 * 3];

			if (section != null)
			{
				section.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, after);
			}
		}
	}


	public World getWorld()
	{
		return this.world;
	}

	public boolean isAirBlock(BlockPos pos)
	{
		return this.isAirBlock(pos.getX(), pos.getY(), pos.getZ());
	}

	public boolean setBlockState(BlockPos pos, IBlockState state)
	{
		if (pos.getY() >= 0  && pos.getY() < 256)
		{
			int chunkX = (pos.getX() - this.offsetX) >> 4;
			int chunkZ = (pos.getZ() - this.offsetZ) >> 4;

			Chunk chunk = this.chunks[(chunkX * 3) + chunkZ];

			if (chunk == null)
			{
				return false;
			}

			chunk.setBlockState(pos, state);

			return true;
		}

		return false;
	}

	public boolean isAreaWithin(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		if (minY < 0 || minY > 255)
		{
			return false;
		}

		if (maxY < 0 || maxY > 255)
		{
			return false;
		}

		int minChunkX = (minX - this.offsetX) >> 4;
		int minChunkZ = (minZ - this.offsetZ) >> 4;

		int maxChunkX = (maxX - this.offsetX) >> 4;
		int maxChunkZ = (maxZ - this.offsetZ) >> 4;

		if (minChunkX < 0 || minChunkX > 2 || minChunkZ < 0 || minChunkZ > 2)
		{
			return false;
		}

		if (maxChunkX < 0 || maxChunkX > 2 || maxChunkZ < 0 || maxChunkZ > 2)
		{
			return false;
		}

		return true;
	}

	public boolean isAreaWithin(BlockPos pos, int radius)
	{
		return this.isAreaWithin(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
				pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius);
	}

	public boolean isBlockWithin(BlockPos pos)
	{
		return this.isBlockWithin(pos.getX(), pos.getY(), pos.getZ());
	}

	public boolean isBlockWithin(int x, int y, int z)
	{
		if (y < 0 || y > 255)
		{
			return false;
		}

		int chunkX = (x - this.offsetX) >> 4;
		int chunkZ = (z - this.offsetZ) >> 4;

		if (chunkX < 0 || chunkX > 2 || chunkZ < 0 || chunkZ > 2)
		{
			return false;
		}

		return true;
	}

	public BlockPos getHighestBlockPos(int x, int z)
	{
		return new BlockPos(x, this.getHeighestBlockValue(x, z), z);
	}
	
	public int getHeighestBlockValue(int x, int z)
	{
		return this.world.getChunk(x >> 4, z >> 4).getHeightValue(x & 15, z & 15);
	}

	public boolean isAirBlock(int x, int y, int z)
	{
		return this.getBlockState(x, y, z).getMaterial() == Material.AIR;
	}
}
