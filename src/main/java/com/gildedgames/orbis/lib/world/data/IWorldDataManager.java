package com.gildedgames.orbis.lib.world.data;

import java.io.IOException;

public interface IWorldDataManager
{
	/**
	 * Registers a {@link IWorldData} to the data manager. This should only be called once, and before
	 * you interact with this class.
	 *
	 * @param data The data manager to register
	 */
	void register(IWorldData data);

	/**
	 * Returns the array of bytes stored on disk for the specified path.
	 *
	 * @param data The {@link IWorldData} that is accessing this data
	 * @param path The path of the data
	 * @return The array of bytes as stored exactly on disk, or null if no resource exists
	 * @throws IOException If an I/O error occurs while reading
	 */
	byte[] readBytes(IWorldData data, String path) throws IOException;

	/**
	 * Writes an array of bytes to dsik for the specified path. This should only be called
	 * from the context of {@link IWorldDataManager#flush()} to ensure consistency.
	 *
	 * @param data The {@link IWorldData} that is writing this data
	 * @param path The path of the data
	 * @param bytes The array of bytes to be stored exactly as is on disk
	 * @throws IOException If an I/O error occurs while reading
	 */
	void writeBytes(IWorldData data, String path, byte[] bytes) throws IOException;

	/**
	 * Called when the world is performing a save. This will forward the flush event
	 * to all registered {@link IWorldData}.
	 */
	void flush();

	/**
	 * Called when the world is unloaded.
	 */
	void close();
}
