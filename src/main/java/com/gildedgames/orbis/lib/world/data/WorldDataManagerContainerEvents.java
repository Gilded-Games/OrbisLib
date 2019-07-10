package com.gildedgames.orbis.lib.world.data;

import com.gildedgames.orbis.lib.OrbisLib;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldDataManagerContainerEvents
{
	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save event)
	{
		if (!event.getWorld().isRemote)
		{
			OrbisLib.services().getWorldDataManager(event.getWorld()).flush();
		}
	}

	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event)
	{
		if (!event.getWorld().isRemote)
		{
			OrbisLib.services().getWorldDataManager(event.getWorld()).close();
		}
	}
}
