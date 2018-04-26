package com.gildedgames.orbis_api.util.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.List;

public class FileSystemPersistingCache<K, V> extends AbstractPersistingCache<K, V>
{

	private static final Logger LOGGER = LogManager.getLogger(FileSystemPersistingCache.class);

	private final File persistenceRootDirectory;

	protected FileSystemPersistingCache(CacheBuilder<Object, Object> cacheBuilder)
	{
		this(cacheBuilder, Files.createTempDir());
	}

	protected FileSystemPersistingCache(CacheBuilder<Object, Object> cacheBuilder, File persistenceDirectory)
	{
		this(cacheBuilder, persistenceDirectory, null);
	}

	protected FileSystemPersistingCache(CacheBuilder<Object, Object> cacheBuilder, RemovalListener<K, V> removalListener)
	{
		this(cacheBuilder, Files.createTempDir(), removalListener);
	}

	protected FileSystemPersistingCache(CacheBuilder<Object, Object> cacheBuilder, File persistenceDirectory, RemovalListener<K, V> removalListener)
	{
		super(cacheBuilder, removalListener);
		this.persistenceRootDirectory = this.validateDirectory(persistenceDirectory);
		LOGGER.info("Persisting to {}", persistenceDirectory.getAbsolutePath());
	}

	private File validateDirectory(File directory)
	{
		directory.mkdirs();
		if (!directory.exists() || !directory.isDirectory() || !directory.canRead() || !directory.canWrite())
		{
			throw new IllegalArgumentException(String.format("Directory %s cannot be used as a persistence directory",
					directory.getAbsolutePath()));
		}
		return directory;
	}

	private File pathToFileFor(K key)
	{
		List<String> pathSegments = this.directoryFor(key);
		File persistenceFile = this.persistenceRootDirectory;
		for (String pathSegment : pathSegments)
		{
			persistenceFile = new File(persistenceFile, pathSegment);
		}
		if (this.persistenceRootDirectory.equals(persistenceFile) || persistenceFile.isDirectory())
		{
			throw new IllegalArgumentException();
		}
		return persistenceFile;
	}

	@Override
	protected V findPersisted(K key) throws IOException
	{
		if (!this.isPersist(key))
		{
			return null;
		}

		File persistenceFile = this.pathToFileFor(key);

		if (!persistenceFile.exists())
		{
			return null;
		}

		FileInputStream fileInputStream = new FileInputStream(persistenceFile);
		try
		{
			FileLock fileLock = fileInputStream.getChannel().lock(0, Long.MAX_VALUE, true);
			try
			{
				return this.readPersisted(key, fileInputStream);
			}
			finally
			{
				fileLock.release();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally
		{
			fileInputStream.close();
		}
	}

	@Override
	protected void persistValue(K key, V value) throws IOException
	{
		if (!this.isPersist(key))
		{
			return;
		}
		File persistenceFile = this.pathToFileFor(key);
		persistenceFile.getParentFile().mkdirs();
		FileOutputStream fileOutputStream = new FileOutputStream(persistenceFile);
		try
		{
			FileLock fileLock = fileOutputStream.getChannel().lock();
			try
			{
				this.persist(key, value, fileOutputStream);
			}
			finally
			{
				fileLock.release();
			}
		}
		finally
		{
			fileOutputStream.close();
		}
	}

	@Override
	protected void persist(K key, V value, OutputStream outputStream) throws IOException
	{
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		objectOutputStream.writeObject(value);
		objectOutputStream.flush();
	}

	@Override
	protected boolean isPersist(K key)
	{
		return true;
	}

	@Override
	protected List<String> directoryFor(K key)
	{
		return Arrays.asList(key.toString());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected V readPersisted(K key, InputStream inputStream) throws IOException
	{
		try
		{
			return (V) new ObjectInputStream(inputStream).readObject();
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(String.format("Serialized version assigned by %s was invalid", key), e);
		}
	}

	@Override
	protected void deletePersistedIfExistent(K key)
	{
		File file = this.pathToFileFor(key);
		file.delete();
	}

	@Override
	protected void deleteAllPersisted()
	{
		for (File file : this.persistenceRootDirectory.listFiles())
		{
			file.delete();
		}
	}

	@Override
	protected int sizeOfPersisted()
	{
		return this.countFilesInFolders(this.persistenceRootDirectory);
	}

	private int countFilesInFolders(File directory)
	{
		int size = 0;
		for (File file : directory.listFiles())
		{
			if (file.isDirectory())
			{
				size += this.countFilesInFolders(file);
			}
			else if (!file.getName().startsWith("."))
			{
				size++;
			}
		}
		return size;
	}

	public File getPersistenceRootDirectory()
	{
		return this.persistenceRootDirectory;
	}
}
