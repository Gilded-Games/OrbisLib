package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.preparation.*;
import com.gildedgames.orbis_api.util.ChunkMap;
import com.gildedgames.orbis_api.world.WorldObjectManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

/**
 * Implementation of {@link IPrepSectorAccess} that uses a flat-file database. Not thread-safe!
 */
public class PrepSectorAccessFlatFile implements IPrepSectorAccess
{
	private final World world;

	private final IPrepRegistryEntry registry;

	private final IPrepManager prepManager;

	private final ChunkMap<IPrepSector> loaded = new ChunkMap<>();

	private final Queue<IPrepSector> dirty = new ArrayDeque<>();

	public PrepSectorAccessFlatFile(final World world, IPrepRegistryEntry registry, IPrepManager prepManager)
	{
		this.world = world;
		this.prepManager = prepManager;
		this.registry = registry;
	}

	@Override
	public Optional<IPrepSector> getLoadedSector(final int chunkX, final int chunkZ)
	{
		final int sectorX = Math.floorDiv(chunkX, this.registry.getSectorChunkArea());
		final int sectorZ = Math.floorDiv(chunkZ, this.registry.getSectorChunkArea());

		return Optional.ofNullable(this.loaded.get(sectorX, sectorZ));
	}

	@Override
	public IPrepSector provideSector(final int chunkX, final int chunkZ)
	{
		final int sectorX = Math.floorDiv(chunkX, this.registry.getSectorChunkArea());
		final int sectorZ = Math.floorDiv(chunkZ, this.registry.getSectorChunkArea());

		// Check if the sector is already loaded
		if (this.loaded.containsKey(sectorX, sectorZ))
		{
			return this.loaded.get(sectorX, sectorZ);
		}

		// Attempt to load sector from disk
		IPrepSectorData sectorData = this.prepManager.readSectorDataFromDisk(sectorX, sectorZ);

		// Return null since the sector hasn't been generated in the prep thread yet
		if (sectorData == null && !this.world.isRemote)
		{
			long worldSeed = WorldObjectManager.getWorldSeed(this.world.provider.getDimension());

			final long seed = worldSeed ^ ((long) sectorX * 341873128712L + (long) sectorZ * 132897987541L);

			sectorData = this.registry.createData(this.world, seed, sectorX, sectorZ);

			this.registry.postSectorDataCreate(this.world, sectorData, this.prepManager.getChunkManager());
		}

		IPrepSector sector = new PrepSector(sectorData);

		// Store the sector in memory
		this.loaded.put(sectorX, sectorZ, sector);

		// Queue the sector for saving if we just generated it
		if (sector.isDirty())
		{
			this.dirty.add(sector);
		}

		// Begin watching the sector
		sector.addWatchingChunk(chunkX, chunkZ);

		return sector;
	}

	@Override
	public void onChunkLoaded(final int chunkX, final int chunkZ)
	{
		final int sectorX = Math.floorDiv(chunkX, this.registry.getSectorChunkArea());
		final int sectorZ = Math.floorDiv(chunkZ, this.registry.getSectorChunkArea());

		// Check if the sector is already loaded
		if (this.loaded.containsKey(sectorX, sectorZ))
		{
			return;
		}

		// Attempt to load sector from disk
		final IPrepSectorData sectorData = this.prepManager.readSectorDataFromDisk(sectorX, sectorZ);

		if (sectorData != null)
		{
			IPrepSector sector = new PrepSector(sectorData);

			// Store the sector in memory
			this.loaded.put(sectorX, sectorZ, sector);

			// Begin watching the sector
			sector.addWatchingChunk(chunkX, chunkZ);
		}
	}

	@Override
	public void onChunkUnloaded(final int chunkX, final int chunkZ)
	{
		final int sectorX = Math.floorDiv(chunkX, this.registry.getSectorChunkArea());
		final int sectorZ = Math.floorDiv(chunkZ, this.registry.getSectorChunkArea());

		final IPrepSector sector = this.loaded.get(sectorX, sectorZ);

		if (sector != null)
		{
			sector.removeWatchingChunk(chunkX, chunkZ);

			if (!sector.hasWatchers())
			{
				// If the sector is dirty, queue it for saving, otherwise drop it
				if (sector.isDirty())
				{
					this.dirty.add(sector);
				}
				else
				{
					this.loaded.remove(sectorX, sectorZ);
				}
			}
		}
	}

	/**
	 * Saves all dirty sectors to the disk synchronously and unloads
	 * those of which have no players watching them.
	 */
	@Override
	public void flush()
	{
		while (!this.dirty.isEmpty())
		{
			final IPrepSector sector = this.dirty.remove();

			this.prepManager.writeSectorDataToDisk(sector.getData());

			sector.markClean();

			// If the sector has no watchers after flushing, remove it from cache
			if (!sector.hasWatchers())
			{
				synchronized (this.loaded)
				{
					this.loaded.remove(sector.getData().getSectorX(), sector.getData().getSectorY());
				}
			}
		}
	}

	public static class Storage implements Capability.IStorage<IPrepSectorAccess>
	{
		@Nullable
		@Override
		public NBTBase writeNBT(final Capability<IPrepSectorAccess> capability, final IPrepSectorAccess instance, final EnumFacing side)
		{
			return null;
		}

		@Override
		public void readNBT(final Capability<IPrepSectorAccess> capability, final IPrepSectorAccess instance, final EnumFacing side, final NBTBase nbt)
		{

		}
	}
}
