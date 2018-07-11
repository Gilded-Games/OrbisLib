package com.gildedgames.orbis_api.preparation.impl.util;

import com.gildedgames.orbis_api.preparation.IChunkMaskTransformer;
import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import com.gildedgames.orbis_api.preparation.IPrepSectorData;
import com.gildedgames.orbis_api.preparation.impl.ChunkMask;
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

import javax.annotation.Nullable;

public class BlockAccessPrep implements IBlockAccessExtended
{
	protected IChunkMaskTransformer transformer;

	private World world;

	private IPrepChunkManager chunkManager;

	private IPrepSectorData sectorData;

	public BlockAccessPrep(World world, IPrepSectorData sectorData, IPrepChunkManager iPrepChunkManager)
	{
		this.world = world;

		this.sectorData = sectorData;
		this.chunkManager = iPrepChunkManager;
		this.transformer = iPrepChunkManager.createMaskTransformer();
	}

	@Override
	@Nullable
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
		ChunkMask chunk = this.getChunk(x >> 4, z >> 4);

		for (int y = 255; y > 0; y--)
		{
			if (chunk.getBlock(x & 15, y, z & 15) > 0)
			{
				return y;
			}
		}

		return -1;
	}

	@Override
	public void setBlockToAir(BlockPos pos)
	{

	}

	@Override
	public boolean setBlockState(BlockPos pos, IBlockState state)
	{
		return false;
	}

	@Override
	public boolean setBlockState(BlockPos pos, IBlockState state, int flags)
	{
		return false;
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
		return null;
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
		ChunkMask chunk = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);

		return this.transformer.remapBlock(chunk.getBlock(pos.getX() & 15, pos.getY(), pos.getZ() & 15));
	}

	@Override
	public boolean isAirBlock(BlockPos pos)
	{
		return this.getBlockState(pos).getBlock() == Blocks.AIR;
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
		return null;
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default)
	{
		return false;
	}

	protected ChunkMask getChunk(int x, int z)
	{
		ChunkMask chunk = this.chunkManager.getChunk(this.sectorData, x, z);

		if (chunk == null)
		{
			throw new RuntimeException("ChunkMask is null at position: x(" + x + "), y(" + z + ")");
		}

		return chunk;
	}
}
