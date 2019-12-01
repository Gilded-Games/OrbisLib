package com.gildedgames.orbis.lib;

import com.gildedgames.orbis.lib.preparation.IPrepManager;
import com.gildedgames.orbis.lib.preparation.IPrepRegistryEntry;
import com.gildedgames.orbis.lib.preparation.impl.capability.PrepManager;
import com.gildedgames.orbis.lib.preparation.impl.capability.PrepManagerStorageProvider;
import com.gildedgames.orbis.lib.world.data.IWorldDataManagerContainer;
import com.gildedgames.orbis.lib.world.data.WorldDataManagerContainer;
import com.gildedgames.orbis.lib.world.data.WorldDataManagerContainerProvider;
import com.gildedgames.orbis.lib.world.instances.IPlayerInstances;
import com.gildedgames.orbis.lib.world.instances.PlayerInstances;
import com.gildedgames.orbis.lib.world.instances.PlayerInstancesProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CapabilityManagerOrbisLib
{
	public static void init()
	{
		CapabilityManager.INSTANCE.register(IPlayerInstances.class, new PlayerInstances.Storage(), PlayerInstances::new);
		CapabilityManager.INSTANCE.register(IPrepManager.class, new PrepManager.Storage(), PrepManager::new);
		CapabilityManager.INSTANCE.register(IWorldDataManagerContainer.class, new WorldDataManagerContainer.Storage(), WorldDataManagerContainer::new);
	}

	@SubscribeEvent
	public static void onWorldAttachCapability(final AttachCapabilitiesEvent<World> event)
	{
		final World world = event.getObject();

		if (!event.getObject().isRemote())
		{
			event.addCapability(OrbisLib.getResource("world_data"), new WorldDataManagerContainerProvider((ServerWorld) event.getObject()));
		}

		for (IPrepRegistryEntry entry : OrbisLib.sectors().getEntries())
		{
			if (entry.shouldAttachTo(world))
			{
				event.addCapability(OrbisLib.getResource("prep_manager_storage"), new PrepManagerStorageProvider(world, entry));

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

		if (event.getObject() instanceof PlayerEntity)
		{
			event.addCapability(OrbisLib.getResource("player_instances"), new PlayerInstancesProvider());
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(final PlayerEvent.Clone event)
	{
		final IPlayerInstances oldPlayer = OrbisLib.instances().getPlayerInstanceData(event.getOriginal());

		if (oldPlayer != null)
		{
			final IPlayerInstances newPlayer = OrbisLib.instances().getPlayerInstanceData((PlayerEntity) event.getEntity());

			final Capability.IStorage<IPlayerInstances> storage = OrbisLibCapabilities.PLAYER_INSTANCES.getStorage();

			final INBT state = storage.writeNBT(OrbisLibCapabilities.PLAYER_INSTANCES, oldPlayer, null);

			storage.readNBT(OrbisLibCapabilities.PLAYER_INSTANCES, newPlayer, null, state);
		}
	}
}
