package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.preparation.IChunkMaskTransformer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ChunkDataContainer
{
	private final ExtendedBlockStorage[] segments = new ExtendedBlockStorage[16];

	private final HashMap<BlockPos, TileEntity> tileEntities = new HashMap<>();

	private final ArrayList<Entity> entities = new ArrayList<>();

	private final int chunkX, chunkZ;

	private final boolean hasSkylight;

	public ChunkDataContainer(int chunkX, int chunkZ, boolean hasSkylight)
	{
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.hasSkylight = hasSkylight;
	}

	public IBlockState getBlockState(final BlockPos pos)
	{
		return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
	}

	public IBlockState getBlockState(final int x, final int y, final int z)
	{
		ExtendedBlockStorage segment = this.segments[y >> 4];

		if (segment == null)
		{
			return Blocks.AIR.getDefaultState();
		}

		return segment.get(x, y & 15, z);
	}

	public void setBlockState(final int x, final int y, final int z, final IBlockState state)
	{
		ExtendedBlockStorage segment = this.segments[y >> 4];

		if (segment == null)
		{
			this.segments[y >> 4] = segment = new ExtendedBlockStorage((y >> 4) * 16, this.hasSkylight);
		}

		segment.set(x, y & 15, z, state);
	}

	public void setBlockState(final BlockPos pos, final IBlockState state)
	{
		this.setBlockState(pos.getX(), pos.getY(), pos.getZ(), state);
	}

	public TileEntity getTileEntity(BlockPos pos)
	{
		return this.tileEntities.get(pos);
	}

	public void setTileEntity(BlockPos pos, TileEntity entity)
	{
		if (entity == null)
		{
			this.tileEntities.remove(pos);
		}
		else
		{
			entity.setPos(pos);

			this.tileEntities.put(pos, entity);
		}
	}

	public static ChunkDataContainer createFromChunkSegmentMasks(World world, ChunkSegmentMask[] masks, IChunkMaskTransformer transformer, int chunkX, int chunkZ)
	{
		ChunkDataContainer container = new ChunkDataContainer(chunkX, chunkZ, world.provider.hasSkyLight());

		BlockStateCacher cacher = new BlockStateCacher(transformer);

		for (int chunkY = 0; chunkY < 16; chunkY++)
		{
			ChunkSegmentMask mask = masks[chunkY];

			if (mask == null || !mask.wasTouched())
			{
				continue;
			}

			ExtendedBlockStorage segment = null;

			for (int x = 0; x < 16; x++)
			{
				for (int z = 0; z < 16; z++)
				{
					for (int y = 0; y < 16; y++)
					{
						int block = mask.getBlock(x, y, z);

						if (block == 0)
						{
							continue;
						}

						if (segment == null)
						{
							segment = new ExtendedBlockStorage(chunkY << 4, world.provider.hasSkyLight());

							cacher.update(segment.data);
						}

						int key = cacher.getValue(transformer, block);

						segment.data.storage.setAt(y << 8 | z << 4 | x, key);
						segment.blockRefCount++;
					}
				}
			}

			container.segments[chunkY] = segment;
		}

		return container;
	}

	public Chunk createChunk(World world, int chunkX, int chunkZ)
	{
		Chunk chunk = new Chunk(world, chunkX, chunkZ);

		for (int chunkY = 0; chunkY < 16; chunkY++)
		{
			ExtendedBlockStorage segment = this.segments[chunkY];

			if (segment == null)
			{
				continue;
			}

			chunk.getBlockStorageArray()[chunkY] = segment;
		}

		for (TileEntity tileEntity : this.tileEntities.values())
		{
			chunk.addTileEntity(tileEntity.getPos(), tileEntity);
		}

		for (Entity entity : this.entities)
		{
			chunk.addEntity(entity);
		}

		return chunk;
	}

	public void addEntity(Entity entity)
	{
		this.entities.add(entity);
	}

	public int getChunkX()
	{
		return this.chunkX;
	}

	public int getChunkZ()
	{
		return this.chunkZ;
	}

	private static class BlockStateCacher
	{
		private final int[] cache;

		private BlockStateContainer container;

		public BlockStateCacher(IChunkMaskTransformer transformer)
		{
			this.cache = new int[transformer.getBlockCount()];
		}

		public void update(BlockStateContainer container)
		{
			this.container = container;

			this.reset();
		}

		public int getValue(IChunkMaskTransformer transformer, int index)
		{
			if (this.container == null)
			{
				throw new IllegalStateException("Not yet initialized");
			}

			int state = this.cache[index];

			if (state < 0)
			{
				int bits = this.container.bits;

				state = this.container.palette.idFor(transformer.getBlockState(index));

				if (bits != this.container.bits)
				{
					this.reset();
				}

				this.cache[index] = state;
			}

			return state;
		}

		private void reset()
		{
			Arrays.fill(this.cache, -1);
		}

	}
}
