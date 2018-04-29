package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.preparation.*;
import com.gildedgames.orbis_api.preparation.impl.PrepSectorAccessFlatFile;
import com.google.common.collect.Sets;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Set;

public class PrepManager implements IPrepManager
{
	private final File folder;

	private final World world;

	private final IPrepRegistryEntry registry;

	private Set<Long> sectorsPreparing = Sets.newConcurrentHashSet();

	private IPrepSectorAccess access;

	private IPrepChunkManager chunkManager;

	public PrepManager()
	{
		this.world = null;
		this.folder = null;
		this.registry = null;
	}

	public PrepManager(World world, File folder, IPrepRegistryEntry registry)
	{
		this.world = world;
		this.folder = folder;
		this.registry = registry;

		if (this.folder.exists())
		{
			if (!this.folder.isDirectory())
			{
				throw new RuntimeException("Storage directory is a file");
			}
		}
		else if (!this.folder.mkdirs())
		{
			throw new RuntimeException("Failed to create storage directory");
		}

		this.access = new PrepSectorAccessFlatFile(this.world, this.registry, this);

		this.chunkManager = new PrepChunkManager(this.world, this.registry);
	}

	@Override
	public IPrepRegistryEntry getRegistryEntry()
	{
		return this.registry;
	}

	@Override
	public IPrepChunkManager getChunkManager()
	{
		return this.chunkManager;
	}

	@Override
	public boolean isSectorPreparing(int sectorX, int sectorY)
	{
		return this.sectorsPreparing.contains(ChunkPos.asLong(sectorX, sectorY));
	}

	@Override
	public void markSectorPreparing(int sectorX, int sectorY)
	{
		this.sectorsPreparing.add(ChunkPos.asLong(sectorX, sectorY));
	}

	@Override
	public void unmarkSectorPreparing(int sectorX, int sectorY)
	{
		this.sectorsPreparing.remove(ChunkPos.asLong(sectorX, sectorY));
	}

	@Override
	public boolean isSectorWrittenToDisk(int sectorX, int sectorY)
	{
		return this.getSectorFile(sectorX, sectorY).exists();
	}

	@Override
	public void writeSectorDataToDisk(IPrepSectorData sectorData)
	{
		synchronized (this)
		{
			final File file = this.getSectorFile(sectorData.getSectorX(), sectorData.getSectorY());

			try (FileOutputStream out = new FileOutputStream(file))
			{
				this.writeSectorDataToStream(sectorData, out);
			}
			catch (final IOException e)
			{
				OrbisAPI.LOGGER.error("Failed to save sector to disk", e);
			}
		}
	}

	@Override
	public IPrepSectorData readSectorDataFromDisk(int sectorX, int sectorY)
	{
		synchronized (this)
		{
			final File file = this.getSectorFile(sectorX, sectorY);

			if (!file.exists())
			{
				return null;
			}

			try (FileInputStream stream = new FileInputStream(file))
			{
				final IPrepSectorData sector = this.readSectorDataFromStream(stream);

				if (sector.getSectorX() != sectorX || sector.getSectorY() != sectorY)
				{
					throw new IOException("Sector has wrong coordinates on disk");
				}

				return sector;
			}
			catch (final IOException e)
			{
				OrbisAPI.LOGGER.error("Failed to read sector from disk", e);
			}

			return null;
		}
	}

	@Override
	public IPrepSectorAccess access()
	{
		return this.access;
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

	/**
	 * Returns the location of a sector on disk by it's coordinates.
	 *
	 * @param sectorX The sector's x-coordinate
	 * @param sectorZ The sector's z-coordinate
	 * @return A {@link File} pointing to the sector on disk
	 */
	private File getSectorFile(final int sectorX, final int sectorZ)
	{
		return new File(this.folder, "sector." + sectorX + "." + sectorZ + ".nbt");
	}

	public static class Storage implements Capability.IStorage<IPrepManager>
	{
		@Nullable
		@Override
		public NBTBase writeNBT(final Capability<IPrepManager> capability, final IPrepManager instance, final EnumFacing side)
		{
			return null;
		}

		@Override
		public void readNBT(final Capability<IPrepManager> capability, final IPrepManager instance, final EnumFacing side, final NBTBase nbt)
		{

		}
	}
}
