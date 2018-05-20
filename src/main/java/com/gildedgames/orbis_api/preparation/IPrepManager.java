package com.gildedgames.orbis_api.preparation;

public interface IPrepManager
{
	IPrepRegistryEntry getRegistryEntry();

	IPrepChunkManager getChunkManager();

	IPrepSectorAccessAsync access();

	IPrepSectorData createSector(int sectorX, int sectorZ);

	void decorateSectorData(IPrepSectorData data);
}
