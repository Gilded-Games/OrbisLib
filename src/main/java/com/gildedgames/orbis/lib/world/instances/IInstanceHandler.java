package com.gildedgames.orbis.lib.world.instances;

import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.Collection;

public interface IInstanceHandler<T extends IInstance> extends NBT
{
	T createNew();

	void unloadAllInstances();

	void registerInstance(T instance);

	void unregisterInstance(T instance);

	/**
	 * Returns an existing instance for the specified dimension ID.
	 * @param dimensionId The dimension ID
	 * @return The instance for the dimension ID that belongs to this handler, or null
	 */
	T getInstanceForDimension(int dimensionId);

	Collection<T> getLoadedInstances();

	World teleportPlayerToInstance(T instance, EntityPlayerMP player);

	void returnPlayerFromInstance(EntityPlayerMP player);
}
