package com.gildedgames.orbis.lib;

import com.gildedgames.orbis.lib.preparation.IPrepManager;
import com.gildedgames.orbis.lib.world.data.IWorldDataManagerContainer;
import com.gildedgames.orbis.lib.world.instances.IPlayerInstances;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class OrbisLibCapabilities
{
	@CapabilityInject(IPlayerInstances.class)
	public static final Capability<IPlayerInstances> PLAYER_INSTANCES = null;

	@CapabilityInject(IPrepManager.class)
	public static final Capability<IPrepManager> PREP_MANAGER = null;

	@CapabilityInject(IWorldDataManagerContainer.class)
	public static final Capability<IWorldDataManagerContainer> WORLD_DATA = null;
}
