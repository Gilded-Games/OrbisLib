package com.gildedgames.orbis_api;

import com.gildedgames.orbis_api.preparation.IPrepManager;
import com.gildedgames.orbis_api.world.data.IWorldDataManagerContainer;
import com.gildedgames.orbis_api.world.instances.IPlayerInstances;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class OrbisAPICapabilities
{
	@CapabilityInject(IPlayerInstances.class)
	public static final Capability<IPlayerInstances> PLAYER_INSTANCES = null;

	@CapabilityInject(IPrepManager.class)
	public static final Capability<IPrepManager> PREP_MANAGER = null;

	@CapabilityInject(IWorldDataManagerContainer.class)
	public static final Capability<IWorldDataManagerContainer> WORLD_DATA = null;
}
