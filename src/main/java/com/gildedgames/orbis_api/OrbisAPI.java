package com.gildedgames.orbis_api;

import com.gildedgames.orbis_api.client.PartialTicks;
import com.gildedgames.orbis_api.network.INetworkMultipleParts;
import com.gildedgames.orbis_api.preparation.IPrepRegistry;
import com.gildedgames.orbis_api.preparation.impl.PrepTasks;
import com.gildedgames.orbis_api.world.WorldObjectManagerEvents;
import com.gildedgames.orbis_api.world.instances.IInstanceRegistry;
import com.gildedgames.orbis_api.world.instances.InstanceEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * This OrbisAPI allows mod developers to integrate
 * the projects they've developed with the Orbis tool
 * into their mod designs. It's recommended you create an
 * IOrbisDefinitionRegistry and register it in this API's services.
 */
@Mod(name = OrbisAPI.MOD_NAME, modid = OrbisAPI.MOD_ID, version = OrbisAPI.MOD_VERSION)
@Mod.EventBusSubscriber
public class OrbisAPI
{
	public static final String MOD_NAME = "Orbis API";

	public static final String MOD_ID = "orbis_api";

	public static final String MOD_VERSION = "1.12.2-1.0.0";

	public static final Logger LOGGER = LogManager.getLogger("OrbisAPI");

	@Mod.Instance(OrbisAPI.MOD_ID)
	public static OrbisAPI INSTANCE;

	private static IOrbisServices services;

	private static boolean loadedInstances;

	public static boolean isClient()
	{
		return FMLCommonHandler.instance().getSide().isClient();
	}

	public static boolean isServer()
	{
		return FMLCommonHandler.instance().getSide().isServer();
	}

	public static IOrbisServices services()
	{
		if (OrbisAPI.services == null)
		{
			OrbisAPI.services = new OrbisServices();

			MinecraftForge.EVENT_BUS.register(InstanceEvents.class);
			MinecraftForge.EVENT_BUS.register(WorldObjectManagerEvents.class);
			MinecraftForge.EVENT_BUS.register(PartialTicks.class);

			MinecraftForge.EVENT_BUS.register(PrepTasks.class);
		}

		return OrbisAPI.services;
	}

	public static IPrepRegistry sectors()
	{
		return OrbisAPI.services().sectors();
	}

	public static INetworkMultipleParts network()
	{
		return OrbisAPI.services().network();
	}

	public static IInstanceRegistry instances()
	{
		return OrbisAPI.services().instances();
	}

	public static File getWorldDirectory()
	{
		return DimensionManager.getCurrentSaveRootDirectory();
	}

	public static ResourceLocation getResource(final String name)
	{
		return new ResourceLocation(OrbisAPI.MOD_ID, name);
	}

	public static String getResourcePath(final String name)
	{
		return (OrbisAPI.MOD_ID + ":") + name;
	}

	@Mod.EventHandler
	public void onFMLInit(final FMLInitializationEvent event)
	{
		CapabilityManagerOrbisAPI.init();
	}

	@Mod.EventHandler
	public void onServerStopping(final FMLServerStoppingEvent event)
	{
		InstanceEvents.saveAllInstancesToDisk();

		loadedInstances = false;
	}

	@Mod.EventHandler
	public void onServerStopped(final FMLServerStoppedEvent event)
	{
		InstanceEvents.unregisterAllInstances();
	}

	@Mod.EventHandler
	public void serverStarted(final FMLServerStartedEvent event)
	{
		if (!loadedInstances)
		{
			InstanceEvents.loadAllInstancesFromDisk();

			loadedInstances = true;
		}
	}

}
