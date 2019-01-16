package com.gildedgames.orbis_api.processing;

import com.gildedgames.orbis_api.world.WorldSlice;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public class BlockAccessWorldSlice implements IBlockAccessExtended
{
	private final World world;

	private final WorldSlice slice;

	public BlockAccessWorldSlice(final World world, final ChunkPos pos)
	{
		this.world = world;

		this.slice = new WorldSlice(this.world, pos);
	}

	@Nullable
	@Override
	public World getWorld()
	{
		return this.world;
	}

	@Override
	public boolean canAccess(final BlockPos pos)
	{
		return this.slice.isBlockWithin(pos);
	}

	@Override
	public boolean canAccess(BlockPos pos, int radius)
	{
		return this.slice.isAreaWithin(pos, radius);
	}

	@Override
	public boolean canAccess(final int x, final int z)
	{
		return this.slice.isBlockWithin(x, 0, z);
	}

	@Override
	public boolean canAccess(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		return this.slice.isAreaWithin(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public BlockPos getTopPos(final BlockPos pos)
	{
		return this.world.getTopSolidOrLiquidBlock(pos);
	}

	@Override
	public int getTopY(final int x, final int z)
	{
		return this.world.getHeight(x, z);
	}

	@Override
	public void setBlockToAir(final BlockPos pos)
	{
		this.slice.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

	@Override
	public boolean setBlockState(final BlockPos pos, final IBlockState state)
	{
		return this.slice.setBlockState(pos, state);
	}

	@Override
	public boolean setBlockState(final BlockPos pos, final IBlockState state, final int flags)
	{
		return this.setBlockState(pos, state);
	}

	@Override
	public void setTileEntity(final BlockPos pos, final TileEntity tileEntity)
	{
		this.world.setTileEntity(pos, tileEntity);
	}

	@Override
	public void spawnEntity(Entity entity)
	{
		this.world.spawnEntity(entity);
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(final BlockPos pos)
	{
		return this.world.getTileEntity(pos);
	}

	@Override
	public int getCombinedLight(final BlockPos pos, final int lightValue)
	{
		return this.world.getCombinedLight(pos, lightValue);
	}

	@Override
	public IBlockState getBlockState(final BlockPos pos)
	{
		return this.slice.getBlockState(pos);
	}

	@Override
	public boolean isAirBlock(final BlockPos pos)
	{
		return this.slice.isAirBlock(pos);
	}

	@Override
	public Biome getBiome(final BlockPos pos)
	{
		return this.world.getBiome(pos);
	}

	@Override
	public int getStrongPower(final BlockPos pos, final EnumFacing direction)
	{
		return this.world.getStrongPower(pos, direction);
	}

	@Override
	public WorldType getWorldType()
	{
		return this.world.getWorldType();
	}

	@Override
	public boolean isSideSolid(final BlockPos pos, final EnumFacing side, final boolean _default)
	{
		return this.world.isSideSolid(pos, side, _default);
	}
}
