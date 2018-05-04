package com.gildedgames.orbis_api.preparation.impl.util;

import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import com.gildedgames.orbis_api.preparation.IPrepSectorData;
import com.gildedgames.orbis_api.processing.IBlockAccessExtended;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

import javax.annotation.Nullable;

public class BlockAccessPrep implements IBlockAccessExtended
{
	private World world;

	private IPrepChunkManager chunkManager;

	private IPrepSectorData sectorData;

	public BlockAccessPrep(World world, IPrepSectorData sectorData)
	{
		this.world = world;

		this.sectorData = sectorData;
		this.chunkManager = PrepHelper.getManager(world).getChunkManager();
	}

	@Nullable
	@Override
	public World getWorld()
	{
		return this.world;
	}

	@Override
	public boolean canAccess(BlockPos pos)
	{
		return true;
	}

	@Override
	public boolean canAccess(int x, int z)
	{
		return true;
	}

	@Override
	public boolean canAccess(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		return true;
	}

	@Override
	public BlockPos getTopPos(BlockPos pos)
	{
		return new BlockPos(pos.getX(), this.getTopY(pos.getX(), pos.getZ()), pos.getZ());
	}

	@Override
	public int getTopY(int x, int z)
	{
		ChunkPrimer chunk = this.getChunk(x, z);

		int xDif = x % 16;
		int zDif = z % 16;

		if (xDif < 0)
		{
			xDif = 16 - Math.abs(xDif);
		}

		if (zDif < 0)
		{
			zDif = 16 - Math.abs(zDif);
		}

		for (int y = this.world.getActualHeight() - 1; y > 0; y--)
		{
			IBlockState state = chunk.getBlockState(xDif, y, zDif);

			if (state != Blocks.AIR.getDefaultState())
			{
				return y;
			}
		}

		return 0;
	}

	@Override
	public void setBlockToAir(BlockPos pos)
	{
		this.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

	@Override
	public boolean setBlockState(BlockPos pos, IBlockState state)
	{
		ChunkPrimer chunk = this.getChunk(pos.getX(), pos.getZ());

		int xDif = pos.getX() % 16;
		int zDif = pos.getZ() % 16;

		if (xDif < 0)
		{
			xDif = 16 - Math.abs(xDif);
		}

		if (zDif < 0)
		{
			zDif = 16 - Math.abs(zDif);
		}

		chunk.setBlockState(xDif, pos.getY(), zDif, state);

		return true;
	}

	@Override
	public boolean setBlockState(BlockPos pos, IBlockState state, int flags)
	{
		return this.setBlockState(pos, state);
	}

	@Override
	public void setTileEntity(BlockPos pos, TileEntity tileEntity)
	{

	}

	@Override
	public void spawnEntity(Entity entity)
	{

	}

	@Override
	public Biome getServerBiome(BlockPos pos)
	{
		return this.world.getBiome(pos);
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(BlockPos pos)
	{
		return null;
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue)
	{
		return 0;
	}

	@Override
	public IBlockState getBlockState(BlockPos pos)
	{
		ChunkPrimer chunk = this.getChunk(pos.getX(), pos.getZ());

		int xDif = pos.getX() % 16;
		int zDif = pos.getZ() % 16;

		if (xDif < 0)
		{
			xDif = 16 - Math.abs(xDif);
		}

		if (zDif < 0)
		{
			zDif = 16 - Math.abs(zDif);
		}

		return chunk.getBlockState(xDif, pos.getY(), zDif);
	}

	@Override
	public boolean isAirBlock(BlockPos pos)
	{
		IBlockState state = this.getBlockState(pos);

		return state.getBlock().isAir(state, this, pos);
	}

	@Override
	public Biome getBiome(BlockPos pos)
	{
		return this.world.getBiome(pos);
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction)
	{
		return 0;
	}

	@Override
	public WorldType getWorldType()
	{
		return this.world.getWorldType();
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default)
	{
		if (!this.world.isValid(pos))
		{
			return _default;
		}

		ChunkPrimer chunk = this.getChunk(pos.getX(), pos.getZ());

		if (chunk == null)
		{
			return _default;
		}

		return this.getBlockState(pos).isSideSolid(this, pos, side);
	}

	private ChunkPrimer getChunk(int x, int z)
	{
		ChunkPrimer chunk = this.chunkManager.getChunk(this.sectorData, x >> 4, z >> 4);

		if (chunk == null)
		{
			throw new RuntimeException("Chunk is null at position: x(" + x + "), y(" + z + ")");
		}

		return chunk;
	}
}
