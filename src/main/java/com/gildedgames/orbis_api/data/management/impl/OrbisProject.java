package com.gildedgames.orbis_api.data.management.impl;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.data.management.*;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OrbisProject implements IProject
{
	private final List<IProjectListener> listeners = Lists.newArrayList();

	private final Map<IDataIdentifier, String> idToResourceLocation = Maps.newHashMap();

	private final Map<IDataIdentifier, File> idToFile = Maps.newHashMap();

	private IProjectCache cache;

	private URI jarLocation;

	private File locationFile;

	private Object mod;

	private String archiveBaseName;

	private boolean isModProject;

	private ProjectInformation info;

	public OrbisProject()
	{
		this.cache = new OrbisProjectCache(this);
	}

	/**
	 * Should be used to load back an existing project.
	 * @param location
	 */
	private OrbisProject(final File location)
	{
		this();

		this.jarLocation = location.toURI();
		this.locationFile = location;
	}

	/**
	 * Should be used to createTE a new project rather than an existing one.
	 * @param location The jarLocation of the project.
	 * @param info The info for the project.
	 */
	public OrbisProject(final File location, ProjectInformation info)
	{
		this();

		if (!location.exists() && !location.mkdirs())
		{
			throw new RuntimeException("Location for OrbisProject cannot be created!");
		}

		if (!location.isDirectory())
		{
			throw new IllegalArgumentException("Location file passed into OrbisProject is not a directory!");
		}

		this.jarLocation = location.toURI();
		this.locationFile = location;

		this.info = info;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("info", this.info);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.info = funnel.get("info");
	}

	@Override
	public String getResourceLocationForId(IDataIdentifier id)
	{
		return this.idToResourceLocation.get(id);
	}

	@Override
	public File getFileForId(IDataIdentifier id)
	{
		return this.idToFile.get(id);
	}

	@Override
	public boolean isModProject()
	{
		return this.isModProject;
	}

	@Override
	public void setIsModProject(boolean flag)
	{
		this.isModProject = flag;
	}

	@Nullable
	@Override
	public File getLocationAsFile()
	{
		return this.locationFile;
	}

	@Override
	public void setLocationAsFile(final File location)
	{
		this.locationFile = location;
	}

	@Override
	public URI getJarLocation()
	{
		return this.jarLocation;
	}

	@Override
	public void setJarLocation(final URI uri)
	{
		this.jarLocation = uri;
	}

	@Override
	public void addListener(final IProjectListener listener)
	{
		this.listeners.add(listener);
	}

	@Override
	public boolean removeListener(final IProjectListener listener)
	{
		return this.listeners.remove(listener);
	}

	@Override
	public ProjectInformation getInfo()
	{
		return this.info;
	}

	@Override
	public void setInfo(ProjectInformation info)
	{
		this.info = info;
	}

	@Override
	public IProjectCache getCache()
	{
		return this.cache;
	}

	@Override
	public void setCache(final IProjectCache cache)
	{
		cache.setProject(this);

		this.cache = cache;
	}

	private void writeMetadata(IData data, File file)
	{
		String extension = FilenameUtils.getExtension(file.getName());
		Optional<IMetadataLoader<OrbisProject>> loader = OrbisAPI.services().getProjectManager().getMetadataLoaderForExtension(extension);

		if (loader.isPresent())
		{
			File metaFile = new File(FilenameUtils.removeExtension(file.getPath()) + ".metadata");

			try (FileOutputStream out = new FileOutputStream(metaFile))
			{
				loader.get().saveMetadata(this, data, out);
			}
			catch (final IOException e)
			{
				OrbisAPI.LOGGER.error("Failed to save data metadata to disk", e);
			}
		}
	}

	@Override
	public void writeData(final IData data, final File file)
	{
		String extension = FilenameUtils.getExtension(file.getPath());

		this.writeMetadata(data, file);

		Optional<IDataLoader<OrbisProject>> dataLoader = OrbisAPI.services().getProjectManager().getDataLoaderForExtension(extension);

		if (dataLoader.isPresent())
		{
			try (FileOutputStream stream = new FileOutputStream(file))
			{
				dataLoader.get().saveData(this, data, file, stream);
			}
			catch (IOException e)
			{
				OrbisAPI.LOGGER.error("Failed to write data to project directory", data, e);
			}
		}
	}

	@Override
	public void loadData(IData data, File file, String location)
	{
		IDataIdentifier before = data.getMetadata().getIdentifier();

		/* Loads data from file then sets it to the cache **/
		this.cache.setData(data, location);

		/*
		 * This will determine if a new identifier will be created
		 * when the data is null.
		 */
		boolean shouldSaveAfter = !data.getMetadata().getIdentifier().equals(before);

		if (this.isModProject && shouldSaveAfter)
		{
			OrbisAPI.LOGGER.error("WARNING: A mod data file (" + data.getMetadata()
					+ ") has a different identifier assigned after loading (Old identifier: " + before
					+ "). This usually means the original identifier has a null UUID data id or the identifier itself is null. Please reimport this data into your project OUTSIDE of the development workspace and let it assign itself the correct metadata.");
		}
		else if (this.locationFile != null && shouldSaveAfter)
		{
			/*
			 * Save the metadata to disk to ensure it doesn't keep creating
			 * new identifiers each time the project is loaded.
			 */
			this.writeMetadata(data, file);
		}
	}

	@Override
	public boolean findAndLoadData(IDataIdentifier id)
	{
		if (id == null)
		{
			throw new IllegalArgumentException("Identifier should not be null when trying to find and load data!");
		}

		final boolean[] found = { false };

		this.walkDataLoading((input, metadataInput, file, location) -> {
			// If already loaded, continue to next file
			if (!this.cache.getDataId(location).isPresent())
			{
				return;
			}

			String extension = FilenameUtils.getExtension(location);

			Optional<IMetadataLoader<OrbisProject>> metadataLoader = OrbisAPI.services().getProjectManager().getMetadataLoaderForExtension(extension);

			if (!metadataLoader.isPresent())
			{
				return;
			}

			InputStream metaInput = metadataInput.get();

			if (metaInput == null)
			{
				return;
			}

			IDataMetadata metadata = metadataLoader.get().loadMetadata(this, metaInput);

			if (metadata != null)
			{
				if (id.equals(metadata.getIdentifier()))
				{
					Optional<IDataLoader<OrbisProject>> dataLoader = OrbisAPI.services().getProjectManager().getDataLoaderForExtension(extension);

					if (dataLoader.isPresent())
					{
						IData data = dataLoader.get().loadData(this, file, input);
						data.setMetadata(metadata);

						found[0] = true;

						this.loadData(data, file, location);

						if (file != null)
						{
							this.idToFile.put(data.getMetadata().getIdentifier(), file);
						}
						else
						{
							this.idToResourceLocation.put(data.getMetadata().getIdentifier(), location);
						}
					}
				}
			}
		});

		return found[0];
	}

	@Override
	public void setModAndArchiveLoadingFrom(Object mod, String archiveBaseName)
	{
		this.mod = mod;
		this.archiveBaseName = archiveBaseName;
	}

	/**
	 * Walks through the project finding data files, then calls the provided
	 * consumer to do what you want with that data file.
	 */
	private void walkDataLoading(ProjectDataWalker dataWalker)
	{
		String locationRoot = this.locationFile != null ? this.locationFile.getPath() : this.jarLocation.getPath();

		URL classpathURL = this.mod.getClass().getClassLoader().getResource("");

		// If this isn't null, we're in a writable classpath
		File classpathDir = null;

		if (classpathURL != null)
		{
			File classpathFile = new File(classpathURL.getPath(), locationRoot);

			if (classpathFile.isDirectory())
			{
				classpathDir = classpathFile;
			}
		}

		List<String> fileList;

		try (InputStream stream = OrbisProject.class.getResourceAsStream(locationRoot + "project_index.txt"))
		{
			if (stream == null)
			{
				throw new IOException("Project file index does not exist");
			}

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
			{
				fileList = reader.lines()
						.filter(line -> !line.startsWith("#"))
						.collect(Collectors.toList());
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to read project file index", e);
		}

		for (String filePath : fileList)
		{
			String fullPath = locationRoot + filePath;

			try (InputStream stream = OrbisProject.class.getResourceAsStream(fullPath))
			{
				if (stream == null)
				{
					OrbisAPI.LOGGER.warn("File in project index does not exist: " + fullPath);

					continue;
				}

				String name = fullPath.replace(locationRoot, "");

				File file = classpathDir != null ? new File(classpathDir, fullPath) : null;

				dataWalker.walk(stream, () -> OrbisProject.class.getResourceAsStream(FilenameUtils.removeExtension(fullPath) + ".metadata"), file, name);
			}
			catch (IOException e)
			{
				throw new RuntimeException("Failed to iterate project files", e);
			}
		}
	}

	@Override
	public void loadAndCacheData()
	{
		this.walkDataLoading((input, metadataInput, file, location) -> {
			String extension = FilenameUtils.getExtension(location);

			Optional<IDataLoader<OrbisProject>> dataLoader = OrbisAPI.services().getProjectManager().getDataLoaderForExtension(extension);
			Optional<IMetadataLoader<OrbisProject>> metadataLoader = OrbisAPI.services().getProjectManager().getMetadataLoaderForExtension(extension);

			if (!dataLoader.isPresent() || !metadataLoader.isPresent())
			{
				return;
			}

			final IData data = dataLoader.get().loadData(this, file, input);

			if (data != null && !this.cache.getDataId(location).isPresent())
			{
				InputStream metaInput = metadataInput.get();

				IDataMetadata metadata = null;

				if (metaInput == null)
				{
					if (!this.isModProject)
					{
						metadata = new DataMetadata();

						metadata.setName(file.getName().replace("." + extension, ""));
						metadata.setIdentifier(this.cache.createNextIdentifier());

						File metaFile = new File(file.getPath().replace("." + extension, ".metadata"));

						try (FileOutputStream out = new FileOutputStream(metaFile))
						{
							metadataLoader.get().saveMetadata(this, data, out);
						}
						catch (IOException e)
						{
							OrbisAPI.LOGGER.error("Failed to save metadata for data file", e);
						}
					}
					else
					{
						OrbisAPI.LOGGER.error("WARNING: A data file in your mod project (" + this.getInfo().getIdentifier()
								+ ") doesn't have a metadata file, meaning it will not work. This can be auto-generated outside of a dev workspace if you simply load up the project.");
					}
				}
				else
				{
					metadata = metadataLoader.get().loadMetadata(this, metaInput);
				}

				if (metadata == null)
				{
					OrbisAPI.LOGGER.error("WARNING: A data file could not load because there was no associate metadata file with it.");

					return;
				}

				data.setMetadata(metadata);

				this.loadData(data, file, location);

				if (file != null)
				{
					this.idToFile.put(data.getMetadata().getIdentifier(), file);
				}
				else
				{
					this.idToResourceLocation.put(data.getMetadata().getIdentifier(), location);
				}
			}
			else
			{
				OrbisAPI.LOGGER.error("Failed to load back a data file from project.", location);
			}
		});
	}

	@Override
	public boolean areModDependenciesMet()
	{
		return true;
	}

	private interface ProjectDataWalker
	{
		void walk(InputStream stream, Supplier<InputStream> metaProvider, File file, String location) throws IOException;
	}
}
