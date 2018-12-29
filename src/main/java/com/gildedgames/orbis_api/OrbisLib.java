package com.gildedgames.orbis_api;

import com.gildedgames.orbis_api.client.PartialTicks;
import com.gildedgames.orbis_api.network.INetworkMultipleParts;
import com.gildedgames.orbis_api.preparation.IPrepRegistry;
import com.gildedgames.orbis_api.preparation.impl.PrepTasks;
import com.gildedgames.orbis_api.world.WorldObjectManagerEvents;
import com.gildedgames.orbis_api.world.data.WorldDataManagerContainerEvents;
import com.gildedgames.orbis_api.world.instances.IInstanceRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * This OrbisLib allows mod developers to integrate
 * the projects they've developed with the Orbis tool
 * into their mod designs. It's recommended you create an
 * IOrbisDefinitionRegistry and register it in this API's services.
 */
@Mod(name = OrbisLib.MOD_NAME, modid = OrbisLib.MOD_ID, version = OrbisLib.MOD_VERSION, certificateFingerprint = OrbisLib.MOD_FINGERPRINT)
@Mod.EventBusSubscriber
public class OrbisLib
{
	public static final String MOD_NAME = "OrbisLib";

	public static final String MOD_ID = "orbislib";

	public static final String MOD_VERSION = "1.12.2-1.1.12";

	public static final String MOD_FINGERPRINT = "db341c083b1b8ce9160a769b569ef6737b3f4cdf";

	public static final Logger LOGGER = LogManager.getLogger("OrbisLib");

	@Mod.Instance(OrbisLib.MOD_ID)
	public static OrbisLib INSTANCE;

	@SidedProxy(clientSide = "com.gildedgames.orbis_api.client.ClientProxy", serverSide = "com.gildedgames.orbis_api.CommonProxy")
	public static CommonProxy PROXY;

	private static IOrbisLibServices services;

	private static boolean loadedInstances;

	public static boolean isClient()
	{
		return FMLCommonHandler.instance().getSide().isClient();
	}

	public static boolean isServer()
	{
		return FMLCommonHandler.instance().getSide().isServer();
	}

	public static IOrbisLibServices services()
	{
		if (OrbisLib.services == null)
		{
			OrbisLib.services = new OrbisLibServices();

			MinecraftForge.EVENT_BUS.register(OrbisLib.services().instances());

			MinecraftForge.EVENT_BUS.register(WorldObjectManagerEvents.class);
			MinecraftForge.EVENT_BUS.register(WorldDataManagerContainerEvents.class);
			MinecraftForge.EVENT_BUS.register(PartialTicks.class);

			MinecraftForge.EVENT_BUS.register(PrepTasks.class);
		}

		return OrbisLib.services;
	}

	public static IPrepRegistry sectors()
	{
		return OrbisLib.services().sectors();
	}

	public static INetworkMultipleParts network()
	{
		return OrbisLib.services().network();
	}

	public static IInstanceRegistry instances()
	{
		return OrbisLib.services().instances();
	}

	public static File getWorldDirectory()
	{
		return DimensionManager.getCurrentSaveRootDirectory();
	}

	public static ResourceLocation getResource(final String name)
	{
		return new ResourceLocation(OrbisLib.MOD_ID, name);
	}

	public static String getResourcePath(final String name)
	{
		return (OrbisLib.MOD_ID + ":") + name;
	}

	@Mod.EventHandler
	public void onFMLInit(final FMLInitializationEvent event)
	{
		CapabilityManagerOrbisAPI.init();
	}

	@Mod.EventHandler
	public void onServerStopping(final FMLServerStoppingEvent event)
	{
		OrbisLib.instances().saveAllInstancesToDisk();

		loadedInstances = false;
	}

	@Mod.EventHandler
	public void onServerStopped(final FMLServerStoppedEvent event)
	{
		OrbisLib.instances().cleanup();
	}

	@Mod.EventHandler
	public void serverStarted(final FMLServerStartedEvent event)
	{
		if (!loadedInstances)
		{
			instances().loadAllInstancesFromDisk();

			loadedInstances = true;
		}
	}

}
