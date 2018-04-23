package com.gildedgames.orbis_api.client;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PartialTicks
{
	private static float partialTicks;

	public static float get()
	{
		return partialTicks;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onClientTick(TickEvent.RenderTickEvent event)
	{
		partialTicks = event.renderTickTime;
	}
}
