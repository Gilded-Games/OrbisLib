package com.gildedgames.orbis_api.world.instances;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.util.TeleporterGeneric;
import com.gildedgames.orbis_api.util.mc.BlockPosDimension;
import com.gildedgames.orbis_api.util.mc.NBTHelper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class InstanceHandler<T extends IInstance> implements IInstanceHandler<T>
{

	private final BiMap<Integer, T> instances = HashBiMap.create();

	private final IInstanceFactory<T> factory;

	public InstanceHandler(final IInstanceFactory<T> factory)
	{
		this.factory = factory;
	}

	@Override
	public T createNew()
	{
		final T instance = this.factory.createInstance(DimensionManager.getNextFreeDimId(), this);

		this.registerInstance(instance);

		return instance;
	}

	@Override
	public void unloadAllInstances()
	{
		for (IInstance instance : this.instances.values())
		{
			OrbisAPI.instances().unloadInstance(instance);
		}

		this.instances.clear();
	}

	@Override
	public void registerInstance(T instance)
	{
		OrbisAPI.instances().loadInstance(instance);

		this.instances.put(instance.getDimensionId(), instance);
	}

	@Override
	public void unregisterInstance(T instance)
	{
		if (instance.isTemporary() && instance.getPlayers().isEmpty())
		{
			OrbisAPI.instances().unloadInstance(instance);

			this.instances.remove(instance.getDimensionId());
		}
	}

	@Override
	public void write(final NBTTagCompound output)
	{
		output.setBoolean("hasWrittenInstances", this.instances.size() > 0);

		final NBTTagList tagList = new NBTTagList();

		for (final Map.Entry<Integer, T> entry : this.instances.entrySet())
		{
			final T instance = entry.getValue();
			final NBTTagCompound newTag = new NBTTagCompound();
			newTag.setInteger("dimension", entry.getKey());

			instance.write(newTag);
			tagList.appendTag(newTag);
		}

		output.setTag("instances", tagList);
	}

	@Override
	public void read(final NBTTagCompound input)
	{
		final boolean hasWrittenInstances = input.getBoolean("hasWrittenInstances");

		if (!hasWrittenInstances)
		{
			return;
		}

		for (final NBTTagCompound tag : NBTHelper.getIterator(input, "instances"))
		{
			int id = tag.getInteger("dimension");

			final T instance = this.factory.createInstance(id, this);
			instance.read(tag);

			this.registerInstance(instance);
		}
	}

	@Override
	public T getInstanceForDimension(final int dimensionId)
	{
		return this.instances.get(dimensionId);
	}

	@Override
	public Collection<T> getLoadedInstances()
	{
		return Collections.unmodifiableCollection(this.instances.values());
	}

	@Override
	public World teleportPlayerToInstance(final T instance, final EntityPlayerMP player)
	{
		if (this.instances.containsValue(instance))
		{
			final IPlayerInstances hook = OrbisAPI.instances().getPlayer(player);

			if (hook.getInstance() != null)
			{
				hook.getInstance().onLeave(player);
			}

			hook.setReturnPosition(new BlockPosDimension((int) player.posX, (int) player.posY, (int) player.posZ, player.dimension));

			final int dimId = instance.getDimensionId();

			final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

			final WorldServer world = server.getWorld(dimId);

			final Teleporter teleporter = this.factory.getTeleporter(world);

			final PlayerList playerList = server.getPlayerList();
			playerList.transferPlayerToDimension(player, dimId, teleporter);

			player.timeUntilPortal = player.getPortalCooldown();

			hook.setInstance(instance);

			instance.onJoin(player);

			return world;
		}

		return player.world;
	}

	@Override
	public void returnPlayerFromInstance(final EntityPlayerMP player)
	{
		final IPlayerInstances hook = OrbisAPI.instances().getPlayer(player);

		if (hook.getInstance() != null && hook.getOutside() != null)
		{
			int id = hook.getInstance().getDimensionId();

			final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

			final BlockPosDimension pos = hook.getOutside();
			final Teleporter teleporter = new TeleporterGeneric(server.getWorld(player.dimension));
			final PlayerList playerList = server.getPlayerList();
			playerList.transferPlayerToDimension(player, pos.getDim(), teleporter);
			player.timeUntilPortal = player.getPortalCooldown();
			hook.setReturnPosition(null);
			hook.setInstance(null);

			player.connection.setPlayerLocation(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
			}
	}
}
