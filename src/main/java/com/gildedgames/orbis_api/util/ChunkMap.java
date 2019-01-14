package com.gildedgames.orbis_api.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.ChunkPos;

import java.util.Collection;

/**
 * A fast map for storing entries on a 2D integer coordinate system.
 *
 * @param <T> The type that will be stored in this map
 */
public class ChunkMap<T>
{
	protected Long2ObjectOpenHashMap<T> map = new Long2ObjectOpenHashMap<>();

	public ChunkMap()
	{

	}

	public static <T> ChunkMap<T> createFrom(Long2ObjectOpenHashMap<T> map)
	{
		ChunkMap<T> created = new ChunkMap<>();
		created.map = map;

		return created;
	}

	public Long2ObjectOpenHashMap<T> getInnerMap()
	{
		return this.map;
	}

	/**
	 * Returns whether or not this map contains the value at the coordinates.
	 *
	 * @param x The x-coordinate to check
	 * @param z The z-coordinate to check
	 * @return True if the map contains an entry, otherwise false
	 */
	public boolean containsKey(int x, int z)
	{
		return this.map.containsKey(ChunkPos.asLong(x, z));
	}

	/**
	 * Gets an entry from the map at the specified coordinates.
	 *
	 * @param x The entry's x-coordinate
	 * @param z The entry's z-coordinate
	 * @return The value in the map at the specified coordinates, null if none
	 */
	public T get(int x, int z)
	{
		return this.map.get(ChunkPos.asLong(x, z));
	}

	/**
	 * Puts an entry into the map, replacing the existing value if it exists. If
	 * the value is null, the entry will be removed.
	 *
	 * @param x The entry's x-coordinate
	 * @param z The entry's z-coordinate
	 * @param value The value to put, null to remove the entry from the map
	 * @return The previous value at the coordinates specified, null if none
	 */
	public T put(int x, int z, T value)
	{
		if (value == null)
		{
			return this.remove(x, z);
		}

		long key = ChunkPos.asLong(x, z);

		return this.map.put(key, value);
	}

	/**
	 * Removes an entry from the map.
	 *
	 * @param x The entry's x-coordinate
	 * @param z The entry's z-coordinate
	 * @return The entry removed from the map, null if none.
	 */
	public T remove(int x, int z)
	{
		return this.map.remove(ChunkPos.asLong(x, z));
	}

	/**
	 * @return The number of entries in the map
	 */
	public int size()
	{
		return this.map.size();
	}

	/**
	 * @return True if this map is empty, otherwise true
	 */
	public boolean isEmpty()
	{
		return this.map.size() == 0;
	}

	/**
	 * Returns the {@link Collection<T>} of values in this map.
	 * @return A {@link Collection<T>} of values in this map, empty if none
	 */
	public Collection<T> getValues()
	{
		return this.map.values();
	}

	/**
	 * Clears the map.
	 */
	public void clear()
	{
		this.map.clear();
	}
}
