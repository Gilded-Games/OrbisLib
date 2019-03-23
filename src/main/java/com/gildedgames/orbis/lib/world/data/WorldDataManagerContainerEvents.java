package com.gildedgames.orbis.lib.world.data;

import com.gildedgames.orbis.lib.OrbisLib;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldDataManagerContainerEvents
{
	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save event)
	{
		OrbisLib.services().getWorldDataManager(event.getWorld()).flush();
	}

	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event)
	{
		OrbisLib.services().getWorldDataManager(event.getWorld()).close();
	}
}
