package com.gildedgames.orbis_api.preparation;

import com.gildedgames.orbis_api.preparation.impl.ChunkSegmentMask;
import net.minecraft.world.World;

/**
 * Actually manages the chunks within a world.
 */
public interface IPrepChunkManager<T extends IChunkColumnInfo>
{
	World getWorld();

	ChunkSegmentMask getChunk(IPrepSectorData sectorData, final int chunkX, final int chunkY, final int chunkZ);

	T getChunkColumnInfo(IPrepSectorData sectorData, final int chunkX, final int chunkY);

	IChunkMaskTransformer createMaskTransformer();
}
