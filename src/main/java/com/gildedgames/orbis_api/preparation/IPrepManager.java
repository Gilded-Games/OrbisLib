package com.gildedgames.orbis_api.preparation;

public interface IPrepManager
{

	boolean isSectorPreparing(int sectorX, int sectorY);

	void markSectorPreparing(int sectorX, int sectorY);

	void unmarkSectorPreparing(int sectorX, int sectorY);

	boolean isSectorWrittenToDisk(int sectorX, int sectorY);

	void writeSectorDataToDisk(IPrepSectorData sector);

	IPrepSectorData readSectorDataFromDisk(int sectorX, int sectorY);

	IPrepSectorAccess access();

}
