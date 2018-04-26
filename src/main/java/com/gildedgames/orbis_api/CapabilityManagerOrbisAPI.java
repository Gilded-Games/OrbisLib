package com.gildedgames.orbis_api;

import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import com.gildedgames.orbis_api.preparation.IPrepManagerPool;
import com.gildedgames.orbis_api.preparation.impl.capability.PrepChunkManager;
import com.gildedgames.orbis_api.preparation.impl.capability.PrepChunkManagerStorageProvider;
import com.gildedgames.orbis_api.preparation.impl.capability.PrepManagerPool;
import com.gildedgames.orbis_api.preparation.impl.capability.PrepManagerPoolStorageProvider;
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
		CapabilityManager.INSTANCE.register(IPrepManagerPool.class, new PrepManagerPool.Storage(), PrepManagerPool::new);
		CapabilityManager.INSTANCE.register(IPrepChunkManager.class, new PrepChunkManager.Storage(), PrepChunkManager::new);
	}

	@SubscribeEvent
	public static void onWorldAttachCapability(final AttachCapabilitiesEvent<World> event)
	{
		final World world = event.getObject();

		event.addCapability(OrbisAPI.getResource("PrepChunkManager"), new PrepChunkManagerStorageProvider(world));
		event.addCapability(OrbisAPI.getResource("PrepManagerPool"), new PrepManagerPoolStorageProvider(world));
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
