package com.gildedgames.orbis_api.world.data;

import com.gildedgames.orbis_api.OrbisLib;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
