package com.gildedgames.orbis.lib.client;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

public class PartialTicks
{
	private static float partialTicks;

	public static float get()
	{
		return partialTicks;
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onClientTick(TickEvent.RenderTickEvent event)
	{
		partialTicks = event.renderTickTime;
	}
}
