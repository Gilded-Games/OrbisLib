package com.gildedgames.orbis.lib;

import com.gildedgames.orbis.lib.preparation.IPrepRegistry;
import com.gildedgames.orbis.lib.preparation.impl.PrepTasks;
import com.gildedgames.orbis.lib.world.WorldObjectManagerEvents;
import com.gildedgames.orbis.lib.world.data.WorldDataManagerContainerEvents;
import com.gildedgames.orbis.lib.world.instances.IInstanceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This OrbisLib allows mod developers to integrate
 * the projects they've developed with the Orbis tool
 * into their mod designs. It's recommended you create an
 * IOrbisDefinitionRegistry and register it in this API's services.
 */
@Mod(OrbisLib.MOD_ID)
@Mod.EventBusSubscriber
public class OrbisLib
{
	public static final String MOD_ID = "orbis-lib";

	public static final Logger LOGGER = LogManager.getLogger("OrbisLib");

	private static OrbisServices services;

	public OrbisLib()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void setup(FMLCommonSetupEvent e)
	{
		CapabilityManagerOrbisLib.init();

		OrbisLib.services = new OrbisServices();

		MinecraftForge.EVENT_BUS.register(WorldObjectManagerEvents.class);
		MinecraftForge.EVENT_BUS.register(WorldDataManagerContainerEvents.class);

		MinecraftForge.EVENT_BUS.register(PrepTasks.class);
	}

	public static IOrbisServices services()
	{
		return OrbisLib.services;
	}

	public static IPrepRegistry sectors()
	{
		return OrbisLib.services().sectors();
	}

	public static IInstanceManager instances()
	{
		return OrbisLib.services().instances();
	}

	public static ResourceLocation getResource(final String name)
	{
		return new ResourceLocation(OrbisLib.MOD_ID, name);
	}

	public static String getPath(final String name)
	{
		return (OrbisLib.MOD_ID + ":") + name;
	}

	@SubscribeEvent
	public void onServerStopping(final FMLServerStoppingEvent event)
	{
		OrbisLib.instances().saveAllInstancesToDisk();
	}

	@SubscribeEvent
	public void serverStarting(final FMLServerStartingEvent event)
	{
		services.init(event.getServer());
	}

	@SubscribeEvent
	public void serverStopping(final FMLServerStoppingEvent event)
	{
		services.shutdown(event.getServer());
	}
}
