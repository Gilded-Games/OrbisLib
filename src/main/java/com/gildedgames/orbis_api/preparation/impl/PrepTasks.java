package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.OrbisAPICapabilities;
import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import com.gildedgames.orbis_api.preparation.IPrepManager;
import com.gildedgames.orbis_api.preparation.IPrepRegistryEntry;
import com.gildedgames.orbis_api.preparation.impl.util.PrepHelper;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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

	private static List<CompletableFuture<Void>> tasks = Lists.newArrayList();

	public PrepTasks()
	{

	}

	public static void prepSector(World world, int sectorX, int sectorY)
	{
		IPrepManager manager = PrepHelper.getManager(world);

		if (manager != null)
		{
			prepSector(manager, world, sectorX, sectorY);
		}
	}

	public static void prepSector(IPrepManager manager, World world, int sectorX, int sectorY)
	{
		if (world.isRemote)
		{
			return;
		}

		boolean preparing = manager.isSectorPreparing(sectorX, sectorY);
		boolean writtenToDisk = manager.isSectorWrittenToDisk(sectorX, sectorY);

		if (!preparing && !writtenToDisk)
		{
			IPrepChunkManager chunkManager = manager.getChunkManager();

			manager.markSectorPreparing(sectorX, sectorY);
			CompletableFuture<Void> task = CompletableFuture
					.supplyAsync(new PrepareSectorTask(manager.getRegistryEntry(), manager, chunkManager, world, sectorX, sectorY), executor);

			tasks.add(task);
		}
		else if (writtenToDisk)
		{
			manager.access()
					.provideSector(sectorX * manager.getRegistryEntry().getSectorChunkArea(), sectorY * manager.getRegistryEntry().getSectorChunkArea());
		}
	}

	@SubscribeEvent
	public static void onTick(TickEvent.ServerTickEvent event)
	{
		List<CompletableFuture<Void>> toRemove = Lists.newArrayList();

		for (CompletableFuture<Void> task : tasks)
		{
			if (task.isDone())
			{
				toRemove.add(task);

				try
				{
					task.get();
				}
				catch (InterruptedException | ExecutionException e)
				{
					e.printStackTrace();
				}
			}
		}

		tasks.removeAll(toRemove);
	}

	/*@SubscribeEvent
	public static void onWorldLoaded(final WorldEvent.Load event)
	{
		final World world = event.getWorld();

		boolean shouldAttach = false;

		for (IPrepRegistryEntry entry : OrbisAPI.sectors().getEntries())
		{
			if (entry.shouldAttachTo(world))
			{
				shouldAttach = true;
				break;
			}
		}

		if (shouldAttach)
		{
			if (world instanceof WorldServer)
			{
				WorldServer worldServer = (WorldServer) world;

				IChunkProvider provider = new ChunkProviderPrepServer(worldServer, worldServer.getChunkProvider());

				ObfuscationReflectionHelper.setPrivateValue(World.class, world, provider, "field_73020_y", "chunkProvider");
			}
			else if (world instanceof WorldClient)
			{
				WorldClient worldClient = (WorldClient) world;

				IChunkProvider provider = new ChunkProviderPrepClient(worldClient, worldClient.getChunkProvider());

				ObfuscationReflectionHelper.setPrivateValue(World.class, world, provider, "field_73020_y", "chunkProvider");
			}
		}
	}*/

	@SubscribeEvent
	public static void onChunkLoaded(final ChunkEvent.Load event)
	{
		final World world = event.getWorld();

		if (world.hasCapability(OrbisAPICapabilities.PREP_MANAGER, null))
		{
			IPrepManager manager = PrepHelper.getManager(world);

			if (manager != null)
			{
				manager.access().onChunkLoaded(event.getChunk().x, event.getChunk().z);
			}
		}
	}

	@SubscribeEvent
	public static void onChunkUnloaded(final ChunkEvent.Unload event)
	{
		final World world = event.getWorld();

		if (world.hasCapability(OrbisAPICapabilities.PREP_MANAGER, null))
		{
			IPrepManager manager = PrepHelper.getManager(world);

			if (manager != null)
			{
				manager.access().onChunkUnloaded(event.getChunk().x, event.getChunk().z);
			}
		}
	}

	@SubscribeEvent
	public static void onWorldSaved(final WorldEvent.Save event)
	{
		final World world = event.getWorld();

		if (world.hasCapability(OrbisAPICapabilities.PREP_MANAGER, null))
		{
			IPrepManager manager = PrepHelper.getManager(world);

			if (manager != null)
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

			IPrepManager manager = PrepHelper.getManager(world);

			if (manager == null)
			{
				return;
			}

			if (!PrepHelper.isSectorLoaded(manager, 0, 0))
			{
				return;
			}

			IPrepRegistryEntry entry = manager.getRegistryEntry();

			int chunkX = ((int) player.posX) >> 4;
			int chunkY = ((int) player.posZ) >> 4;

			int minChunkX = chunkX - CHUNK_RADIUS_SEARCHING;
			int minChunkY = chunkY - CHUNK_RADIUS_SEARCHING;

			int maxChunkX = chunkX + CHUNK_RADIUS_SEARCHING;
			int maxChunkY = chunkY + CHUNK_RADIUS_SEARCHING;

			int centerSectorX = Math.floorDiv(chunkX, entry.getSectorChunkArea());
			int centerSectorY = Math.floorDiv(chunkY, entry.getSectorChunkArea());

			// Prepare this first to give priority to sectors the player is in
			prepSector(manager, world, centerSectorX, centerSectorY);

			int minSectorX = Math.floorDiv(minChunkX, entry.getSectorChunkArea());
			int minSectorY = Math.floorDiv(minChunkY, entry.getSectorChunkArea());

			int maxSectorX = Math.floorDiv(maxChunkX, entry.getSectorChunkArea());
			int maxSectorY = Math.floorDiv(maxChunkY, entry.getSectorChunkArea());

			for (int x = minSectorX; x < maxSectorX; x++)
			{
				for (int y = minSectorY; y < maxSectorY; y++)
				{
					prepSector(manager, world, x, y);
				}
			}
		}
	}

}
