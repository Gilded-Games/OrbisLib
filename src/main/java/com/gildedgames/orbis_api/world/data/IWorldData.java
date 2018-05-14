package com.gildedgames.orbis_api.world.data;

import net.minecraft.util.ResourceLocation;

public interface IWorldData
{
	/**
	 * Returns the {@link ResourceLocation} of the database that will store data for this {@link IWorldData}.
	 * This should never change throughout the lifespan of the {@link IWorldData}.
	 *
	 * @return A {@link ResourceLocation} without spaces or special symbols
	 */
	ResourceLocation getName();

	/**
	 * Called when the world is saving data. This may or may not be called from a thread different
	 * to the main server thread. You should only perform write operations in the context of this
	 * method.
	 */
	void flush();
}
