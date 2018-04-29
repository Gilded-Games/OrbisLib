package com.gildedgames.orbis_api.preparation;

public interface IPrepManager
{
	IPrepRegistryEntry getRegistryEntry();

	IPrepChunkManager getChunkManager();

	boolean isSectorPreparing(int sectorX, int sectorY);

	void markSectorPreparing(int sectorX, int sectorY);

	void unmarkSectorPreparing(int sectorX, int sectorY);

	boolean isSectorWrittenToDisk(int sectorX, int sectorY);

	/**
	 * Must be thread-safe.
	 * @param sector The sector we're writing to disk.
	 */
	void writeSectorDataToDisk(IPrepSectorData sector);

	/**
	 * Must be thread-safe.
	 * @param sectorX X position of the sector.
	 * @param sectorY Y position of the sector.
	 * @return The sector we want to read back.
	 */
	IPrepSectorData readSectorDataFromDisk(int sectorX, int sectorY);

	IPrepSectorAccess access();

}
