package com.gildedgames.orbis_api.world.instances;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.OrbisAPICapabilities;
import com.gildedgames.orbis_api.network.instances.PacketRegisterDimension;
import com.gildedgames.orbis_api.network.instances.PacketUnregisterDimension;
import com.gildedgames.orbis_api.util.mc.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class InstanceRegistryImpl implements IInstanceRegistry
{
	private static final String FILE_PATH = "//data//instances.dat";

	private List<IInstanceHandler> registeredHandlers = new ArrayList<>();

	private HashSet<IInstance> instances = new HashSet<>();

	private HashSet<IInstance> deleteQueue = new HashSet<>();

	@Override
	public List<IInstanceHandler> getInstanceHandlers()
	{
		return Collections.unmodifiableList(this.registeredHandlers);
	}

	@Override
	public Collection<IInstance> getInstances()
	{
		return Collections.unmodifiableCollection(this.instances);
	}

	@Override
	public <T extends IInstance> InstanceHandler<T> createInstanceHandler(final IInstanceFactory<T> factory)
	{
		final InstanceHandler<T> handler = new InstanceHandler<>(factory);

		this.registeredHandlers.add(handler);

		return handler;
	}

	@Override
	public IPlayerInstances getPlayer(final EntityPlayer player)
	{
		return player.getCapability(OrbisAPICapabilities.PLAYER_INSTANCES, null);
	}

	@Override
	public IPlayerInstances getPlayer(final UUID uuid)
	{
		final EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid);

		if (player == null)
		{
			return null;
		}

		return this.getPlayer(player);
	}

	@Override
	public void loadInstance(IInstance instance)
	{
		int id = instance.getDimensionId();
		DimensionType type = instance.getDimensionType();

		if (DimensionManager.isDimensionRegistered(id))
		{
			throw new IllegalStateException("Tried to register DIM" + id + ", but it's already registered");
		}

		DimensionManager.registerDimension(id, type);

		OrbisAPI.LOGGER.info("DimensionType " + type.getName() + " registered (ID: " + id + ") to instance registry");

		if (OrbisAPI.isServer())
		{
			OrbisAPI.network().sendPacketToAllPlayers(new PacketRegisterDimension(instance.getDimensionType(), instance.getDimensionId()));
		}

		this.instances.add(instance);
	}

	@Override
	public void unloadInstance(IInstance instance)
	{
		int id = instance.getDimensionId();

		if (!DimensionManager.isDimensionRegistered(id))
		{
			throw new IllegalStateException("Tried to unload DIM" + id + ", but it isn't registered");
		}

		DimensionManager.unloadWorld(id);

		if (instance.isTemporary())
		{
			this.deleteQueue.add(instance);

			OrbisAPI.LOGGER.info("Dimension ID " + id + " queued to unload");
		}
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END)
		{
			return;
		}

		Collection<IInstance> unloaded = this.deleteQueue.stream()
				.filter((instance) -> DimensionManager.getWorld(instance.getDimensionId()) == null)
				.collect(Collectors.toList());

		for (IInstance instance : unloaded)
		{
			if (instance.getPlayers().isEmpty())
			{
				this.deleteDimension(instance);
			}

			this.deleteQueue.remove(instance);
		}
	}

	private void deleteDimension(IInstance instance)
	{
		int id = instance.getDimensionId();

		OrbisAPI.LOGGER.info("Dimension with ID " + id + " queued for deletion");

		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		server.addScheduledTask(() -> {
			File file = new File(server.getEntityWorld().getSaveHandler().getWorldDirectory(), "DIM" + id);

			if (!file.isDirectory())
			{
				OrbisAPI.LOGGER.warn("World save directory '" + file.getAbsolutePath() + "' doesn't exist, cannot delete (this is likely a severe bug)");

				return;
			}

			OrbisAPI.LOGGER.info("Dimension with ID " + id + " is being deleted");

			try
			{
				FileUtils.deleteDirectory(file);
			}
			catch (IOException e)
			{
				OrbisAPI.LOGGER.warn("Failed to cleanup dimension ID " + id);

				return;
			}

			OrbisAPI.LOGGER.info("Deleted dimension with ID " + id + ", unregistering...");

			this.unregisterInstance(instance);
		});
	}

	private void unregisterInstance(IInstance instance)
	{
		int id = instance.getDimensionId();

		DimensionManager.unregisterDimension(id);

		if (OrbisAPI.isServer())
		{
			OrbisAPI.network().sendPacketToAllPlayers(new PacketUnregisterDimension(instance.getDimensionId()));
		}

		this.instances.remove(instance);
		this.deleteQueue.remove(instance);
	}

	@Override
	public void loadAllInstancesFromDisk()
	{
		final NBTTagCompound tag = NBTHelper.readNBTFromFile(FILE_PATH);

		if (tag == null)
		{
			return;
		}

		int i = 0;

		for (final IInstanceHandler<?> handler : this.getInstanceHandlers())
		{
			final NBTTagCompound subTag = tag.getCompoundTag(String.valueOf(i++));

			handler.read(subTag);
		}
	}

	@Override
	public void cleanup()
	{
		this.registeredHandlers.forEach(IInstanceHandler::unloadAllInstances);

		for (IInstance instance : this.instances)
		{
			DimensionManager.unregisterDimension(instance.getDimensionId());

			if (OrbisAPI.isServer())
			{
				OrbisAPI.network().sendPacketToAllPlayers(new PacketUnregisterDimension(instance.getDimensionId()));
			}
		}

		this.instances.clear();
		this.deleteQueue.clear();
	}

	@Override
	public void saveAllInstancesToDisk()
	{
		final NBTTagCompound tag = new NBTTagCompound();

		int i = 0;

		tag.setInteger("size", OrbisAPI.instances().getInstanceHandlers().size());

		for (final IInstanceHandler<?> handler : this.getInstanceHandlers())
		{
			final NBTTagCompound subTag = new NBTTagCompound();
			handler.write(subTag);

			tag.setTag(String.valueOf(i++), subTag);
		}

		NBTHelper.writeNBTToFile(tag, FILE_PATH);
	}

	@SubscribeEvent
	public void onWorldSaved(final WorldEvent.Save event)
	{
		this.saveAllInstancesToDisk();
	}

	@SubscribeEvent
	public void onClientJoinedServer(final PlayerEvent.PlayerLoggedInEvent event)
	{
		for (IInstance instance : this.instances)
		{
			OrbisAPI.network().sendPacketToPlayer(new PacketRegisterDimension(instance.getDimensionType(), instance.getDimensionId()),
					(EntityPlayerMP) event.player);
		}
	}

	@SubscribeEvent
	public void onClientDisconnect(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
	{
		if (FMLCommonHandler.instance().getMinecraftServerInstance() == null)
		{
			this.cleanup();
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerTravel(PlayerEvent.PlayerChangedDimensionEvent event)
	{
		final IPlayerInstances hook = this.getPlayer(event.player);

		if (hook.getInstance() != null)
		{
			hook.getInstance().onLeave(event.player);
			hook.setInstance(null);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
	{
		if (event.player.world.isRemote || !event.player.world.provider.canRespawnHere())
		{
			return;
		}

		IPlayerInstances hook = this.getPlayer(event.player);

		if (hook.getInstance() == null)
		{
			return;
		}

		hook.getInstance().onRespawn(event.player);
	}
}
