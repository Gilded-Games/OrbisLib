package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.OrbisAPICapabilities;
import com.gildedgames.orbis_api.preparation.IPrepManager;
import com.gildedgames.orbis_api.preparation.impl.util.PrepHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PrepTasks
{
	@SubscribeEvent
	public static void onChunkLoaded(final ChunkEvent.Load event)
	{
		final World world = event.getWorld();

		if (world.hasCapability(OrbisAPICapabilities.PREP_MANAGER, null))
		{
			IPrepManager manager = PrepHelper.getManager(world);

			if (manager != null)
			{
				manager.getAccess().onChunkLoaded(event.getChunk().x, event.getChunk().z);
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
				manager.getAccess().onChunkUnloaded(event.getChunk().x, event.getChunk().z);
			}
		}
	}
}
