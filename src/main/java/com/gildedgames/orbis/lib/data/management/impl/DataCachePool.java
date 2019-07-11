package com.gildedgames.orbis.lib.data.management.impl;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.data.management.IDataCache;
import com.gildedgames.orbis.lib.data.management.IDataCachePool;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class DataCachePool implements IDataCachePool
{
	public static final String EXTENSION = "cache";

	private final File location;

	private Map<String, IDataCache> idToCache = Maps.newHashMap();

	public DataCachePool(final File location)
	{
		if (!location.exists() && !location.mkdirs())
		{
			throw new RuntimeException("Directory for DataCachePool cannot be created!");
		}

		if (!location.isDirectory())
		{
			throw new IllegalArgumentException("File passed into DataCachePool is not a directory!");
		}

		this.location = location;
	}

	private void readCacheFromDisk(final File file)
	{
		try (FileInputStream in = new FileInputStream(file))
		{
			final CompoundNBT tag = CompressedStreamTools.readCompressed(in);

			final NBTFunnel funnel = new NBTFunnel(tag);

			final IDataCache cache = funnel.get(EXTENSION);

			this.registerCache(cache);
		}
		catch (final IOException e)
		{
			OrbisLib.LOGGER.catching(e);
		}
	}

	private void saveCacheToDisk(final IDataCache cache)
	{
		final File cacheFile = new File(this.location, cache.getCacheId() + "." + EXTENSION);

		try (FileOutputStream out = new FileOutputStream(cacheFile))
		{
			final CompoundNBT tag = new CompoundNBT();
			final NBTFunnel funnel = new NBTFunnel(tag);

			funnel.set(EXTENSION, cache);

			CompressedStreamTools.writeCompressed(tag, out);
		}
		catch (final IOException e)
		{
			OrbisLib.LOGGER.error("Failed to save IDataCache to disk", e);
		}
	}

	@Override
	public void flushToDisk()
	{
		this.idToCache.values().forEach(this::saveCacheToDisk);
	}

	@Override
	public void readFromDisk()
	{
		try (Stream<Path> paths = Files.walk(Paths.get(this.location.getPath())))
		{
			paths.forEach(p ->
			{
				final File file = p.toFile();

				final String extension = FilenameUtils.getExtension(file.getName());

				/** Prevents the path walking from including non-cache files **/
				if (!extension.equals(EXTENSION))
				{
					return;
				}

				this.readCacheFromDisk(file);
			});
		}
		catch (final IOException e)
		{
			OrbisLib.LOGGER.error(e);
		}
	}

	@Override
	public void registerCache(final IDataCache cache)
	{
		if (cache == null)
		{
			throw new IllegalArgumentException("The cache you're trying to register to this DataCachePool is null.");
		}

		this.idToCache.put(cache.getCacheId(), cache);
	}

	@Nullable
	@Override
	public <T extends IDataCache> Optional<T> findCache(final String cacheID)
	{
		final IDataCache cache = this.idToCache.get(cacheID);

		if (cache == null)
		{
			OrbisLib.LOGGER.warn("The cache you attempted to find (" + cacheID + ") could not be found. Something might be wrong.");

			return Optional.empty();
		}

		return Optional.of((T) cache);
	}

	@Override
	public CompoundNBT writeCacheData()
	{
		final CompoundNBT tag = new CompoundNBT();
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.setStringMap("idToCache", this.idToCache);

		return tag;
	}

	@Override
	public void readCacheData(final CompoundNBT tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.idToCache = funnel.getStringMap("idToCache");
	}
}
