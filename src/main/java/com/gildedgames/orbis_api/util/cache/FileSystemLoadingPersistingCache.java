package com.gildedgames.orbis_api.util.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class FileSystemLoadingPersistingCache<K, V> extends FileSystemPersistingCache<K, V> implements LoadingCache<K, V>
{

	private final CacheLoader<? super K, V> cacheLoader;

	protected FileSystemLoadingPersistingCache(CacheBuilder<Object, Object> cacheBuilder, CacheLoader<? super K, V> cacheLoader)
	{
		this(cacheBuilder, cacheLoader, Files.createTempDir());
	}

	protected FileSystemLoadingPersistingCache(CacheBuilder<Object, Object> cacheBuilder, CacheLoader<? super K, V> cacheLoader, File persistenceDirectory)
	{
		this(cacheBuilder, cacheLoader, persistenceDirectory, null);
	}

	protected FileSystemLoadingPersistingCache(CacheBuilder<Object, Object> cacheBuilder, CacheLoader<? super K, V> cacheLoader,
			RemovalListener<K, V> removalListener)
	{
		this(cacheBuilder, cacheLoader, Files.createTempDir(), removalListener);
	}

	protected FileSystemLoadingPersistingCache(CacheBuilder<Object, Object> cacheBuilder, CacheLoader<? super K, V> cacheLoader,
			File persistenceDirectory, RemovalListener<K, V> removalListener)
	{
		super(cacheBuilder, persistenceDirectory, removalListener);
		this.cacheLoader = cacheLoader;
	}

	@Override
	public V get(K key) throws ExecutionException
	{
		return this.get(key, new ValueLoaderFromCacheLoader(this.cacheLoader, key));
	}

	@Override
	public V getUnchecked(K key)
	{
		try
		{
			return this.get(key, new ValueLoaderFromCacheLoader(this.cacheLoader, key));
		}
		catch (ExecutionException e)
		{
			throw new RuntimeException(e.getCause());
		}
	}

	@Override
	public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException
	{
		ImmutableMap.Builder<K, V> all = ImmutableMap.builder();
		for (K key : keys)
		{
			all.put(key, this.get(key));
		}
		return all.build();
	}

	@Override
	public V apply(K key)
	{
		try
		{
			return this.cacheLoader.load(key);
		}
		catch (Exception e)
		{
			throw new RuntimeException(String.format("Could not apply cache on key %s", key), e);
		}
	}

	@Override
	public void refresh(K key)
	{
		try
		{
			this.getUnderlyingCache().put(key, this.cacheLoader.load(key));
		}
		catch (Exception e)
		{
			throw new RuntimeException(String.format("Could not refresh value for key %s", key), e);
		}
	}

	private class ValueLoaderFromCacheLoader implements Callable<V>
	{

		private final K key;

		private final CacheLoader<? super K, V> cacheLoader;

		private ValueLoaderFromCacheLoader(CacheLoader<? super K, V> cacheLoader, K key)
		{
			this.key = key;
			this.cacheLoader = cacheLoader;
		}

		@Override
		public V call() throws Exception
		{
			return this.cacheLoader.load(this.key);
		}
	}
}
