package com.gildedgames.orbis.lib.preparation.impl.util;

import com.gildedgames.orbis.lib.OrbisLibCapabilities;
import com.gildedgames.orbis.lib.preparation.IPrepManager;
import com.gildedgames.orbis.lib.preparation.IPrepSector;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class PrepHelper
{
	/**
	 * Helper method which returns the {@link IPrepManager) for a world, or throws
	 * an {@link NoSuchElementException} if it does not exist.
	 *
	 * @param world The world
	 * @return The {@link IPrepManager} belonging to the world
	 */
	public static LazyOptional<IPrepManager> getManager(IWorld world)
	{
		return world.getWorld().getCapability(OrbisLibCapabilities.PREP_MANAGER, null);
	}

	public static IPrepSector getSector(World world, int chunkX, int chunkY)
	{
		IPrepManager manager = PrepHelper.getManager(world).orElseThrow(NoSuchElementException::new);

		try
		{
			return manager.getAccess().provideSectorForChunk(chunkX, chunkY, false).get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static boolean isSectorLoaded(World world, int chunkX, int chunkY)
	{
		IPrepManager manager = PrepHelper.getManager(world).orElseThrow(NoSuchElementException::new);

		return isSectorLoaded(manager, chunkX, chunkY);
	}

	public static boolean isSectorLoaded(IPrepManager manager, int chunkX, int chunkY)
	{
		Optional<IPrepSector> sector = manager.getAccess().getLoadedSectorForChunk(chunkX, chunkY);

		return sector.isPresent();
	}

}
