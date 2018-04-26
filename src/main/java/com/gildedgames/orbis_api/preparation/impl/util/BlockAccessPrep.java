package com.gildedgames.orbis_api.preparation.impl.util;

import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import com.gildedgames.orbis_api.preparation.impl.capability.PrepHelper;
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
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;

public class BlockAccessPrep implements IBlockAccessExtended
{
	private World world;

	private IPrepChunkManager chunkManager;

	public BlockAccessPrep(World world)
	{
		this.world = world;

		this.chunkManager = PrepHelper.getChunks(world);
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
		Chunk chunk = this.chunkManager.getChunk(x >> 4, z >> 4);

		if (chunk == null)
		{
			throw new RuntimeException("Chunk is null at position: x(" + x + "), y(" + z + ")");
		}

		return chunk.getHeightValue(x & 15, z & 15);
	}

	@Override
	public void setBlockToAir(BlockPos pos)
	{
		Chunk chunk = this.getChunk(pos);

		chunk.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

	@Override
	public boolean setBlockState(BlockPos pos, IBlockState state)
	{
		Chunk chunk = this.getChunk(pos);

		chunk.setBlockState(pos, state);

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
		Chunk chunk = this.getChunk(pos);

		return chunk.getBlockState(pos);
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

		Chunk chunk = this.getChunk(pos);

		if (chunk == null || chunk.isEmpty())
		{
			return _default;
		}

		return this.getBlockState(pos).isSideSolid(this, pos, side);
	}

	private Chunk getChunk(BlockPos pos)
	{
		Chunk chunk = this.chunkManager.getChunk(pos.getX() >> 4, pos.getZ() >> 4);

		if (chunk == null)
		{
			throw new RuntimeException("Chunk is null at position: x(" + pos.getX() + "), y(" + pos.getZ() + ")");
		}

		return chunk;
	}
}
