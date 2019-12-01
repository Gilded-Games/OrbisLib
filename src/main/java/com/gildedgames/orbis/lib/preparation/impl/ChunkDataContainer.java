package com.gildedgames.orbis.lib.preparation.impl;

import com.gildedgames.orbis.lib.preparation.IChunkMaskTransformer;
import com.gildedgames.orbis.lib.processing.IBlockAccess;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import java.util.ArrayList;
import java.util.HashMap;

public class ChunkDataContainer implements IBlockAccess
{
	private final ChunkSection[] segments = new ChunkSection[16];

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

	@Override
	public BlockState getBlockState(final BlockPos pos)
	{
		return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public IFluidState getFluidState(BlockPos pos)
	{
		return null;
	}

	public BlockState getBlockState(final int x, final int y, final int z)
	{
		ChunkSection segment = this.segments[y >> 4];

		if (segment == null)
		{
			return Blocks.AIR.getDefaultState();
		}

		return segment.getBlockState(x, y & 15, z);
	}

	public boolean setBlockState(final int x, final int y, final int z, final BlockState state)
	{
		ChunkSection segment = this.segments[y >> 4];

		if (segment == null)
		{
			this.segments[y >> 4] = segment = new ChunkSection((y >> 4) * 16);
		}

		segment.setBlockState(x, y & 15, z, state);

		return true;
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags)
	{
		return this.setBlockState(pos.getX(), pos.getY(), pos.getZ(), state);
	}

	@Override
	public boolean removeBlock(BlockPos pos, boolean isMoving)
	{
		// TODO: Implement liquids
		return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
	}

	@Override
	public boolean destroyBlock(BlockPos pos, boolean dropBlock)
	{
		return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
	}

	@Override
	public TileEntity getTileEntity(BlockPos pos)
	{
		return this.tileEntities.get(pos);
	}

	@Override
	public boolean isAreaLoaded(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public World getWorld()
	{
		throw new UnsupportedOperationException();
	}

	@Override
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

	public static ChunkDataContainer createFromMask(World world, ChunkMask mask, IChunkMaskTransformer transformer, int chunkX, int chunkZ)
	{
		ChunkDataContainer container = new ChunkDataContainer(chunkX, chunkZ, world.dimension.hasSkyLight());

		for (int chunkY = 0; chunkY < 32; chunkY++)
		{
			ChunkMaskSegment src = mask.getSegment(chunkY);

			if (src == null)
			{
				continue;
			}

			ChunkSection dest = container.segments[chunkY >> 1];

			if (dest == null)
			{
				dest = new ChunkSection((chunkY >> 1) << 4);

				container.segments[chunkY >> 1] = dest;
			}

			for (int x = 0; x < 16; x++)
			{
				for (int z = 0; z < 16; z++)
				{
					for (int y = 0, y2 = (chunkY & 0b1) * 8; y < 8; y++, y2++)
					{
						int block = src.getBlock(x, y, z);

						if (block == 0)
						{
							continue;
						}

						dest.setBlockState(x, y2, z, transformer.getBlockState(block));
					}
				}
			}
		}

		return container;
	}

	public Chunk createChunk(World world, ChunkPos chunkPos)
	{
		Chunk chunk = new Chunk(world, chunkPos, new Biome[256]);

		for (int chunkY = 0; chunkY < 16; chunkY++)
		{
			ChunkSection segment = this.segments[chunkY];

			if (segment == null)
			{
				continue;
			}

			chunk.getSections()[chunkY] = segment;
		}

		for (TileEntity tileEntity : this.tileEntities.values())
		{
			chunk.addTileEntity(tileEntity.getPos(), tileEntity);
		}

		for (Entity entity : this.entities)
		{
			chunk.addEntity(entity);
		}

//		TODO: What happened to generateSkylightMap?
//		chunk.generateSkylightMap();

		return chunk;
	}

	@Override
	public boolean addEntity(Entity entity)
	{
		this.entities.add(entity);

		return true;
	}

	public int getChunkX()
	{
		return this.chunkX;
	}

	public int getChunkZ()
	{
		return this.chunkZ;
	}}
