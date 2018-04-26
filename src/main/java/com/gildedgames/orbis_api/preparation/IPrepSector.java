package com.gildedgames.orbis_api.preparation;

public interface IPrepSector
{
	/**
	 * @return This sector's data.
	 */
	IPrepSectorData getData();

	/**
	 * @return True if this sector should be saved again, such as if it's data has changed.
	 */
	boolean isDirty();

	/**
	 * Marks the sector as dirty for saving later.
	 */
	void markDirty();

	/**
	 * Marks the sector as clean. Should be called after saving.
	 */
	void markClean();

	/**
	 * Adds a belonging loaded chunk to this sector.
	 * @param chunkX The chunk's x-coordinate
	 * @param chunkZ The chunk's z-coordinate
	 */
	void addWatchingChunk(int chunkX, int chunkZ);

	/**
	 * Removes a loaded chunk belonging to this sector.
	 * @param chunkX The chunk's x-coordinate
	 * @param chunkZ The chunk's z-coordinate
	 */
	void removeWatchingChunk(int chunkX, int chunkZ);

	/**
	 * @return True if the sector has currently watching chunks
	 */
	boolean hasWatchers();
}
