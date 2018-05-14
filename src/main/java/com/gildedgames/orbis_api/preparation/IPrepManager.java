package com.gildedgames.orbis_api.preparation;

import com.google.common.util.concurrent.ListenableFuture;

public interface IPrepManager
{
	IPrepRegistryEntry getRegistryEntry();

	IPrepChunkManager getChunkManager();

	IPrepSectorAccessAsync access();

	ListenableFuture<IPrepSectorData> createSector(int sectorX, int sectorZ);
}
