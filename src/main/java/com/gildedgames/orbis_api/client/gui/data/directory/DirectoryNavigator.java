package com.gildedgames.orbis_api.client.gui.data.directory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;

public class DirectoryNavigator implements IDirectoryNavigator
{

	private final Stack<File> pastHistory = new Stack<>();

	private final Stack<File> futureHistory = new Stack<>();

	private final List<INavigatorNode> nodesInCurrentDirectory = Lists.newArrayList();

	private final List<IDirectoryNavigatorListener> listeners = Lists.newArrayList();

	private final Map<File, List<File>> injectedDirectories = Maps.newHashMap();

	private final IDirectoryNodeFactory nodeFactory;

	private File currentDirectory;

	public DirectoryNavigator(final IDirectoryNodeFactory nodeFactory)
	{
		this.nodeFactory = nodeFactory;
	}

	@Override
	public void addListener(final IDirectoryNavigatorListener listener)
	{
		this.listeners.add(listener);
	}

	@Override
	public boolean removeListener(final IDirectoryNavigatorListener listener)
	{
		return this.listeners.remove(listener);
	}

	@Override
	public void onClickNode(INavigatorNode node)
	{
		this.listeners.forEach(l -> l.onNodeClick(this, node));
	}

	@Override
	public void onOpenNode(final INavigatorNode node)
	{
		this.listeners.forEach(l -> l.onNodeOpen(this, node));
	}

	@Override
	public void openDirectory(final File file)
	{
		if (!file.isDirectory())
		{
			throw new RuntimeException("File provided to DirectoryNavigator.viewDirectories() was not a directory.");
		}

		if (this.currentDirectory != null)
		{
			this.pastHistory.push(this.currentDirectory);
		}

		this.currentDirectory = file;

		this.futureHistory.clear();

		this.listeners.forEach(l -> l.onDirectoryOpen(this, file));

		this.refresh();
	}

	@Override
	public void injectDirectories(File forDirectory, List<File> injectedDirectories)
	{
		if (!forDirectory.isDirectory())
		{
			throw new RuntimeException("File provided to DirectoryNavigator.injectDirectories() 'forDirectory' was not a directory.");
		}

		for (File file : injectedDirectories)
		{
			if (!file.isDirectory())
			{
				throw new RuntimeException("File provided to DirectoryNavigator.injectDirectories() 'injectedDirectories' was not a directory.");
			}
		}

		this.injectedDirectories.put(forDirectory, injectedDirectories);
	}

	@Override
	public boolean canGoBack()
	{
		return !this.pastHistory.isEmpty();
	}

	@Override
	public boolean canGoForward()
	{
		return !this.futureHistory.isEmpty();
	}

	@Override
	public void back()
	{
		final File prev = this.pastHistory.pop();

		this.futureHistory.push(this.currentDirectory);

		this.currentDirectory = prev;

		this.listeners.forEach(l -> l.onBack(this));

		this.refresh();
	}

	@Override
	public void forward()
	{
		final File next = this.futureHistory.pop();

		this.pastHistory.push(this.currentDirectory);

		this.currentDirectory = next;

		this.listeners.forEach(l -> l.onForward(this));

		this.refresh();
	}

	@Override
	public void refresh()
	{
		this.nodesInCurrentDirectory.clear();

		List<File> directories = Lists.newArrayList(this.currentDirectory);

		if (this.injectedDirectories.containsKey(this.currentDirectory))
		{
			directories.addAll(this.injectedDirectories.get(this.currentDirectory));
		}

		for (File directory : directories)
		{
			try (Stream<Path> paths = Files.walk(Paths.get(directory.getPath())))
			{
				paths.forEach(p ->
				{
					final File file = p.toFile();

					try
					{
						final String parent = file.getCanonicalPath().replace(file.getName(), "");
						final String currentDirParent = directory.getCanonicalPath().replace(file.getName(), "") + File.separator;

						if (file.getPath().equals(directory.getPath()) || !parent.equals(currentDirParent))
						{
							return;
						}
					}
					catch (final IOException e)
					{
						e.printStackTrace();
					}

					final String extension = file.isDirectory() ? "" : FilenameUtils.getExtension(file.getName());

					final INavigatorNode node = this.nodeFactory.createFrom(file, extension);

					if (node != null)
					{
						this.nodesInCurrentDirectory.add(node);
					}
				});
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
		}

		this.nodesInCurrentDirectory.sort((p1, p2) ->
		{
			final File f1 = p1.getFile();
			final File f2 = p2.getFile();

			int flag = 0;

			if (f1.isDirectory() && !f2.isDirectory())
			{
				flag = -1;
			}

			if (!f1.isDirectory() && f2.isDirectory())
			{
				flag = 1;
			}

			return flag;
		});

		this.listeners.forEach(l -> l.onRefresh(this));
	}

	@Override
	public File currentDirectory()
	{
		return this.currentDirectory;
	}

	@Override
	public List<INavigatorNode> getNodes()
	{
		return this.nodesInCurrentDirectory;
	}

}
