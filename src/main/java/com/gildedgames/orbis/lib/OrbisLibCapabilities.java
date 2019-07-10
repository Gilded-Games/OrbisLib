package com.gildedgames.orbis.lib;

import com.gildedgames.orbis.lib.world.data.IWorldDataManagerContainer;
import com.gildedgames.orbis.lib.world.instances.IPlayerInstances;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class OrbisLibCapabilities
{
	@CapabilityInject(IPlayerInstances.class)
	public static final Capability<IPlayerInstances> PLAYER_INSTANCES = null;

	@CapabilityInject(IWorldDataManagerContainer.class)
	public static final Capability<IWorldDataManagerContainer> WORLD_DATA = null;
}
