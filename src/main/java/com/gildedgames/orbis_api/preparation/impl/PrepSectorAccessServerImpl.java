package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.preparation.*;
import com.gildedgames.orbis_api.util.ChunkMap;
import com.gildedgames.orbis_api.world.data.IWorldData;
import com.gildedgames.orbis_api.world.data.IWorldDataManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.*;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

public class PrepSectorAccessServerImpl implements IPrepSectorAccess, IWorldData
{
	private static final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Sector Access %d").build();

	private final World world;

	private final ListeningExecutorService service =
			MoreExecutors.listeningDecorator(new ThreadPoolExecutor(0, 1, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), threadFactory));

	private final IPrepRegistryEntry registry;

	private final IPrepManager prepManager;

	private final ChunkMap<IPrepSector> generating = new ChunkMap<>();

	private final LoadingCache<ChunkPos, IPrepSector> dormantCache = CacheBuilder.newBuilder()
			.maximumSize(32)
			.expireAfterAccess(2, TimeUnit.MINUTES)
			.build(new CacheLoader<ChunkPos, IPrepSector>()
			{
				@Override
				public IPrepSector load(@Nonnull ChunkPos key) throws Exception
				{
					IPrepSector sector;

					synchronized (PrepSectorAccessServerImpl.this.loaded)
					{
						sector = PrepSectorAccessServerImpl.this.loaded.get(key.x, key.z);
					}

					if (sector == null)
					{
						return PrepSectorAccessServerImpl.this.loadSector(key.x, key.z);
					}

					return sector;
				}
			});

	private final ChunkMap<IPrepSector> loaded = new ChunkMap<>();

	private final IWorldDataManager dataManager;

	public PrepSectorAccessServerImpl(World world, IPrepRegistryEntry registry, IPrepManager prepManager, IWorldDataManager dataManager)
	{
		this.world = world;
		this.prepManager = prepManager;
		this.registry = registry;

		this.dataManager = dataManager;
		this.dataManager.register(this);
	}

	@Override
	public Optional<IPrepSector> getLoadedSector(int sectorX, int sectorZ)
	{
		if (this.world.isRemote)
		{
			return Optional.empty();
		}

		synchronized (this.generating)
		{
			IPrepSector job = PrepSectorAccessServerImpl.this.generating.get(sectorX, sectorZ);

			if (job != null)
			{
				return Optional.of(job);
			}
		}

		return Optional.ofNullable(this.dormantCache.getIfPresent(new ChunkPos(sectorX, sectorZ)));
	}

	@Override
	public Optional<IPrepSector> getLoadedSectorForChunk(final int chunkX, final int chunkZ)
	{
		final int sectorX = Math.floorDiv(chunkX, this.registry.getSectorChunkArea());
		final int sectorZ = Math.floorDiv(chunkZ, this.registry.getSectorChunkArea());

		return this.getLoadedSector(sectorX, sectorZ);
	}

	@Override
	public ListenableFuture<IPrepSector> provideSector(int sectorX, int sectorZ)
	{
		Optional<IPrepSector> sector = this.getLoadedSector(sectorX, sectorZ);

		if (sector.isPresent())
		{
			return Futures.immediateFuture(sector.get());
		}

		return this.service.submit(() -> PrepSectorAccessServerImpl.this.dormantCache.get(new ChunkPos(sectorX, sectorZ)));
	}

	@Override
	public ListenableFuture<IPrepSector> provideSectorForChunk(final int chunkX, final int chunkZ)
	{
		final int sectorX = Math.floorDiv(chunkX, this.registry.getSectorChunkArea());
		final int sectorZ = Math.floorDiv(chunkZ, this.registry.getSectorChunkArea());

		return this.provideSector(sectorX, sectorZ);
	}

	private IPrepSector loadSector(int sectorX, int sectorZ) throws Exception
	{
		if (this.world.isRemote)
		{
			return null;
		}

		IPrepSectorData data = this.readSectorDataFromDisk(sectorX, sectorZ);

		PrepSector sector;

		if (data != null)
		{
			sector = new PrepSector(data);

			OrbisAPI.LOGGER.info("Loaded Sector (" + sectorX + ", " + sectorZ + ") from disk");
		}
		else
		{
			OrbisAPI.LOGGER.info("Generating Sector (" + sectorX + ", " + sectorZ + ")");

			data = this.prepManager.createSector(sectorX, sectorZ);
			sector = new PrepSector(data);

			synchronized (this.generating)
			{
				this.generating.put(sectorX, sectorZ, sector);
			}

			this.prepManager.decorateSectorData(data);

			synchronized (this.generating)
			{
				this.generating.remove(sectorX, sectorZ);
			}

			sector.getData().markDirty();
		}

		return sector;
	}

	@Override
	public void onChunkLoaded(final int chunkX, final int chunkZ)
	{
		try
		{
			IPrepSector sector = this.provideSectorForChunk(chunkX, chunkZ).get();

			if (sector == null)
			{
				return;
			}

			int sectorX = sector.getData().getSectorX();
			int sectorZ = sector.getData().getSectorY();

			if (!PrepSectorAccessServerImpl.this.loaded.containsKey(sectorX, sectorZ))
			{
				PrepSectorAccessServerImpl.this.loaded.put(sectorX, sectorZ, sector);
			}

			sector.addWatchingChunk(chunkX, chunkZ);
		}
		catch (InterruptedException | ExecutionException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onChunkUnloaded(final int chunkX, final int chunkZ)
	{
		this.getLoadedSectorForChunk(chunkX, chunkZ).ifPresent(sector -> {
			sector.removeWatchingChunk(chunkX, chunkZ);

			int sectorX = sector.getData().getSectorX();
			int sectorZ = sector.getData().getSectorY();

			if (!sector.hasWatchers() && !sector.getData().isDirty())
			{
				PrepSectorAccessServerImpl.this.loaded.remove(sectorX, sectorZ);
			}
		});
	}

	@Override
	public Collection<IPrepSector> getLoadedSectors()
	{
		return this.loaded.getValues();
	}

	@Override
	public ResourceLocation getName()
	{
		return this.registry.getUniqueId();
	}

	/**
	 * Saves all dirty sectors to the disk synchronously and unloads
	 * those of which have no players watching them.
	 */
	@Override
	public void flush()
	{
		if (this.world.isRemote)
		{
			return;
		}

		List<IPrepSector> dirty = new ArrayList<>();

		synchronized (this.loaded)
		{
			for (IPrepSector sector : this.loaded.getValues())
			{
				if (sector.getData().isDirty())
				{
					dirty.add(sector);
				}
			}
		}

		for (IPrepSector sector : dirty)
		{
			try
			{
				this.writeSectorDataToDisk(sector.getData());
			}
			catch (IOException e)
			{
				OrbisAPI.LOGGER.warn("Failed to flush sector", e);
			}

			sector.getData().markClean();
		}

		synchronized (this.loaded)
		{
			List<IPrepSector> removal = new ArrayList<>();

			for (IPrepSector sector : this.loaded.getValues())
			{
				if (!sector.hasWatchers())
				{
					removal.add(sector);
				}
			}

			for (IPrepSector sector : removal)
			{
				this.loaded.remove(sector.getData().getSectorX(), sector.getData().getSectorY());
			}
		}
	}

	/**
	 * Returns the location of a sector on disk by it's coordinates.
	 *
	 * @param sectorX The sector's x-coordinate
	 * @param sectorZ The sector's z-coordinate
	 * @return A {@link File} pointing to the sector on disk
	 */
	private String getSectorFileName(final int sectorX, final int sectorZ)
	{
		return "sector_" + sectorX + "_" + sectorZ + ".nbt.gz";
	}

	private IPrepSectorData readSectorDataFromDisk(int sectorX, int sectorY) throws IOException
	{
		byte[] bytes = this.dataManager.readBytes(this, this.getSectorFileName(sectorX, sectorY));

		if (bytes == null)
		{
			return null;
		}

		try (InputStream stream = new ByteArrayInputStream(bytes))
		{
			final IPrepSectorData sector = this.readSectorDataFromStream(stream);

			if (sector.getSectorX() != sectorX || sector.getSectorY() != sectorY)
			{
				throw new IOException("Sector has wrong coordinates on disk");
			}

			return sector;
		}
	}

	/**
	 * Reads an {@link IPrepSector} from an {@link InputStream}.
	 *
	 * @param stream The {@link InputStream} containing the sector's data
	 * @return The {@link IPrepSector} read from the stream
	 *
	 * @throws IOException If an I/O exception occurs while reading
	 */
	private IPrepSectorData readSectorDataFromStream(final InputStream stream) throws IOException
	{
		final NBTTagCompound tag = CompressedStreamTools.readCompressed(stream);

		return this.registry.createDataAndRead(this.world, tag);
	}

	private void writeSectorDataToDisk(IPrepSectorData sectorData) throws IOException
	{
		byte[] bytes;

		try (ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			this.writeSectorDataToStream(sectorData, out);

			bytes = out.toByteArray();
		}

		this.dataManager.writeBytes(this, this.getSectorFileName(sectorData.getSectorX(), sectorData.getSectorY()), bytes);
	}

	/**
	 * Writes an {@link IPrepSector} to an {@link OutputStream}.
	 *
	 * @param sector The sector to write
	 * @param out The {@link OutputStream} to write to
	 *
	 * @throws IOException If an I/O exception occurs while writing
	 */
	private void writeSectorDataToStream(final IPrepSectorData sector, final OutputStream out) throws IOException
	{
		final NBTTagCompound tag = new NBTTagCompound();
		sector.write(tag);

		CompressedStreamTools.writeCompressed(tag, out);
	}
}
