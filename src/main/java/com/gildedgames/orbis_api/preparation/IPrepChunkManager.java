package com.gildedgames.orbis_api.preparation;

import com.gildedgames.orbis_api.preparation.impl.ChunkMask;
import net.minecraft.world.World;

/**
 * Actually manages the chunks within a world.
 */
public interface IPrepChunkManager
{
	World getWorld();

	ChunkMask getChunk(IPrepSectorData sectorData, final int chunkX, final int chunkY);

	IChunkMaskTransformer createMaskTransformer();
}
