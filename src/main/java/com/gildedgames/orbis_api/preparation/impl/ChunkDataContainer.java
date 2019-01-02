package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.preparation.IChunkMaskTransformer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
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
		return x << 12 | z << 8 | y;
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

		for (int y = 0; y < 256; y++)
		{
			ExtendedBlockStorage chunkBlocks = chunk.getBlockStorageArray()[y >> 4];

			for (int x = 0; x < 16; x++)
			{
				for (int z = 0; z < 16; z++)
				{
					Block block = this.getBlock(x, y, z);

					if (block != Blocks.AIR)
					{
						if (chunkBlocks == Chunk.NULL_BLOCK_STORAGE)
						{
							chunk.getBlockStorageArray()[y >> 4] = chunkBlocks = new ExtendedBlockStorage(y & 0xF0, flag);
						}

						chunkBlocks.set(x, y & 15, z, this.getBlockState(x, y, z));
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
}
