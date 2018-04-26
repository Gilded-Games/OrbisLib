package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.OrbisAPICapabilities;
import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import com.gildedgames.orbis_api.preparation.IPrepManagerPool;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class PrepHelper
{
	/**
	 * Helper method which returns the {@link IPrepManagerPool} for a world, or throws
	 * an {@link RuntimeException} if it does not exist.
	 *
	 * @param world The world
	 * @return The {@link IPrepManagerPool} belonging to the world
	 */
	@Nonnull
	public static IPrepManagerPool getPool(World world)
	{
		IPrepManagerPool access = null;

		if (world.hasCapability(OrbisAPICapabilities.PREP_MANAGER_POOL, null))
		{
			access = world.getCapability(OrbisAPICapabilities.PREP_MANAGER_POOL, null);
		}

		if (access == null)
		{
			throw new RuntimeException("World does not have IPrepManagerPool capability");
		}

		return access;
	}

	/**
	 * Helper method which returns the {@link IPrepManagerPool} for a world, or throws
	 * an {@link RuntimeException} if it does not exist.
	 *
	 * @param world The world
	 * @return The {@link IPrepManagerPool} belonging to the world
	 */
	@Nonnull
	public static IPrepChunkManager getChunks(World world)
	{
		IPrepChunkManager access = null;

		if (world.hasCapability(OrbisAPICapabilities.PREP_CHUNK_MANAGER, null))
		{
			access = world.getCapability(OrbisAPICapabilities.PREP_CHUNK_MANAGER, null);
		}

		if (access == null)
		{
			throw new RuntimeException("World does not have IPrepManagerPool capability");
		}

		return access;
	}
}
