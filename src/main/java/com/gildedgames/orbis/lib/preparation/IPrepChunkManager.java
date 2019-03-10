package com.gildedgames.orbis.lib.preparation;

import com.gildedgames.orbis.lib.preparation.impl.ChunkMask;
import net.minecraft.world.World;

/**
 * Actually manages the chunks within a world.
 */
public interface IPrepChunkManager<T extends IChunkColumnInfo>
{
	World getWorld();

	ChunkMask getChunk(IPrepSectorData sectorData, final int chunkX, final int chunkZ);

	IChunkMaskTransformer createMaskTransformer();
}
