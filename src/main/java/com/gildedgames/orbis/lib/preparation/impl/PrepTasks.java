package com.gildedgames.orbis.lib.preparation.impl;

import com.gildedgames.orbis.lib.preparation.impl.util.PrepHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PrepTasks
{
	@SubscribeEvent
	public static void onChunkLoaded(final ChunkEvent.Load event)
	{
		final IWorld world = event.getWorld();
		final ChunkPos pos = event.getChunk().getPos();

		PrepHelper.getManager(world).ifPresent((manager) -> manager.getAccess().onChunkLoaded(pos.x, pos.z));
	}

	@SubscribeEvent
	public static void onChunkUnloaded(final ChunkEvent.Unload event)
	{
		final IWorld world = event.getWorld();
		final ChunkPos pos = event.getChunk().getPos();

		PrepHelper.getManager(world).ifPresent((manager) -> manager.getAccess().onChunkUnloaded(pos.x, pos.z));
	}

	@SubscribeEvent
	public static void onWorldTick(final TickEvent.WorldTickEvent event)
	{
		final IWorld world = event.world;

		PrepHelper.getManager(world).ifPresent((manager) -> manager.getAccess().update());
	}
}
