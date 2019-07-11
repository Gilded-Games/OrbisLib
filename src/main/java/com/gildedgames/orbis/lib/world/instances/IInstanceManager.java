package com.gildedgames.orbis.lib.world.instances;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

public interface IInstanceManager
{
	IPlayerInstances getPlayerInstanceData(PlayerEntity player);

	<T extends IInstance> T createInstance(IInstanceFactory<T> factory);

	<T extends IInstance> void destroyInstance(IInstanceFactory<T> factory, IInstance instance);

	void saveAllInstancesToDisk();

	void loadAllInstancesFromDisk();

	World teleportPlayerToInstance(IInstance instance, ServerPlayerEntity player);

	void returnPlayerFromInstance(ServerPlayerEntity player);
}
