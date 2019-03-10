package com.gildedgames.orbis.lib.preparation;

public interface IPrepManager
{
	IPrepRegistryEntry getRegistryEntry();

	IPrepSectorAccess getAccess();

	IPrepSectorData createSector(int sectorX, int sectorZ);

	void decorateSectorData(IPrepSectorData data);
}
