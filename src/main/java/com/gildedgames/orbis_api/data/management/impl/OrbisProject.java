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
import com.google.gson.Gson;
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

	private final Map<String, IDataLoader<OrbisProject>> acceptedFileExtensions = Maps.newHashMap();

	private final Map<IDataIdentifier, String> idToResourceLocation = Maps.newHashMap();

	private final Map<IDataIdentifier, File> idToFile = Maps.newHashMap();

	private final Map<String, IMetadataLoader<OrbisProject>> metadataLoaders = Maps.newHashMap();

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

		IDataLoader<OrbisProject> nbtDataLoader = new IDataLoader<OrbisProject>()
		{
			@Override
			public void saveData(OrbisProject project, IData data, File file, String location, OutputStream output)
			{
				try
				{
					final NBTTagCompound tag = new NBTTagCompound();
					final NBTFunnel funnel = new NBTFunnel(tag);

					funnel.set("data", data);

					CompressedStreamTools.writeCompressed(tag, output);
				}
				catch (final IOException e)
				{
					OrbisAPI.LOGGER.error("Failed to save project data to disk", e);
				}
			}

			@Override
			public IData loadData(OrbisProject project, File file, String location, InputStream input)
			{
				try
				{
					NBTTagCompound tag = CompressedStreamTools.readCompressed(input);
					NBTFunnel funnel = new NBTFunnel(tag);

					IData data = funnel.get("data");

					tag = funnel.getTag().getCompoundTag("data").getCompoundTag("data");

					data.read(tag);

					return data;
				}
				catch (IOException e)
				{
					OrbisAPI.LOGGER.error("Failed to load project data from disk", e);
				}

				return null;
			}
		};

		IDataLoader<OrbisProject> jsonDataLoader = new IDataLoader<OrbisProject>()
		{
			@Override
			public void saveData(OrbisProject project, IData data, File file, String location, OutputStream output)
			{

			}

			@Override
			public IData loadData(OrbisProject project, File file, String location, InputStream input)
			{
				return new JsonData();
			}
		};

		this.acceptedFileExtensions.put(BlueprintData.EXTENSION, nbtDataLoader);
		this.acceptedFileExtensions.put(FrameworkData.EXTENSION, nbtDataLoader);
		this.acceptedFileExtensions.put(BlueprintStackerData.EXTENSION, nbtDataLoader);
		this.acceptedFileExtensions.put(JsonData.EXTENSION, jsonDataLoader);

		IMetadataLoader<OrbisProject> jsonMetadataLoader = new IMetadataLoader<OrbisProject>()
		{
			@Override
			public void saveMetadata(OrbisProject project, IData data, File file, String location, OutputStream outputStream)
			{
				try (OutputStreamWriter writer = new OutputStreamWriter(outputStream))
				{
					Gson gson = new Gson();

					gson.toJson(writer, DataMetadata.class);
				}
				catch (IOException e)
				{
					OrbisAPI.LOGGER.error("Failed to save data metadata to disk", e);
				}
			}

			@Override
			public IDataMetadata loadMetadata(OrbisProject project, File file, String location, InputStream input)
			{
				try (InputStreamReader reader = new InputStreamReader(input))
				{
					Gson gson = new Gson();

					return gson.fromJson(reader, DataMetadata.class);
				}
				catch (IOException e)
				{
					OrbisAPI.LOGGER.error("Failed to load data metadata from disk", e);
				}

				return null;
			}
		};

		this.metadataLoaders.put(BlueprintData.EXTENSION, jsonMetadataLoader);
		this.metadataLoaders.put(FrameworkData.EXTENSION, jsonMetadataLoader);
		this.metadataLoaders.put(BlueprintStackerData.EXTENSION, jsonMetadataLoader);
		this.metadataLoaders.put(JsonData.EXTENSION, jsonMetadataLoader);
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

		this.info = funnel.get("identifier");
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

	@Override
	public void writeData(final IData data, final File file)
	{
		try
		{
			String extension = FilenameUtils.getExtension(file.getName());
			IMetadataLoader<OrbisProject> loader = this.metadataLoaders.get(extension);

			if (loader != null)
			{
				//TODO SHOULD NOT ALWAYS BE FILE PATH, CAN BE RESOURCE LOCATION
				String location = this.getLocationFromFile(file.getCanonicalPath());

				File metaFile = new File(file.getPath().replace("." + extension, ".metadata"));

				try (FileOutputStream out = new FileOutputStream(metaFile))
				{
					loader.saveMetadata(this, data, file, location, out);
				}
				catch (final IOException e)
				{
					OrbisAPI.LOGGER.error("Failed to save data metadata to disk", e);
				}
			}
		}
		catch (IOException e)
		{
			OrbisAPI.LOGGER.info(e);
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

		this.walkDataLoading((input, metadataInput, file, location, resourceLocation) -> {
			// If already loaded, continue to next file
			if (!this.cache.getDataId(location).isPresent())
			{
				return;
			}

			String extension = FilenameUtils.getExtension(file.getName());

			IMetadataLoader<OrbisProject> metadataLoader = this.metadataLoaders.get(extension);

			if (metadataLoader == null)
			{
				return;
			}

			IDataMetadata metadata = metadataLoader.loadMetadata(this, file, location, metadataInput);

			if (metadata != null)
			{
				if (id.equals(metadata.getIdentifier()))
				{
					IDataLoader<OrbisProject> dataLoader = this.acceptedFileExtensions.get(extension);

					if (dataLoader != null)
					{
						IData data = dataLoader.loadData(this, file, location, input);

						found[0] = true;

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
			if (resources.getScheme().equals("jar") && this.isModProject)
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

			String locationRoot = this.locationFile != null ? this.locationFile.getPath() : this.jarLocation.getPath();

			try (Stream<Path> paths = Files.walk(myPath))
			{
				paths.forEach(p ->
				{
					final URI uri = p.toUri();
					final String path = uri.toString().contains("!") ? uri.toString().split("!")[1] : uri.toString();

					String relativePath = path.contains("/assets") ? path.replace(path.substring(0, path.indexOf("/assets")), "") : path;

					/* Prevents the path walking from including the project's jarLocation **/
					if (relativePath.replace("/", "\\").equals(locationRoot.replace("/", "\\")) || !relativePath.replace("/", "\\")
							.contains(locationRoot.replace("/", "\\")))
					{
						return;
					}

					final String extension = FilenameUtils.getExtension(path);
					final String name = FilenameUtils.getName(path);

					/* Prevents the path walking from including the project data itself (hidden file) **/
					if (extension.equals("project"))
					{
						return;
					}

					/* Make sure the file has an extension type accepted by this project **/
					if (this.acceptedFileExtensions.keySet().contains(extension))
					{
						try
						{
							File file = null;

							if (!usesJar)
							{
								file = new File(uri);
							}

							final String resourceLocation = !usesJar ? file.getCanonicalPath() : path;

							File metadataFile = file != null ? new File(file.getPath().replace("." + extension, ".metadata")) : null;
							String resourceLocationMetadata = resourceLocation.replace("." + extension, ".metadata");

							try (InputStream in = usesJar ? MinecraftServer.class.getResourceAsStream(resourceLocation) : new FileInputStream(file))
							{
								try (InputStream metaIn = usesJar ?
										MinecraftServer.class.getResourceAsStream(resourceLocationMetadata) :
										new FileInputStream(metadataFile))
								{
									String projectsLoc = resourceLocation.substring(
											resourceLocation.lastIndexOf("projects") + 9);

									final String location = projectsLoc
											.substring(projectsLoc.indexOf(locationRoot));

									dataWalker.walk(in, metaIn, file, location, resourceLocation);
								}
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
		String locationRoot = this.locationFile != null ? this.locationFile.getPath() : this.jarLocation.getPath();

		String projectsLoc = filePath.substring(
				filePath.lastIndexOf("projects") + 9);

		return projectsLoc
				.substring(projectsLoc.indexOf(locationRoot));
	}

	@Override
	public void loadAndCacheData()
	{
		this.walkDataLoading((input, metadataInput, file, location, resourceLocation) -> {
			String extension = FilenameUtils.getExtension(file.getName());

			IDataLoader<OrbisProject> dataLoader = this.acceptedFileExtensions.get(extension);
			IMetadataLoader<OrbisProject> metadataLoader = this.metadataLoaders.get(extension);

			if (dataLoader == null)
			{
				return;
			}

			final IData data = dataLoader.loadData(this, file, location, input);

			if (data != null && !this.cache.getDataId(location).isPresent())
			{
				IDataMetadata metadata = metadataLoader.loadMetadata(this, file, location, metadataInput);

				data.setMetadata(metadata);

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
		void walk(InputStream in, InputStream metaIn, File file, String location, String resourceLocation) throws IOException;
	}
}
