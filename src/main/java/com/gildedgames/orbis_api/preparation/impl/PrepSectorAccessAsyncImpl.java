package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.preparation.*;
import com.gildedgames.orbis_api.util.ChunkMap;
import com.gildedgames.orbis_api.world.data.IWorldDataManager;
import com.google.common.util.concurrent.*;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Executors;

/**
 * Implementation of {@link IPrepSectorAccessAsync} that uses a flat-file database.
 */
public class PrepSectorAccessAsyncImpl implements IPrepSectorAccessAsync
{
	private final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

	// THREAD-SAFE
	private final ChunkMap<ListenableFuture<IPrepSector>> futures = new ChunkMap<>();

	private final World world;

	private final IPrepRegistryEntry registry;

	private final IPrepManager prepManager;

	// NOT THREAD-SAFE, ONLY MODIFY ON GAME THREAD
	private final ChunkMap<IPrepSector> loaded = new ChunkMap<>();

	// NOT THREAD-SAFE, ONLY MODIFY ON GAME THREAD
	private final Queue<IPrepSector> dirty = new ArrayDeque<>();

	private final IWorldDataManager dataManager;

	public PrepSectorAccessAsyncImpl(World world, IPrepRegistryEntry registry, IPrepManager prepManager, IWorldDataManager dataManager)
	{
		this.world = world;
		this.prepManager = prepManager;
		this.registry = registry;

		this.dataManager = dataManager;
		this.dataManager.register(this);
	}

	@Override
	public Optional<IPrepSector> getLoadedSector(final int chunkX, final int chunkZ)
	{
		final int sectorX = Math.floorDiv(chunkX, this.registry.getSectorChunkArea());
		final int sectorZ = Math.floorDiv(chunkZ, this.registry.getSectorChunkArea());

		return Optional.ofNullable(this.loaded.get(sectorX, sectorZ));
	}

	@Override
	public ListenableFuture<IPrepSector> provideSector(final int chunkX, final int chunkZ)
	{
		if (this.world.isRemote)
		{
			return Futures.immediateFuture(null);
		}

		final int sectorX = Math.floorDiv(chunkX, this.registry.getSectorChunkArea());
		final int sectorZ = Math.floorDiv(chunkZ, this.registry.getSectorChunkArea());

		// Check if the sector is already loaded
		if (this.loaded.containsKey(sectorX, sectorZ))
		{
			return Futures.immediateFuture(this.loaded.get(sectorX, sectorZ));
		}

		synchronized (this.futures)
		{
			if (this.futures.containsKey(sectorX, sectorZ))
			{
				return this.futures.get(sectorX, sectorZ);
			}
		}

		SettableFuture<IPrepSector> future = SettableFuture.create();

		synchronized (this.futures)
		{
			this.futures.put(sectorX, sectorZ, future);
		}

		this.service.execute(() -> {
			IPrepSectorData data = null;

			try
			{
				data = this.readSectorDataFromDisk(sectorX, sectorZ);
			}
			catch (IOException e)
			{
				future.setException(e);
			}

			if (data != null)
			{
				future.set(new PrepSector(data));

				return;
			}

			Futures.addCallback(this.prepManager.createSector(sectorX, sectorZ), new FutureCallback<IPrepSectorData>()
			{
				@Override
				public void onSuccess(@Nullable IPrepSectorData result)
				{
					PrepSector sector = new PrepSector(result);
					sector.markDirty();

					future.set(sector);
				}

				@Override
				public void onFailure(@Nonnull Throwable t)
				{
					future.setException(t);
				}
			}, this.service);
		});

		Futures.addCallback(future, new FutureCallback<IPrepSector>()
		{
			@Override
			public void onSuccess(IPrepSector result)
			{
				FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
					// Store the sector in memory
					PrepSectorAccessAsyncImpl.this.loaded.put(sectorX, sectorZ, result);

					// Queue the sector for saving if we just generated it
					if (result.isDirty())
					{
						PrepSectorAccessAsyncImpl.this.dirty.add(result);
					}

					// Begin watching the sector
					result.addWatchingChunk(chunkX, chunkZ);

					synchronized (PrepSectorAccessAsyncImpl.this.futures)
					{
						PrepSectorAccessAsyncImpl.this.futures.remove(sectorX, sectorZ);
					}
				});
			}

			@Override
			public void onFailure(@Nonnull Throwable t)
			{
				t.printStackTrace();
			}
		}, this.service);

		return future;
	}

	@Override
	public void onChunkLoaded(final int chunkX, final int chunkZ)
	{
		if (this.world.isRemote)
		{
			return;
		}

		final int sectorX = Math.floorDiv(chunkX, this.registry.getSectorChunkArea());
		final int sectorZ = Math.floorDiv(chunkZ, this.registry.getSectorChunkArea());

		// Check if the sector is already loaded
		if (this.loaded.containsKey(sectorX, sectorZ))
		{
			return;
		}

		this.provideSector(sectorX, sectorZ);
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
		while (!this.dirty.isEmpty())
		{
			final IPrepSector sector = this.dirty.remove();

			try
			{
				this.writeSectorDataToDisk(sector.getData());
			}
			catch (IOException e)
			{
				OrbisAPI.LOGGER.warn("Failed to flush sector", e);
			}

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

	public static class Storage implements Capability.IStorage<IPrepSectorAccessAsync>
	{
		@Nullable
		@Override
		public NBTBase writeNBT(final Capability<IPrepSectorAccessAsync> capability, final IPrepSectorAccessAsync instance, final EnumFacing side)
		{
			return null;
		}

		@Override
		public void readNBT(final Capability<IPrepSectorAccessAsync> capability, final IPrepSectorAccessAsync instance, final EnumFacing side,
				final NBTBase nbt)
		{

		}
	}
}
