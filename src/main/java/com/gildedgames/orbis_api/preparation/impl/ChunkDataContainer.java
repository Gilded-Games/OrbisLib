package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.preparation.IChunkMaskTransformer;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IBlockStatePalette;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.ArrayList;
import java.util.HashMap;

public class ChunkDataContainer
{
	private final short[] blocks = new short[16 * 256 * 16];

	private final byte[] blocksMeta = new byte[16 * 256 * 16];

	private final HashMap<BlockPos, TileEntity> tileEntities = new HashMap<>();

	private final ArrayList<Entity> entities = new ArrayList<>();

	private final int chunkX, chunkZ;

	public ChunkDataContainer(int chunkX, int chunkZ)
	{
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	private int getIndex(int x, int y, int z)
	{
		return y << 8 | z << 4 | x;
	}

	public IBlockState getBlockState(final BlockPos pos)
	{
		return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
	}

	public IBlockState getBlockState(final int x, final int y, final int z)
	{
		return this.getBlockState(this.getIndex(x, y, z));
	}

	private IBlockState getBlockState(final int index)
	{
		int blockID = this.blocks[index];
		int blockMeta = this.blocksMeta[index];

		Block block = Block.getBlockById(blockID);
		return block.getStateFromMeta(blockMeta);
	}

	public Block getBlock(BlockPos pos)
	{
		return this.getBlock(this.getIndex(pos.getX(), pos.getY(), pos.getZ()));
	}

	public Block getBlock(int x, int y, int z)
	{
		return this.getBlock(this.getIndex(x, y, z));
	}

	private Block getBlock(int index)
	{
		return Block.getBlockById(this.blocks[index]);
	}

	public void setBlockState(final int x, final int y, final int z, final IBlockState state)
	{
		int id = Block.getIdFromBlock(state.getBlock());

		final int index = this.getIndex(x, y, z);

		this.blocks[index] = (short) id;
		this.blocksMeta[index] = (byte) state.getBlock().getMetaFromState(state);
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
			this.tileEntities.put(pos, entity);
		}
	}

	public static ChunkDataContainer createFromChunkSegmentMasks(ChunkSegmentMask[] masks, IChunkMaskTransformer transformer, int chunkX, int chunkZ)
	{
		ChunkDataContainer container = new ChunkDataContainer(chunkX, chunkZ);

		for (int chunkY = 0; chunkY < 16; chunkY++)
		{
			ChunkSegmentMask mask = masks[chunkY];

			if (mask == null)
			{
				continue;
			}

			for (int x = 0; x < 16; x++)
			{
				for (int z = 0; z < 16; z++)
				{
					for (int y = 0; y < 16; y++)
					{
						int raw = mask.getBlock(x, y, z);

						int index = container.getIndex(x, y + (chunkY * 16), z);
						container.blocks[index] = (short) transformer.getBlockID(raw);
						container.blocksMeta[index] = (byte) transformer.getBlockMeta(raw);
					}
				}
			}
		}

		return container;
	}

	public Chunk createChunk(World world, int chunkX, int chunkZ)
	{
		Chunk chunk = new Chunk(world, chunkX, chunkZ);

		boolean flag = chunk.getWorld().provider.hasSkyLight();

		for (int chunkY = 0; chunkY < 16; chunkY++)
		{
			ExtendedBlockStorage chunkBlocks = chunk.getBlockStorageArray()[chunkY];

			BlockStateContainer container = null;
			BlockStateCacher cacher = null;

			if (chunkBlocks != Chunk.NULL_BLOCK_STORAGE)
			{
				container = chunkBlocks.getData();

				cacher = new BlockStateCacher(container.palette, container.bits);
			}

			for (int y = 0; y < 16; y++)
			{
				for (int z = 0; z < 16; z++)
				{
					for (int x = 0; x < 16; x++)
					{
						int index = this.getIndex(x, y + (chunkY * 16), z);

						int blockID = this.blocks[index];

						if (blockID == 0)
						{
							continue;
						}

						if (chunkBlocks == Chunk.NULL_BLOCK_STORAGE)
						{
							chunkBlocks = new ExtendedBlockStorage(chunkY << 4, flag);
							container = chunkBlocks.getData();

							chunk.getBlockStorageArray()[chunkY] = chunkBlocks;

							cacher = new BlockStateCacher(container.palette, container.bits);
						}

						int blockMeta = this.blocksMeta[index];

						int val = cacher.getBlockState(blockID, blockMeta);

						if (cacher.bits != container.bits)
						{
							cacher = new BlockStateCacher(container.palette, container.bits);
						}

						chunkBlocks.blockRefCount++;

						container.storage.setAt(y << 8 | z << 4 | x, val);
					}
				}
			}
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

	// I hate Minecraft.
	private class BlockStateCacher
	{
		private final Int2IntOpenHashMap cache = new Int2IntOpenHashMap();

		private final IBlockStatePalette palette;

		private final int bits;

		public BlockStateCacher(IBlockStatePalette palette, int bits)
		{
			this.cache.defaultReturnValue(-1);

			this.palette = palette;
			this.bits = bits;
		}

		public int getBlockState(int blockID, int blockMeta)
		{
			int index = blockID << 4 | blockMeta;

			int state = this.cache.get(index);

			if (state < 0)
			{
				state = this.palette.idFor(Block.getBlockById(blockID).getStateFromMeta(blockMeta));

				this.cache.put(index, state);
			}

			return state;
		}

	}
}
