package com.gildedgames.orbis_api;

import com.gildedgames.orbis_api.world.instances.IPlayerInstances;
import com.gildedgames.orbis_api.world.instances.PlayerInstances;
import com.gildedgames.orbis_api.world.instances.PlayerInstancesProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber()
public class CapabilityManagerOrbisAPI
{
	public static void init()
	{
		CapabilityManager.INSTANCE.register(IPlayerInstances.class, new PlayerInstances.Storage(), PlayerInstances::new);
	}

	@SubscribeEvent
	public static void onEntityLoad(final AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject() == null)
		{
			return;
		}

		if (event.getObject() instanceof EntityPlayer)
		{
			event.addCapability(OrbisAPI.getResource("PlayerInstances"), new PlayerInstancesProvider((EntityPlayer) event.getObject()));
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(final PlayerEvent.Clone event)
	{
		final IPlayerInstances oldPlayer = OrbisAPI.instances().getPlayer(event.getOriginal());

		if (oldPlayer != null)
		{
			final IPlayerInstances newPlayer = OrbisAPI.instances().getPlayer((EntityPlayer) event.getEntity());

			final Capability.IStorage<IPlayerInstances> storage = OrbisAPICapabilities.PLAYER_INSTANCES.getStorage();

			final NBTBase state = storage.writeNBT(OrbisAPICapabilities.PLAYER_INSTANCES, oldPlayer, null);

			storage.readNBT(OrbisAPICapabilities.PLAYER_INSTANCES, newPlayer, null, state);
		}
	}
}
