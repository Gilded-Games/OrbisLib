package com.gildedgames.orbis_api.data.management.impl;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.blueprint.BlueprintStackerData;
import com.gildedgames.orbis_api.data.framework.FrameworkData;
import com.gildedgames.orbis_api.data.json.JsonData;
import com.gildedgames.orbis_api.data.management.*;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class OrbisProject implements IProject
{
	private final List<IProjectListener> listeners = Lists.newArrayList();

	private final List<String> acceptedFileExtensions = Lists.newArrayList();

	private final Map<String, IManualDataLoader<OrbisProject>> extensionsToLoadManually = Maps.newHashMap();

	private final Map<IDataIdentifier, String> idToResourceLocation = Maps.newHashMap();

	private final Map<IDataIdentifier, File> idToFile = Maps.newHashMap();

	/**
	 * Used for data types like JSON which does not have the same structure as our IData NBT files.
	 */
	private Map<String, IDataMetadata> manualMetaCache = Maps.newHashMap();

	private IProjectCache cache;

	private URI jarLocation;

	private File locationFile;

	private IProjectIdentifier identifier;

	private IProjectMetadata metadata;

	private Object mod;

	private String archiveBaseName;

	private boolean isModProject;

	private OrbisProject()
	{
		this.cache = new OrbisProjectCache(this);

		this.acceptedFileExtensions.add(BlueprintData.EXTENSION);
		this.acceptedFileExtensions.add(FrameworkData.EXTENSION);
		this.acceptedFileExtensions.add(BlueprintStackerData.EXTENSION);

		this.extensionsToLoadManually.put(JsonData.EXTENSION, new IManualDataLoader<OrbisProject>()
		{
			@Override
			public void saveMetadata(OrbisProject project, IData data, File file, String location)
			{
				project.setManualMetadata(location, data.getMetadata());
			}

			@Override
			public IData load(OrbisProject project, File file, String location)
			{
				JsonData data = new JsonData();
				IDataMetadata metadata = project.getManualMetadta(location);

				if (metadata != null)
				{
					data.setMetadata(metadata);
				}

				return data;
			}
		});
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
	 * @param identifier The unique identifier for the project.
	 */
	public OrbisProject(final File location, final IProjectIdentifier identifier)
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

		this.identifier = identifier;

		this.metadata = new ProjectMetadata();
	}

	public IDataMetadata getManualMetadta(String location)
	{
		return this.manualMetaCache.get(location);
	}

	public void setManualMetadata(String location, IDataMetadata meta)
	{
		this.manualMetaCache.put(location, meta);
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("identifier", this.identifier);
		funnel.set("metadata", this.metadata);
		funnel.setMap("manualMetaCache", this.manualMetaCache, NBTFunnel.STRING_SETTER, NBTFunnel.setter());
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.identifier = funnel.get("identifier");
		this.metadata = funnel.get("metadata");
		this.manualMetaCache = funnel.getMap("manualMetaCache", NBTFunnel.STRING_GETTER, NBTFunnel.getter());
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
	public IProjectIdentifier getProjectIdentifier()
	{
		return this.identifier;
	}

	@Override
	public void setProjectIdentifier(IProjectIdentifier projectIdentifier)
	{
		this.identifier = projectIdentifier;
	}

	@Override
	public IProjectMetadata getMetadata()
	{
		return this.metadata;
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

	@Override
	public void writeData(final IData data, final File file)
	{
		if (this.extensionsToLoadManually.containsKey(data.getFileExtension()))
		{
			try
			{
				//TODO SHOULD NOT ALWAYS BE FILE PATH, CAN BE RESOURCE LOCATION
				String location = this.getLocationFromFile(file.getCanonicalPath());

				this.extensionsToLoadManually.get(data.getFileExtension()).saveMetadata(this, data, file, location);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			return;
		}

		try (FileOutputStream out = new FileOutputStream(file))
		{
			final NBTTagCompound tag = new NBTTagCompound();
			final NBTFunnel funnel = new NBTFunnel(tag);

			funnel.set("data", data);

			CompressedStreamTools.writeCompressed(tag, out);
		}
		catch (final IOException e)
		{
			OrbisAPI.LOGGER.error("Failed to save project data to disk", e);
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
			 * Save the data to disk to ensure it doesn't keep creating
			 * new identifiers each time the project is loaded.
			 */
			this.writeData(data, file);
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

		this.walkDataLoading((input, file, location, resourceLocation, manual) -> {
			// If already loaded, continue to next file
			if (!this.cache.getDataId(location).isPresent())
			{
				return;
			}

			final IData data;
			NBTFunnel funnel = null;

			if (!manual)
			{
				final NBTTagCompound tag = CompressedStreamTools.readCompressed(input);

				funnel = new NBTFunnel(tag);

				data = funnel.get("data");
			}
			else
			{
				IManualDataLoader<OrbisProject> chosenLoader = this.extensionsToLoadManually.get(FilenameUtils.getExtension(location));

				data = chosenLoader.load(this, file, location);
			}

			if (data != null)
			{
				NBTTagCompound tag = null;

				if (funnel != null)
				{
					// Nested data tags because of the way NBTHelper writes NBT files
					tag = funnel.getTag().getCompoundTag("data").getCompoundTag("data");

					data.readMetadataOnly(tag);
				}

				if (id.equals(data.getMetadata().getIdentifier()))
				{
					found[0] = true;

					if (tag != null)
					{
						data.read(tag);
					}

					this.loadData(data, file, location);

					if (file != null)
					{
						this.idToFile.put(data.getMetadata().getIdentifier(), file);
					}
					else
					{
						this.idToResourceLocation.put(data.getMetadata().getIdentifier(), resourceLocation);
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
		try
		{
			/*
			 * Uses the file jarLocation if accessible (used on Orbis client)
			 * Otherwise, uses URI and accesses from MC server resource
			 * so it works when stored in a mod jar.
			 */
			final String rawPath = this.mod.getClass().getResource("").toURI().toString();
			URI resources = URI.create(rawPath);

			final Path myPath;
			FileSystem fileSystem = null;

			final boolean usesJar;

			String modPackage = "/" + this.mod.getClass().getName().replace(this.mod.getClass().getSimpleName(), "").replace(".", "/");

			/* INSIDE JAR **/
			if (resources.getScheme().equals("jar"))
			{
				resources = URI.create(rawPath.replace(modPackage, "/"));

				fileSystem = FileSystems.newFileSystem(resources, Collections.emptyMap());
				myPath = fileSystem.getPath("/");

				usesJar = true;
			}
			else if (this.jarLocation != null) /* DEVELOPMENT WORKSPACE, JAR **/
			{
				String subRaw = rawPath.substring(rawPath.lastIndexOf(this.archiveBaseName));
				String pack = subRaw.substring(this.archiveBaseName.length(), subRaw.indexOf("/"));

				String orig = "/" + this.archiveBaseName + pack + modPackage;
				String assets = "/" + this.archiveBaseName + "_main/assets/";

				resources = URI.create(rawPath.replace(orig, assets));

				myPath = Paths.get(resources);

				usesJar = false;
			}
			else
			{
				myPath = Paths.get(this.locationFile.getPath());

				usesJar = false;
			}

			try (Stream<Path> paths = Files.walk(myPath))
			{
				paths.forEach(p ->
				{
					final URI uri = p.toUri();
					final String path = uri.toString().contains("!") ? uri.toString().split("!")[1] : uri.toString();

					String locationRoot = this.locationFile != null ? this.locationFile.getPath() : this.jarLocation.getPath();
					String relativePath = path.replace(path.substring(0, path.indexOf("/assets")), "");

					/* Prevents the path walking from including the project's jarLocation **/
					if (relativePath.equals(locationRoot) || !relativePath.startsWith(locationRoot))
					{
						return;
					}

					final String extension = FilenameUtils.getExtension(path);

					/* Prevents the path walking from including the project data itself (hidden file) **/
					if (extension.equals("project"))
					{
						return;
					}

					/* Make sure the file has an extension type accepted by this project **/
					if (this.acceptedFileExtensions.contains(extension))
					{
						try
						{
							File file = null;

							if (!usesJar)
							{
								file = new File(uri);
							}

							final String resourceLocation = !usesJar ? file.getCanonicalPath() : path;

							try (InputStream in = usesJar ? MinecraftServer.class.getResourceAsStream(resourceLocation) : new FileInputStream(file))
							{
								String projectsLoc = resourceLocation.substring(
										resourceLocation.lastIndexOf("projects") + 9);

								final String location = projectsLoc
										.substring(projectsLoc.indexOf(this.identifier.getProjectId()) + this.identifier.getProjectId().length() + 1);

								dataWalker.walk(in, file, location, resourceLocation, false);
							}
							catch (final IOException e)
							{
								OrbisAPI.LOGGER.error(e);
							}
						}
						catch (final IOException e)
						{
							OrbisAPI.LOGGER.error(e);
						}
					}

					if (this.extensionsToLoadManually.containsKey(extension))
					{
						try
						{
							File file = null;

							if (!usesJar)
							{
								file = new File(uri);
							}

							final String resourceLocation = !usesJar ? file.getCanonicalPath() : path;

							try (InputStream in = usesJar ? MinecraftServer.class.getResourceAsStream(resourceLocation) : new FileInputStream(file))
							{
								String location = this.getLocationFromFile(resourceLocation);

								dataWalker.walk(in, file, location, resourceLocation, true);
							}
							catch (final IOException e)
							{
								OrbisAPI.LOGGER.error(e);
							}
						}
						catch (final IOException e)
						{
							OrbisAPI.LOGGER.error(e);
						}
					}
				});
			}
			catch (final IOException e)
			{
				OrbisAPI.LOGGER.error(e);
			}

			if (fileSystem != null)
			{
				fileSystem.close();
			}
		}
		catch (final IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}
	}

	private String getLocationFromFile(String filePath)
	{
		String projectsLoc = filePath.substring(
				filePath.lastIndexOf("projects") + 9);

		return projectsLoc
				.substring(projectsLoc.indexOf(this.identifier.getProjectId()) + this.identifier.getProjectId().length() + 1);
	}

	@Override
	public void loadAndCacheData()
	{
		this.walkDataLoading((input, file, location, resourceLocation, manual) -> {
			final IData data;

			if (!manual)
			{
				final NBTTagCompound tag = CompressedStreamTools.readCompressed(input);

				final NBTFunnel funnel = new NBTFunnel(tag);

				data = funnel.get("data");
			}
			else
			{
				IManualDataLoader<OrbisProject> chosenLoader = this.extensionsToLoadManually.get(FilenameUtils.getExtension(location));

				data = chosenLoader.load(this, file, location);
			}

			if (data != null && !this.cache.getDataId(location).isPresent())
			{
				this.loadData(data, file, location);

				if (file != null)
				{
					this.idToFile.put(data.getMetadata().getIdentifier(), file);
				}
				else
				{
					this.idToResourceLocation.put(data.getMetadata().getIdentifier(), resourceLocation);
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
		void walk(InputStream in, File file, String location, String resourceLocation, boolean manualLoading) throws IOException;
	}
}
