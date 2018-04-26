package com.gildedgames.orbis_api.preparation;

import com.gildedgames.orbis_api.util.mc.NBT;

/**
 * A sector of variable size which holds data for future generation.
 * These are used to prepare the terrain data in memory, then use it to
 * coordinate structures and other data ahead of time.
 *
 * Sectors do not hold any chunks, but rather just the data relevant to that sector
 * (such as Blueprints).
 */
public interface IPrepSectorData extends NBT
{
	IPrepSector setParent(IPrepSector sector);

	int getSectorX();

	int getSectorY();

	long getSeed();

	boolean shouldPrepareChunk(int chunkX, int chunkY);
}