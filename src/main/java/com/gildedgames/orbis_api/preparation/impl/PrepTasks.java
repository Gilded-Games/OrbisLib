package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.OrbisAPICapabilities;
import com.gildedgames.orbis_api.preparation.IPrepManager;
import com.gildedgames.orbis_api.preparation.IPrepRegistryEntry;
import com.gildedgames.orbis_api.preparation.impl.util.PrepHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PrepTasks
{

	/**
	 * The chunk radius at which the manager searches around players to mark
	 * sectors as active.
	 */
	private static int CHUNK_RADIUS_SEARCHING = 180;

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
			PrepHelper.getManager(world).access().provideSector(centerSectorX, centerSectorY);

			int minSectorX = Math.floorDiv(minChunkX, entry.getSectorChunkArea());
			int minSectorY = Math.floorDiv(minChunkY, entry.getSectorChunkArea());

			int maxSectorX = Math.floorDiv(maxChunkX, entry.getSectorChunkArea());
			int maxSectorY = Math.floorDiv(maxChunkY, entry.getSectorChunkArea());

			for (int x = minSectorX; x < maxSectorX; x++)
			{
				for (int y = minSectorY; y < maxSectorY; y++)
				{
					PrepHelper.getManager(world).access().provideSector(centerSectorX, centerSectorY);
				}
			}
		}
	}

}
