package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.OrbisAPICapabilities;
import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import com.gildedgames.orbis_api.preparation.IPrepManager;
import com.gildedgames.orbis_api.preparation.IPrepManagerPool;
import com.gildedgames.orbis_api.preparation.IPrepRegistryEntry;
import com.gildedgames.orbis_api.preparation.impl.capability.PrepHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrepTasks
{

	/**
	 * The chunk radius at which the manager searches around players to mark
	 * sectors as active.
	 */
	private static int CHUNK_RADIUS_SEARCHING = 180;

	private static ExecutorService executor = Executors.newFixedThreadPool(5);

	public PrepTasks()
	{

	}

	public static void prepSector(World world, int sectorX, int sectorY, IPrepRegistryEntry entry)
	{
		IPrepManagerPool pool = PrepHelper.getPool(world);
		IPrepChunkManager chunkManager = PrepHelper.getChunks(world);

		IPrepManager manager = pool.get(entry.getUniqueId());

		if (!manager.isSectorPreparing(sectorX, sectorY) && !manager.isSectorWrittenToDisk(sectorX, sectorY))
		{
			CompletableFuture.runAsync(new PrepareSectorTask(entry, manager, chunkManager, world, sectorX, sectorY), executor);
		}
	}

	@SubscribeEvent
	public static void onChunkLoaded(final ChunkEvent.Load event)
	{
		final World world = event.getWorld();

		if (world.hasCapability(OrbisAPICapabilities.PREP_MANAGER_POOL, null))
		{
			IPrepManagerPool pool = PrepHelper.getPool(world);

			for (IPrepManager manager : pool.getManagers())
			{
				manager.access().onChunkLoaded(event.getChunk().x, event.getChunk().z);
			}
		}
	}

	@SubscribeEvent
	public static void onChunkUnloaded(final ChunkEvent.Unload event)
	{
		final World world = event.getWorld();

		if (world.hasCapability(OrbisAPICapabilities.PREP_MANAGER_POOL, null))
		{
			IPrepManagerPool pool = PrepHelper.getPool(world);

			for (IPrepManager manager : pool.getManagers())
			{
				manager.access().onChunkUnloaded(event.getChunk().x, event.getChunk().z);
			}
		}
	}

	@SubscribeEvent
	public static void onWorldSaved(final WorldEvent.Save event)
	{
		final World world = event.getWorld();

		if (world.hasCapability(OrbisAPICapabilities.PREP_MANAGER_POOL, null))
		{
			IPrepManagerPool pool = PrepHelper.getPool(world);

			for (IPrepManager manager : pool.getManagers())
			{
				manager.access().flush();
			}
		}
	}

	@SubscribeEvent
	public static void onEvent(LivingEvent.LivingUpdateEvent event)
	{
		if (event.getEntity() instanceof EntityPlayer && !event.getEntity().getEntityWorld().isRemote)
		{
			EntityPlayer player = (EntityPlayer) event.getEntity();
			World world = player.getEntityWorld();

			IPrepManagerPool pool = PrepHelper.getPool(world);
			IPrepChunkManager chunkManager = PrepHelper.getChunks(world);

			int chunkX = ((int) player.posX) >> 4;
			int chunkY = ((int) player.posZ) >> 4;

			int minChunkX = chunkX - CHUNK_RADIUS_SEARCHING;
			int minChunkY = chunkY - CHUNK_RADIUS_SEARCHING;

			int maxChunkX = chunkX + CHUNK_RADIUS_SEARCHING;
			int maxChunkY = chunkY + CHUNK_RADIUS_SEARCHING;

			for (IPrepRegistryEntry entry : OrbisAPI.sectors().getEntries())
			{
				if (!entry.shouldAttachTo(world))
				{
					continue;
				}

				IPrepManager manager = pool.get(entry.getUniqueId());

				int centerSectorX = chunkX / entry.getSectorChunkArea();
				int centerSectorY = chunkY / entry.getSectorChunkArea();

				// Prepare this first to give priority to sectors the player is in
				if (!manager.isSectorPreparing(centerSectorX, centerSectorY) && !manager.isSectorWrittenToDisk(centerSectorX, centerSectorY))
				{
					executor.submit(new PrepareSectorTask(entry, manager, chunkManager, world, centerSectorX, centerSectorY));
				}

				int minSectorX = minChunkX / entry.getSectorChunkArea();
				int minSectorY = minChunkY / entry.getSectorChunkArea();

				int maxSectorX = maxChunkX / entry.getSectorChunkArea();
				int maxSectorY = maxChunkY / entry.getSectorChunkArea();

				for (int x = minSectorX; x < maxSectorX; x++)
				{
					for (int y = minSectorY; y < maxSectorY; y++)
					{
						if (!manager.isSectorPreparing(x, y) && !manager.isSectorWrittenToDisk(x, y))
						{
							CompletableFuture.runAsync(new PrepareSectorTask(entry, manager, chunkManager, world, x, y), executor);
						}
					}
				}
			}
		}
	}

}
