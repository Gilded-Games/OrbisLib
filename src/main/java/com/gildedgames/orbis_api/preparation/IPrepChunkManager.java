package com.gildedgames.orbis_api.preparation;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import javax.annotation.Nullable;

/**
 * Actually manages the chunks within a world.
 */
public interface IPrepChunkManager
{
	World getWorld();

	@Nullable
	ChunkPrimer getChunk(IPrepSectorData sectorData, final int chunkX, final int chunkY);

	IBlockState getPreparedState(IPrepSectorData sectorData, final int x, final int y, final int z);

	/**
	 * Sets a state globally in the sector. If the position
	 * lies outside of the sector, it will return false and
	 * do nothing.
	 * @param x Global x position.
	 * @param y Global y position.
	 * @param z Global z position.
	 * @param state The state being set in this sector.
	 * @return Whether or not the block was successfully set.
	 */
	boolean setPreparedState(IPrepSectorData sectorData, int x, int y, int z, IBlockState state);
}
