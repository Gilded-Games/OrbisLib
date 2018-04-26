package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import com.gildedgames.orbis_api.preparation.IPrepManager;
import com.gildedgames.orbis_api.preparation.IPrepRegistryEntry;
import com.gildedgames.orbis_api.preparation.IPrepSectorData;
import com.gildedgames.orbis_api.world.WorldObjectManager;
import net.minecraft.world.World;

public class PrepareSectorTask implements Runnable
{
	private IPrepRegistryEntry registry;

	private IPrepManager manager;

	private IPrepChunkManager chunkManager;

	private World world;

	private int sectorX, sectorY;

	public PrepareSectorTask(IPrepRegistryEntry registration, IPrepManager manager, IPrepChunkManager chunkManager, World world, int sectorX, int sectorY)
	{
		this.registry = registration;
		this.manager = manager;
		this.chunkManager = chunkManager;

		this.world = world;

		this.sectorX = sectorX;
		this.sectorY = sectorY;
	}

	@Override
	public void run()
	{
		IPrepSectorData sectorData = this.createSectorData();

		this.manager.markSectorPreparing(this.sectorX, this.sectorY);

		this.registry.preSectorDataSave(this.world, sectorData, this.chunkManager);

		this.manager.writeSectorDataToDisk(sectorData);

		this.registry.postSectorDataSave(this.world, sectorData, this.chunkManager);

		this.manager.writeSectorDataToDisk(sectorData);

		this.manager.unmarkSectorPreparing(this.sectorX, this.sectorY);
	}

	private IPrepSectorData createSectorData()
	{
		long worldSeed = WorldObjectManager.getWorldSeed(this.world.provider.getDimension());

		final long seed = worldSeed ^ ((long) this.sectorX * 341873128712L + (long) this.sectorY * 132897987541L);

		return this.registry.createData(this.world, seed, this.sectorX, this.sectorY);
	}
}
