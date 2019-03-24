package com.gildedgames.orbis.lib.world.instances;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public interface IInstanceManager
{
	IPlayerInstances getPlayerInstanceData(EntityPlayer player);

	<T extends IInstance> T createInstance(IInstanceFactory<T> factory);

	<T extends IInstance> void destroyInstance(IInstanceFactory<T> factory, IInstance instance);

	void saveAllInstancesToDisk();

	void loadAllInstancesFromDisk();

	World teleportPlayerToInstance(IInstance instance, EntityPlayerMP player);

	void returnPlayerFromInstance(EntityPlayerMP player);
}
