package com.gildedgames.orbis_api.preparation.impl.util;

import com.gildedgames.orbis_api.OrbisAPICapabilities;
import com.gildedgames.orbis_api.preparation.IPrepManager;
import com.gildedgames.orbis_api.preparation.IPrepSector;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class PrepHelper
{
	/**
	 * Helper method which returns the {@link IPrepManager) for a world, or throws
	 * an {@link RuntimeException} if it does not exist.
	 *
	 * @param world The world
	 * @return The {@link IPrepManager} belonging to the world
	 */
	@Nullable
	public static IPrepManager getManager(World world)
	{
		IPrepManager access = null;

		if (world.hasCapability(OrbisAPICapabilities.PREP_MANAGER, null))
		{
			access = world.getCapability(OrbisAPICapabilities.PREP_MANAGER, null);
		}

		return access;
	}

	public static IPrepSector getSector(World world, int chunkX, int chunkY)
	{
		IPrepManager manager = PrepHelper.getManager(world);

		try
		{
			return manager.access().provideSector(chunkX, chunkY).get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static boolean isSectorLoaded(World world, int chunkX, int chunkY)
	{
		IPrepManager manager = PrepHelper.getManager(world);

		return isSectorLoaded(manager, chunkX, chunkY);
	}

	public static boolean isSectorLoaded(IPrepManager manager, int chunkX, int chunkY)
	{
		Optional<IPrepSector> sector = manager.access().getLoadedSector(chunkX, chunkY);

		return sector.isPresent();
	}
}
