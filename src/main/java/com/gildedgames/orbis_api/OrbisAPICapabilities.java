package com.gildedgames.orbis_api;

import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import com.gildedgames.orbis_api.preparation.IPrepManagerPool;
import com.gildedgames.orbis_api.world.instances.IPlayerInstances;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class OrbisAPICapabilities
{
	@CapabilityInject(IPlayerInstances.class)
	public static final Capability<IPlayerInstances> PLAYER_INSTANCES = null;

	@CapabilityInject(IPrepManagerPool.class)
	public static final Capability<IPrepManagerPool> PREP_MANAGER_POOL = null;

	@CapabilityInject(IPrepChunkManager.class)
	public static final Capability<IPrepChunkManager> PREP_CHUNK_MANAGER = null;
}
