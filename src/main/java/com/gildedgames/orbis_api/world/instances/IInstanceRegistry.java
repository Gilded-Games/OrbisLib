package com.gildedgames.orbis_api.world.instances;

import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IInstanceRegistry
{
	List<IInstanceHandler> getInstanceHandlers();

	Collection<IInstance> getInstances();

	<T extends IInstance> IInstanceHandler<T> createInstanceHandler(IInstanceFactory<T> factory);

	IPlayerInstances getPlayer(EntityPlayer player);

	IPlayerInstances getPlayer(UUID uuid);

	void loadInstance(IInstance instance);

	void unloadInstance(IInstance instance);

	void saveAllInstancesToDisk();

	void loadAllInstancesFromDisk();

	/**
	 * Unregisters all dimensions.
	 */
	void cleanup();
}
