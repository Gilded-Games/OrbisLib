package com.gildedgames.orbis_api;

import com.gildedgames.orbis_api.world.instances.IPlayerInstances;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class OrbisAPICapabilities
{
	@CapabilityInject(IPlayerInstances.class)
	public static final Capability<IPlayerInstances> PLAYER_INSTANCES = null;
}
