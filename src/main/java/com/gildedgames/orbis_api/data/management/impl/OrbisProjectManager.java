package com.gildedgames.orbis_api.data.management.impl;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.core.exceptions.OrbisMissingDataException;
import com.gildedgames.orbis_api.core.exceptions.OrbisMissingProjectException;
import com.gildedgames.orbis_api.data.management.*;
import com.gildedgames.orbis_api.util.mc.FileHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class OrbisProjectManager implements IProjectManager
{
	private final File baseDirectory;

	private final Map<IProjectIdentifier, IProject> idToProject = Maps.newHashMap();

	private final Map<String, IProject> nameToProject = Maps.newHashMap();

	private Object mod;

	private String archiveBaseName;

	private Set<IProjectManagerListener> listeners = Sets.newHashSet();

	private Supplier<IProject> projectFactory;

	private List<File> extraProjectSources;

	public OrbisProjectManager(final File baseDirectory, List<File> extraProjectSources, Object mod, String archiveBaseName, Supplier<IProject> projectFactory)
	{
		if (!baseDirectory.exists() && !baseDirectory.mkdirs())
		{
			throw new RuntimeException("Base directory for OrbisProjectManager cannot be created!");
		}

		if (!baseDirectory.isDirectory())
		{
			throw new IllegalArgumentException("File passed into OrbisProjectManager is not a directory!");
		}

		this.extraProjectSources = extraProjectSources;

		this.baseDirectory = baseDirectory;
		this.mod = mod;
		this.archiveBaseName = archiveBaseName;
		this.projectFactory = projectFactory;
	}

	public static boolean isProjectDirectory(final File file)
	{
		final File[] innerFiles = file.listFiles();

		if (innerFiles != null)
		{
			/** Attempt to find the hidden .project file that contains
			 * the metadata for the project **/
			for (final File innerFile : innerFiles)
			{
				if (innerFile != null && !innerFile.isDirectory() && innerFile.getName().equals("project_data.json"))
				{
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public List<File> getExtraProjectSourceFolders()
	{
		return this.extraProjectSources;
	}

	@Override
	public Supplier<IProject> getProjectFactory()
	{
		return this.projectFactory;
	}

	@Override
	public void listen(IProjectManagerListener listener)
	{
		this.listeners.add(listener);
	}

	@Override
	public boolean unlisten(IProjectManagerListener listener)
	{
		return this.listeners.remove(listener);
	}

	@Override
	public void cacheProject(final String folderName, final IProject project)
	{
		this.nameToProject.put(folderName, project);
		this.idToProject.put(project.getInfo().getIdentifier(), project);
	}

	@Override
	public File getLocation()
	{
		return this.baseDirectory;
	}

	@Override
	public void flushProjects()
	{
		this.idToProject.values().forEach((project) ->
		{
			if (!project.isModProject())
			{
				this.saveProjectToDisk(project);
			}
		});
	}

	private void walkProjects(final BiConsumer<File, File> action)
	{
		final File[] files = this.baseDirectory.listFiles();

		if (files == null)
		{
			return;
		}

		for (final File file : files)
		{
			/** Once we've found a directory, fetch files inside project directory **/
			if (file != null && file.isDirectory())
			{
				final File[] innerFiles = file.listFiles();

				if (innerFiles != null)
				{
					/** Attempt to find the hidden .project file that contains
					 * the metadata for the project **/
					for (final File innerFile : innerFiles)
					{
						if (innerFile != null && !innerFile.isDirectory() && innerFile.getName().equals("project_data.json"))
						{
							action.accept(innerFile, file);
						}
					}
				}
			}
		}
	}

	@Override
	public void refreshCache()
	{
		final List<IProjectIdentifier> foundProjects = Lists.newArrayList();

		this.walkProjects((innerFile, file) ->
		{
			try (FileInputStream in = new FileInputStream(innerFile))
			{
				try (InputStreamReader reader = new InputStreamReader(in))
				{
					try
					{
						ProjectInformation info = OrbisAPI.services().getGson().fromJson(reader, ProjectInformation.class);

						foundProjects.add(info.getIdentifier());

						if (!this.idToProject.keySet().contains(info.getIdentifier()))
						{
							IProject project = this.projectFactory.get();
							project.setInfo(info);

							project.setModAndArchiveLoadingFrom(this.mod, this.archiveBaseName);

							project.setLocationAsFile(file);

							project.loadAndCacheData();

							this.cacheProject(file.getName(), project);
						}
					}
					catch (JsonSyntaxException | JsonIOException e)
					{
						OrbisAPI.LOGGER.error("Failed to load project info from json file", e);
					}
				}
			}
			catch (final IOException e)
			{
				OrbisAPI.LOGGER.catching(e);
			}
		});

		final List<IProjectIdentifier> projectsToRemove = Lists.newArrayList();

		this.idToProject.forEach((id, p) -> {

			if (!foundProjects.contains(id))
			{
				if (!p.isModProject())
				{
					projectsToRemove.add(id);
				}
			}
		});

		projectsToRemove.forEach(this.idToProject::remove);
	}

	@Override
	public void scanAndCacheProjects()
	{
		this.walkProjects((innerFile, file) ->
		{
			/** When found, load and cache the project into memory **/
			try (FileInputStream in = new FileInputStream(innerFile))
			{
				try (InputStreamReader reader = new InputStreamReader(in))
				{
					try
					{
						ProjectInformation info = OrbisAPI.services().getGson().fromJson(reader, ProjectInformation.class);

						IProject project = this.projectFactory.get();
						project.setInfo(info);

						project.setLocationAsFile(file);

						boolean needsToResave = false;

						if (this.idToProject.containsKey(project.getInfo().getIdentifier()))
						{
							OrbisAPI.LOGGER.error("WARNING: A project (" + project.getInfo().getIdentifier()
									+ ") has not been loaded since it has the same id as another existing project.");

							return;
						}

						if (needsToResave)
						{
							this.saveProjectToDisk(project);
						}

						this.cacheProject(file.getName(), project);

						project.setModAndArchiveLoadingFrom(this.mod, this.archiveBaseName);

						project.loadAndCacheData();
					}
					catch (JsonSyntaxException | JsonIOException e)
					{
						OrbisAPI.LOGGER.error("Failed to load project info from json file", e);
					}
				}
				catch (final IOException e)
				{
					OrbisAPI.LOGGER.catching(e);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		});
	}

	@Override
	public Collection<IProject> getCachedProjects()
	{
		return this.idToProject.values();
	}

	private boolean findAndLoadProject(String folderName)
	{
		final boolean[] flag = new boolean[1];

		this.walkProjects((innerFile, file) ->
		{
			/** When found, load and cache the project into memory **/
			try (FileInputStream in = new FileInputStream(innerFile))
			{
				try (InputStreamReader reader = new InputStreamReader(in))
				{
					try
					{
						ProjectInformation info = OrbisAPI.services().getGson().fromJson(reader, ProjectInformation.class);
						IProject project = this.projectFactory.get();

						project.setInfo(info);

						//TODO: This will neverh ave the location as file set???
						if (project.getLocationAsFile().getName().equals(folderName))
						{
							project.setLocationAsFile(file);

							this.cacheProject(file.getName(), project);

							project.setModAndArchiveLoadingFrom(this.mod, this.archiveBaseName);

							project.loadAndCacheData();

							flag[0] = true;
						}
					}
					catch (JsonSyntaxException | JsonIOException e)
					{
						OrbisAPI.LOGGER.error("Failed to load project info from json file", e);
					}
				}
			}
			catch (final IOException e)
			{
				OrbisAPI.LOGGER.catching(e);
			}
		});

		return flag[0];
	}

	private boolean findAndLoadProject(IProjectIdentifier identifier)
	{
		final boolean[] flag = new boolean[1];

		this.walkProjects((innerFile, file) ->
		{
			/** When found, load and cache the project into memory **/
			try (FileInputStream in = new FileInputStream(innerFile))
			{
				try (InputStreamReader reader = new InputStreamReader(in))
				{
					try
					{
						ProjectInformation info = OrbisAPI.services().getGson().fromJson(reader, ProjectInformation.class);

						if (info.getIdentifier().equals(identifier))
						{
							final IProject project = this.projectFactory.get();
							project.setInfo(info);

							project.setLocationAsFile(file);

							this.cacheProject(file.getName(), project);

							project.setModAndArchiveLoadingFrom(this.mod, this.archiveBaseName);

							project.loadAndCacheData();

							flag[0] = true;
						}
					}
					catch (JsonSyntaxException | JsonIOException e)
					{
						OrbisAPI.LOGGER.error("Failed to load project info from json file", e);
					}
				}
			}
			catch (final IOException e)
			{
				OrbisAPI.LOGGER.catching(e);
			}
		});

		return flag[0];
	}

	@Nullable
	@Override
	public <T extends IProject> Optional<T> findProject(final String folderName) throws OrbisMissingProjectException
	{
		final IProject project = this.nameToProject.get(folderName);

		if (project == null)
		{
			// Try to find and load project if not loaded yet
			if (this.findAndLoadProject(folderName))
			{
				return Optional.of((T) this.nameToProject.get(folderName));
			}

			throw new OrbisMissingProjectException(folderName);
		}

		return Optional.of((T) project);
	}

	@Nullable
	@Override
	public <T extends IProject> Optional<T> findProject(final IProjectIdentifier identifier) throws OrbisMissingProjectException
	{
		final IProject project = this.idToProject.get(identifier);

		if (project == null)
		{
			// Try to find and load project if not loaded yet
			if (this.findAndLoadProject(identifier))
			{
				return Optional.of((T) this.idToProject.get(identifier));
			}

			throw new OrbisMissingProjectException(identifier);
		}

		return Optional.of((T) project);
	}

	@Nullable
	@Override
	public <T extends IData> Optional<T> findData(final IProject project, final File file) throws OrbisMissingDataException, OrbisMissingProjectException
	{
		try
		{
			final boolean isInProject = file.getCanonicalPath().startsWith(project.getLocationAsFile().getCanonicalPath() + File.separator);

			if (isInProject)
			{
				final String dataLocation = file.getCanonicalPath().replace(project.getLocationAsFile().getCanonicalPath() + File.separator, "");

				final Optional<UUID> dataId = project.getCache().getDataId(dataLocation);

				if (dataId.isPresent())
				{
					return project.getCache().getData(dataId.get());
				}
			}
		}
		catch (final IOException e)
		{
			OrbisAPI.LOGGER.error(e);
		}

		return Optional.empty();
	}

	@Nullable
	@Override
	public <T extends IData> Optional<T> findData(final IDataIdentifier identifier) throws OrbisMissingDataException, OrbisMissingProjectException
	{
		final Optional<IProject> projectOp = this.findProject(identifier.getProjectIdentifier());

		if (!projectOp.isPresent())
		{
			throw new NullPointerException("Project is null when trying to find data!");
		}

		IProject project = projectOp.get();

		Optional<IData> data = project.getCache().getData(identifier.getDataId());

		if (!data.isPresent())
		{
			// Try to find and load data if not loaded yet
			if (project.findAndLoadData(identifier))
			{
				return project.getCache().getData(identifier.getDataId());
			}

			throw new OrbisMissingDataException(identifier);
		}

		return (Optional<T>) data;
	}

	@Nullable
	@Override
	public <T extends IDataMetadata> Optional<T> findMetadata(final IDataIdentifier identifier) throws OrbisMissingDataException, OrbisMissingProjectException
	{
		final Optional<IProject> projectOp = this.findProject(identifier.getProjectIdentifier());

		if (!projectOp.isPresent())
		{
			throw new NullPointerException("Project is null when trying to find data!");
		}

		IProject project = projectOp.get();

		final Optional<IDataMetadata> metadata = project.getCache().getMetadata(identifier.getDataId());

		if (!metadata.isPresent())
		{
			throw new OrbisMissingDataException(identifier);
		}

		return (Optional<T>) metadata;
	}

	@Override
	public <T extends IProject> T createAndSaveProject(final String name, final IProjectIdentifier identifier)
	{
		final File file = new File(this.baseDirectory, name);
		final IProject project = new OrbisProject(file,
				new ProjectInformation(identifier, new ProjectMetadata()));

		this.saveProjectToDisk(project);
		this.cacheProject(name, project);

		return (T) project;
	}

	@Override
	public <T extends IProject> T saveProjectIfDoesntExist(final String name, final IProject project)
	{
		final File location = new File(this.baseDirectory, name);

		final IProject existing = this.idToProject.get(project.getInfo().getIdentifier());

		if (existing != null && existing.getLocationAsFile().exists())
		{
			/**
			 * Check if project already exists and it has the same
			 * "last changed" date. If so, move that project to the
			 * requested folder.
			 *
			 * Then return so new project instance isn't cached.
			 */
			if (existing.getInfo().getMetadata().getLastChanged().equals(project.getInfo().getMetadata().getLastChanged()))
			{
				if (!existing.getLocationAsFile().equals(location))
				{
					if (!location.exists() || location.delete())
					{
						if (existing.getLocationAsFile().renameTo(location))
						{
							this.nameToProject.remove(existing.getLocationAsFile().getName());

							if (existing.getLocationAsFile().delete())
							{
								existing.setLocationAsFile(location);

								this.nameToProject.put(name, existing);
							}
						}
						else
						{
							throw new RuntimeException("Could not rename project folder. Abort!");
						}
					}
				}

				return (T) existing;
			}
		}

		if (!location.exists())
		{
			if (!location.mkdirs())
			{
				throw new RuntimeException("Location for Project cannot be created!");
			}
		}

		project.setLocationAsFile(location);

		this.saveProjectToDisk(project);
		this.cacheProject(name, project);

		return (T) existing;
	}

	@Override
	public boolean projectNameExists(final String name)
	{
		return this.nameToProject.containsKey(name);
	}

	@Override
	public boolean projectExists(final IProjectIdentifier id)
	{
		return this.idToProject.containsKey(id);
	}

	private void saveProjectToDisk(final IProject project)
	{
		final File projectFile = new File(project.getLocationAsFile(), "project_data.json");

		try
		{
			if (projectFile.exists())
			{
				FileHelper.unhide(projectFile);
			}

			try (FileOutputStream out = new FileOutputStream(projectFile))
			{
				try (OutputStreamWriter writer = new OutputStreamWriter(out))
				{
					try
					{
						OrbisAPI.services().getGson().toJson(project.getInfo(), writer);
					}
					catch (JsonIOException e)
					{
						OrbisAPI.LOGGER.error("Failed to save Project info to json file", e);
					}
				}
			}
			catch (final IOException e)
			{
				OrbisAPI.LOGGER.error("Failed to save Project to disk", e);
			}

			FileHelper.hide(projectFile);
		}
		catch (final IOException e)
		{
			OrbisAPI.LOGGER.error(e);
		}
	}

}
