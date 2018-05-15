package com.gildedgames.orbis_api.world.data;

import com.gildedgames.orbis_api.OrbisAPI;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldDataManagerContainerEvents
{
	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save event)
	{
		OrbisAPI.services().getWorldDataManager(event.getWorld()).flush();
	}

	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event)
	{
		OrbisAPI.services().getWorldDataManager(event.getWorld()).close();
	}
}
