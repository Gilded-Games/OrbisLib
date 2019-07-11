package com.gildedgames.orbis.lib.world.instances;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.OrbisLibCapabilities;
import com.gildedgames.orbis.lib.util.mc.BlockPosDimension;
import com.gildedgames.orbis.lib.util.mc.NBTHelper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class InstanceManagerImpl implements IInstanceManager
{
	private static final String FILE_PATH = "//data//instances.dat";

	private final MinecraftServer server;

	private final Multimap<IInstanceFactory<? extends IInstance>, IInstance> registeredInstances = HashMultimap.create();

	private final BiMap<IInstance, DimensionType> registeredDimensions = HashBiMap.create();

	private final Set<IInstance> deletionQueue = new HashSet<>();

	public InstanceManagerImpl(MinecraftServer server)
	{
		this.server = server;
	}

	@Override
	public IPlayerInstances getPlayerInstanceData(final PlayerEntity player)
	{
		return player.getCapability(OrbisLibCapabilities.PLAYER_INSTANCES, null)
				.orElseThrow(NullPointerException::new);
	}

	@Override
	public <T extends IInstance> T createInstance(IInstanceFactory<T> factory)
	{
		T instance = factory.createInstance();

		DimensionType type = DimensionManager.registerDimension(factory.getUniqueName(), factory.getDimensionType(), null, instance.getHasSkylight());

		this.registeredInstances.put(factory, instance);
		this.registeredDimensions.put(instance, type);

		OrbisLib.LOGGER.info("Dimension " + type + " was created for " + instance);

		return instance;
	}

	@Override
	public <T extends IInstance> void destroyInstance(IInstanceFactory<T> factory, IInstance instance)
	{
		ServerWorld world = this.getWorldForInstance(instance);

		if (world != null)
		{
			DimensionManager.unloadWorld(world);
		}

		if (instance.isTemporary())
		{
			OrbisLib.LOGGER.info("Queued instance " + instance + " for deletion");

			this.deletionQueue.add(instance);
		}

		OrbisLib.LOGGER.info("Unloaded instance " + instance);

		this.registeredInstances.remove(factory, instance);
		this.registeredDimensions.remove(instance);
	}

	private void loadInstance(IInstanceFactory<? extends IInstance> factory, IInstance instance, int id)
	{
		this.registeredInstances.put(factory, instance);

		OrbisLib.LOGGER.info("Loaded instance " + instance);
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END)
		{
			return;
		}

		for (IInstance instance : this.deletionQueue)
		{
			ServerWorld world = this.getWorldForInstance(instance);

			if (world == null)
			{
				this.deleteInstance(instance, this.getDimensionTypeForInstance(instance));
			}
		}
	}

	private DimensionType getDimensionTypeForInstance(IInstance instance)
	{
		DimensionType type = this.registeredDimensions.get(instance);

		if (type == null)
		{
			throw new IllegalArgumentException(instance + " is not registered to a dimension");
		}

		return type;
	}

	private ServerWorld getWorldForInstance(IInstance instance)
	{
		return DimensionManager.getWorld(this.server, this.getDimensionTypeForInstance(instance), false, false);
	}

	private void deleteInstance(IInstance instance, DimensionType type)
	{
		OrbisLib.LOGGER.info("Dimension " + type + " queued for deletion");

		MinecraftServer server = this.server;
		server.runAsync(() -> {
			File file = type.getDirectory(server.getWorld(DimensionType.field_223227_a_).getSaveHandler().getWorldDirectory());

			if (!file.isDirectory())
			{
				OrbisLib.LOGGER.warn("World save directory '" + file.getAbsolutePath() + "' doesn't exist, cannot delete (this is likely a severe bug)");

				return;
			}

			OrbisLib.LOGGER.info("Dimension with " + type + " is being deleted");

			try
			{
				FileUtils.deleteDirectory(file);
			}
			catch (IOException e)
			{
				OrbisLib.LOGGER.warn("Failed to delete dimension " + type);

				return;
			}

			OrbisLib.LOGGER.info("Deleted dimension " + type + " successfully");

			this.unregisterDimension(instance);
		});
	}

	private void unregisterDimension(IInstance instance)
	{
		DimensionType type = this.getDimensionTypeForInstance(instance);

		DimensionManager.unregisterDimension(type.getId());

		OrbisLib.LOGGER.info("Unregistered dimension " + type + " from instance " + instance);
	}

	@Override
	public void loadAllInstancesFromDisk()
	{
		final CompoundNBT tag = NBTHelper.readNBTFromFile(this.server, FILE_PATH);

		if (tag == null)
		{
			return;
		}

		for (String key : tag.keySet())
		{
			ResourceLocation name = new ResourceLocation(key);

			for (final IInstanceFactory<? extends IInstance> factory : this.registeredInstances.keys())
			{
				if (factory.getUniqueName().equals(name))
				{
					ListNBT list = tag.getList(key, 10);

					for (int i = 0; i < list.size(); i++)
					{
						CompoundNBT nbt = list.getCompound(i);

						IInstance instance = factory.createInstance();
						instance.read(nbt.getCompound("Data"));

						int id = nbt.getInt("Dimension");

						this.loadInstance(factory, instance, id);
					}
				}
			}
		}
	}

	@Override
	public void saveAllInstancesToDisk()
	{
		final CompoundNBT root = new CompoundNBT();

		for (final IInstanceFactory<? extends IInstance> factory : this.registeredInstances.keys())
		{
			ListNBT list = new ListNBT();

			for (IInstance instance : this.registeredInstances.get(factory))
			{
				CompoundNBT data = new CompoundNBT();
				instance.write(data);

				CompoundNBT tag = new CompoundNBT();
				tag.putInt("Dimension", this.getDimensionTypeForInstance(instance).getId());
				tag.put("Data", data);

				list.add(tag);
			}

			root.put(factory.getUniqueName().toString(), list);
		}

		NBTHelper.writeNBTToFile(this.server, root, FILE_PATH);
	}

	@SubscribeEvent
	public void onWorldTick(final TickEvent.WorldTickEvent event)
	{
		IInstance instance = this.registeredDimensions.inverse().get(event.world.getDimension().getType());

		if (instance == null)
		{
			return;
		}

		instance.tick();
	}

	@SubscribeEvent
	public void onWorldSaved(final WorldEvent.Save event)
	{
		this.saveAllInstancesToDisk();
	}

//  TODO: Re-implement
//	@SubscribeEvent
//	public void onClientDisconnect(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
//	{
//		if (FMLCommonHandler.instance().getMinecraftServerInstance() == null)
//		{
//			this.cleanup();
//		}
//	}

	@Override
	public World teleportPlayerToInstance(final IInstance instance, final ServerPlayerEntity player)
	{
		final MinecraftServer server = player.getServer();

		final DimensionType registeredDimension = this.getDimensionTypeForInstance(instance);

		final ServerWorld fromWorld = player.getServerWorld();
		final ServerWorld toWorld = DimensionManager.getWorld(server, registeredDimension, false, true);

		if (toWorld == null)
		{
			return player.getEntityWorld();
		}

		final IPlayerInstances hook = OrbisLib.instances().getPlayerInstanceData(player);

		if (hook.getInstance() != null)
		{
			hook.getInstance().onLeave(player);
			hook.setInstance(null);
		}

		final PlayerList playerList = server.getPlayerList();
		playerList.recreatePlayerEntity(player, player.dimension, true);

		player.timeUntilPortal = player.getPortalCooldown();

		hook.setReturnPosition(new BlockPosDimension((int) player.posX, (int) player.posY, (int) player.posZ, player.dimension));
		hook.setInstance(instance);

		instance.onJoin(player);

		return toWorld;
	}

	@Override
	public void returnPlayerFromInstance(final ServerPlayerEntity player)
	{
		final IPlayerInstances hook = OrbisLib.instances().getPlayerInstanceData(player);

		if (hook.getInstance() != null && hook.getOutside() != null)
		{
			final MinecraftServer server = player.getServer();

			final BlockPosDimension pos = hook.getOutside();

			final ServerWorld fromWorld = player.getServerWorld();
			final ServerWorld toWorld = DimensionManager.getWorld(server, pos.getDimension(), false, false);

			final PlayerList playerList = server.getPlayerList();
			playerList.recreatePlayerEntity(player, player.dimension, true);

			player.timeUntilPortal = player.getPortalCooldown();

			hook.setReturnPosition(null);
			hook.setInstance(null);

			player.connection.setPlayerLocation(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
		}
	}


	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerTravel(PlayerEvent.PlayerChangedDimensionEvent event)
	{
		final PlayerEntity player = event.getPlayer();
		final IPlayerInstances hook = this.getPlayerInstanceData(player);

		if (hook.getInstance() != null)
		{
			hook.getInstance().onLeave(player);
			hook.setInstance(null);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
	{
		PlayerEntity player = event.getPlayer();
		World world = player.getEntityWorld();

		if (world.isRemote() || !world.getDimension().canRespawnHere())
		{
			return;
		}

		IPlayerInstances hook = this.getPlayerInstanceData(event.getPlayer());

		if (hook.getInstance() == null)
		{
			return;
		}

		hook.getInstance().onRespawn(event.getPlayer());
	}
}
