package com.gildedgames.orbis_api.world;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class WorldObjectManagerEvents
{
	@SubscribeEvent
	public static void onWorldTick(final TickEvent.WorldTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END)
		{
			final World world = event.world;

			if (!world.isRemote)
			{
				final WorldObjectManager manager = WorldObjectManager.get(world);

				manager.updateObjects();
				manager.checkForDirtyObjects();
			}
		}
	}

}
