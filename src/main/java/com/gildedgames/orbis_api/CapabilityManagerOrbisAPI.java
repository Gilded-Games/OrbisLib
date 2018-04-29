package com.gildedgames.orbis_api;

import com.gildedgames.orbis_api.preparation.IPrepManager;
import com.gildedgames.orbis_api.preparation.IPrepRegistryEntry;
import com.gildedgames.orbis_api.preparation.impl.capability.PrepManager;
import com.gildedgames.orbis_api.preparation.impl.capability.PrepManagerStorageProvider;
import com.gildedgames.orbis_api.world.instances.IPlayerInstances;
import com.gildedgames.orbis_api.world.instances.PlayerInstances;
import com.gildedgames.orbis_api.world.instances.PlayerInstancesProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;
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
		CapabilityManager.INSTANCE.register(IPrepManager.class, new PrepManager.Storage(), PrepManager::new);
	}

	@SubscribeEvent
	public static void onWorldAttachCapability(final AttachCapabilitiesEvent<World> event)
	{
		final World world = event.getObject();

		for (IPrepRegistryEntry entry : OrbisAPI.sectors().getEntries())
		{
			if (entry.shouldAttachTo(world))
			{
				event.addCapability(OrbisAPI.getResource("PrepManagerPool"), new PrepManagerStorageProvider(world, entry));

				break;
			}
		}
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
