package com.gildedgames.orbis.lib.preparation.impl;

import com.gildedgames.orbis.lib.preparation.IPrepManager;
import com.gildedgames.orbis.lib.preparation.impl.util.PrepHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PrepTasks
{
	@SubscribeEvent
	public static void onChunkLoaded(final ChunkEvent.Load event)
	{
		final World world = event.getWorld();

		IPrepManager manager = PrepHelper.getManager(world);

		if (manager != null)
		{
			manager.getAccess().onChunkLoaded(event.getChunk().x, event.getChunk().z);
		}
	}

	@SubscribeEvent
	public static void onChunkUnloaded(final ChunkEvent.Unload event)
	{
		final World world = event.getWorld();

		IPrepManager manager = PrepHelper.getManager(world);

		if (manager != null)
		{
			manager.getAccess().onChunkUnloaded(event.getChunk().x, event.getChunk().z);
		}
	}

	@SubscribeEvent
	public static void onWorldTick(final TickEvent.WorldTickEvent event)
	{
		final World world = event.world;

		IPrepManager manager = PrepHelper.getManager(world);

		if (manager != null)
		{
			manager.getAccess().update();
		}
	}
}
