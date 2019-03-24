package com.gildedgames.orbis.lib.preparation.impl.util;

import com.gildedgames.orbis.lib.preparation.*;
import com.gildedgames.orbis.lib.preparation.impl.ChunkMask;
import com.gildedgames.orbis.lib.preparation.impl.capability.PrepChunkManager;
import com.gildedgames.orbis.lib.processing.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockAccessPrep implements IBlockAccess
{
	protected IChunkMaskTransformer transformer;

	private World world;

	private IPrepChunkManager<? extends IChunkColumnInfo> chunkManager;

	private IPrepSectorData sectorData;

	public BlockAccessPrep(World world, IPrepSectorData sectorData, IPrepRegistryEntry<? extends IChunkColumnInfo> registryEntry)
	{
		this.world = world;

		this.sectorData = sectorData;

		this.chunkManager = new PrepChunkManager<>(world, registryEntry);
		this.transformer = this.chunkManager.createMaskTransformer();
	}

	@Override
	public boolean isAreaLoaded(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		return true;
	}

	@Override
	@Nullable
	public World getWorld()
	{
		return this.world;
	}

	@Override
	public void setTileEntity(BlockPos pos, TileEntity te)
	{

	}

	protected ChunkMask getChunk(BlockPos pos)
	{
		return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}

	protected ChunkMask getChunk(int x, int z)
	{
		ChunkMask chunk = this.chunkManager.getChunk(this.sectorData, x, z);

		if (chunk == null)
		{
			throw new RuntimeException("ChunkMask is null at position: x(" + x + "), z(" + z + ")");
		}

		return chunk;
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(BlockPos pos)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public IBlockState getBlockState(BlockPos pos)
	{
		return this.transformer.getBlockState(this.getChunk(pos).getBlock(pos));
	}

	@Override
	public IFluidState getFluidState(BlockPos pos)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setBlockState(BlockPos pos, IBlockState newState, int flags)
	{
		return false;
	}

	@Override
	public boolean spawnEntity(Entity entityIn)
	{
		return false;
	}

	@Override
	public boolean removeBlock(BlockPos pos)
	{
		return false;
	}

	@Override
	public void setLightFor(EnumLightType type, BlockPos pos, int lightValue)
	{

	}

	@Override
	public boolean destroyBlock(BlockPos pos, boolean dropBlock)
	{
		return false;
	}
}
