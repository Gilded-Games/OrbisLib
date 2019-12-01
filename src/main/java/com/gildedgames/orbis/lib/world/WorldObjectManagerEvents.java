package com.gildedgames.orbis.lib.world;

import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
