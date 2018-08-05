package com.gildedgames.orbis_api;

import com.gildedgames.orbis_api.block.*;
import com.gildedgames.orbis_api.core.BlockDataChunk;
import com.gildedgames.orbis_api.core.CreationData;
import com.gildedgames.orbis_api.core.GameRegistrar;
import com.gildedgames.orbis_api.core.PlacedBlueprint;
import com.gildedgames.orbis_api.core.baking.BakedBlueprint;
import com.gildedgames.orbis_api.core.baking.BakedEntitySpawn;
import com.gildedgames.orbis_api.core.registry.IOrbisDefinitionRegistry;
import com.gildedgames.orbis_api.core.tree.ConditionLink;
import com.gildedgames.orbis_api.core.tree.LayerLink;
import com.gildedgames.orbis_api.core.tree.NodeMultiParented;
import com.gildedgames.orbis_api.core.tree.NodeTree;
import com.gildedgames.orbis_api.core.variables.*;
import com.gildedgames.orbis_api.core.variables.conditions.GuiConditionCheckBlueprintVariable;
import com.gildedgames.orbis_api.core.variables.conditions.GuiConditionPercentage;
import com.gildedgames.orbis_api.core.variables.conditions.GuiConditionRatio;
import com.gildedgames.orbis_api.core.variables.post_resolve_actions.PostResolveActionMutateBlueprintVariable;
import com.gildedgames.orbis_api.core.variables.post_resolve_actions.PostResolveActionSpawnEntities;
import com.gildedgames.orbis_api.core.variables.var_comparators.*;
import com.gildedgames.orbis_api.core.variables.var_mutators.*;
import com.gildedgames.orbis_api.core.world_objects.BlueprintRegion;
import com.gildedgames.orbis_api.data.DataCondition;
import com.gildedgames.orbis_api.data.blueprint.*;
import com.gildedgames.orbis_api.data.framework.FrameworkData;
import com.gildedgames.orbis_api.data.framework.FrameworkNode;
import com.gildedgames.orbis_api.data.json.JsonData;
import com.gildedgames.orbis_api.data.management.IProject;
import com.gildedgames.orbis_api.data.management.IProjectManager;
import com.gildedgames.orbis_api.data.management.impl.*;
import com.gildedgames.orbis_api.data.pathway.Entrance;
import com.gildedgames.orbis_api.data.pathway.PathwayData;
import com.gildedgames.orbis_api.data.region.Region;
import com.gildedgames.orbis_api.data.schedules.*;
import com.gildedgames.orbis_api.data.shapes.*;
import com.gildedgames.orbis_api.inventory.InventorySpawnEggs;
import com.gildedgames.orbis_api.network.INetworkMultipleParts;
import com.gildedgames.orbis_api.network.NetworkMultipleParts;
import com.gildedgames.orbis_api.network.instances.PacketRegisterDimension;
import com.gildedgames.orbis_api.network.instances.PacketRegisterInstance;
import com.gildedgames.orbis_api.network.instances.PacketUnregisterDimension;
import com.gildedgames.orbis_api.preparation.IPrepRegistry;
import com.gildedgames.orbis_api.preparation.impl.PrepRegistry;
import com.gildedgames.orbis_api.util.io.IClassSerializer;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.io.SimpleSerializer;
import com.gildedgames.orbis_api.util.mc.BlockPosDimension;
import com.gildedgames.orbis_api.world.data.IWorldDataManager;
import com.gildedgames.orbis_api.world.instances.IInstanceRegistry;
import com.gildedgames.orbis_api.world.instances.InstanceRegistryImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class OrbisServices implements IOrbisServices
{
	public final Logger logger = LogManager.getLogger("OrbisAPI");

	private final Map<String, IOrbisDefinitionRegistry> idToRegistry = Maps.newHashMap();

	private final String baseFolder = "orbis";

	private final Map<String, IProject> loadedProjects = Maps.newHashMap();

	private final GameRegistrar gameRegistrar = new GameRegistrar();

	private List<IOrbisServicesListener> listeners = Lists.newArrayList();

	private IProjectManager projectManager;

	private IOHelper io;

	private INetworkMultipleParts network;

	private IInstanceRegistry instancesRegistry = new InstanceRegistryImpl();

	private Object mod;

	private String archiveBaseName;

	private IPrepRegistry sectors = new PrepRegistry();

	private OrbisLootTableCache lootTableCache = new OrbisLootTableCache();

	private boolean scanAndCacheProjectsOnStartup;

	public OrbisServices()
	{
		this.network = new NetworkMultipleParts(OrbisAPI.MOD_ID);

		this.network.reg(PacketRegisterDimension.Handler.class, PacketRegisterDimension.class, Side.CLIENT);
		this.network.reg(PacketUnregisterDimension.Handler.class, PacketUnregisterDimension.class, Side.CLIENT);
		this.network.reg(PacketRegisterInstance.Handler.class, PacketRegisterInstance.class, Side.CLIENT);
	}

	@Nullable
	@Override
	public IOrbisDefinitionRegistry findDefinitionRegistry(final String registryId)
	{
		final IOrbisDefinitionRegistry registry = this.idToRegistry.get(registryId);

		return registry;
	}

	@Override
	public void register(final IOrbisDefinitionRegistry registry)
	{
		if (registry == null)
		{
			throw new RuntimeException("Registry provided is null! Aborting.");
		}

		this.idToRegistry.put(registry.getRegistryId(), registry);
	}

	@Override
	@Nullable
	public IProject loadProject(final MinecraftServer server, final ResourceLocation location, Object mod, String archiveBaseName)
	{
		IProject project = this.get(server, location, mod, archiveBaseName);

		if (project != null)
		{
			project.setIsModProject(true);
		}

		return project;
	}

	@Override
	public IOHelper io()
	{
		if (this.io == null)
		{
			this.io = new IOHelper();

			final IClassSerializer s = new SimpleSerializer("orbis_api");

			IOHelper.register(s, 0, Region.class);
			IOHelper.register(s, 1, BlueprintData.class);
			IOHelper.register(s, 3, BlockDataContainer.class);
			IOHelper.register(s, 4, SphereShape.class);
			IOHelper.register(s, 5, LineShape.class);
			IOHelper.register(s, 6, BlockFilter.class);
			IOHelper.register(s, 7, BlockFilterLayer.class);
			IOHelper.register(s, 8, BlockDataWithConditions.class);
			IOHelper.register(s, 9, DataCondition.class);
			IOHelper.register(s, 10, DataMetadata.class);
			IOHelper.register(s, 11, ProjectIdentifier.class);
			IOHelper.register(s, 12, OrbisProject.class);
			IOHelper.register(s, 13, DataIdentifier.class);
			IOHelper.register(s, 14, OrbisProjectCache.class);
			IOHelper.register(s, 15, ProjectMetadata.class);
			IOHelper.register(s, 16, BlockDataContainerDefaultVoid.class);
			IOHelper.register(s, 17, CreationData.class);
			IOHelper.register(s, 18, PyramidShape.class);
			IOHelper.register(s, 19, ConeShape.class);
			IOHelper.register(s, 20, CylinderShape.class);
			IOHelper.register(s, 21, DomeShape.class);
			IOHelper.register(s, 22, CuboidShape.class);
			IOHelper.register(s, 23, DataCache.class);
			IOHelper.register(s, 24, ScheduleLayer.class);
			IOHelper.register(s, 25, FilterRecord.class);
			IOHelper.register(s, 26, Entrance.class);
			IOHelper.register(s, 27, BlueprintRegion.class);
			IOHelper.register(s, 28, FrameworkNode.class);
			IOHelper.register(s, 29, ScheduleRegion.class);
			IOHelper.register(s, 30, InventorySpawnEggs.class);
			IOHelper.register(s, 31, ScheduleRecord.class);
			IOHelper.register(s, 32, ScheduleBlueprint.class);
			IOHelper.register(s, 33, BlockPosDimension.class);
			IOHelper.register(s, 34, FilterOptions.class);
			IOHelper.register(s, 35, FrameworkData.class);
			IOHelper.register(s, 36, PlacedBlueprint.class);
			IOHelper.register(s, 37, PathwayData.class);
			IOHelper.register(s, 38, BlueprintDataHolder.class);
			IOHelper.register(s, 39, BlueprintStackerData.class);
			IOHelper.register(s, 40, PostGenReplaceLayer.class);
			IOHelper.register(s, 41, GuiVarBoolean.class);
			IOHelper.register(s, 42, GuiVarDouble.class);
			IOHelper.register(s, 43, GuiVarFloat.class);
			IOHelper.register(s, 44, GuiVarFloatRange.class);
			IOHelper.register(s, 45, GuiVarInteger.class);
			IOHelper.register(s, 46, GuiVarString.class);
			IOHelper.register(s, 47, GuiConditionRatio.class);
			IOHelper.register(s, 48, NodeMultiParented.class);
			IOHelper.register(s, 49, NodeTree.class);
			IOHelper.register(s, 50, ConditionLink.class);
			IOHelper.register(s, 51, LayerLink.class);
			IOHelper.register(s, 52, GuiConditionPercentage.class);
			IOHelper.register(s, 53, BlueprintVariable.class);
			IOHelper.register(s, 54, GuiConditionCheckBlueprintVariable.class);
			IOHelper.register(s, 55, GuiVarBlueprintVariable.class);
			IOHelper.register(s, 56, GuiVarDropdown.class);
			IOHelper.register(s, 57, NumberSet.class);
			IOHelper.register(s, 58, NumberMultiply.class);
			IOHelper.register(s, 59, NumberIncrease.class);
			IOHelper.register(s, 60, NumberDivide.class);
			IOHelper.register(s, 61, NumberDecrease.class);
			IOHelper.register(s, 62, NumberLessThanOrEqual.class);
			IOHelper.register(s, 63, NumberLessThan.class);
			IOHelper.register(s, 64, NumberGreaterThanOrEqual.class);
			IOHelper.register(s, 65, NumberGreaterThan.class);
			IOHelper.register(s, 66, NumberEquals.class);
			IOHelper.register(s, 67, NumberDoesntEqual.class);
			IOHelper.register(s, 68, GuiVarString.Equals.class);
			IOHelper.register(s, 69, GuiVarString.DoesntEqual.class);
			IOHelper.register(s, 70, GuiVarString.Contains.class);
			IOHelper.register(s, 71, GuiVarString.Clear.class);
			IOHelper.register(s, 72, GuiVarString.Replace.class);
			IOHelper.register(s, 73, GuiVarString.Concatenate.class);
			IOHelper.register(s, 74, GuiVarString.Set.class);
			IOHelper.register(s, 75, GuiVarBoolean.EqualsTrue.class);
			IOHelper.register(s, 76, GuiVarBoolean.EqualsFalse.class);
			IOHelper.register(s, 77, GuiVarBoolean.Set.class);
			IOHelper.register(s, 78, BlockDataContainerDefault.class);
			IOHelper.register(s, 79, PostResolveActionMutateBlueprintVariable.class);
			IOHelper.register(s, 80, BlockDataChunk.class);
			IOHelper.register(s, 81, BakedBlueprint.class);
			IOHelper.register(s, 82, BakedEntitySpawn.class);
			IOHelper.register(s, 83, BlockStateRecord.class);
			IOHelper.register(s, 84, ScheduleLayerOptions.class);
			IOHelper.register(s, 85, BlueprintMetadata.class);
			IOHelper.register(s, 87, PostResolveActionSpawnEntities.class);
			IOHelper.register(s, 88, GuiVarItemStack.class);
			IOHelper.register(s, 89, JsonData.class);

			this.io.register(s);
		}

		return this.io;
	}

	@Nullable
	private IProject get(@Nullable final MinecraftServer server, final ResourceLocation projectPath, Object mod, String archiveBaseName)
	{
		final String s = projectPath.getResourcePath();

		if (this.loadedProjects.containsKey(s))
		{
			return this.loadedProjects.get(s);
		}
		else
		{
			if (server == null)
			{
				this.readProjectFromJar(mod, archiveBaseName, projectPath);
			}
			else
			{
				this.readProject(mod, archiveBaseName, projectPath);
			}

			return this.loadedProjects.getOrDefault(s, null);
		}
	}

	/**
	 * This reads a structure template from the given location and stores it.
	 * This first attempts get the template from an external folder.
	 * If it isn't there then it attempts to take it from the minecraft jar.
	 */
	private boolean readProject(Object mod, String archiveBaseName, final ResourceLocation server)
	{
		final String s = server.getResourcePath();
		final File file1 = new File(this.baseFolder, s + File.separator + "project_data.project");

		if (!file1.exists())
		{
			return this.readProjectFromJar(mod, archiveBaseName, server);
		}
		else
		{
			InputStream inputstream = null;
			boolean flag;

			try
			{
				inputstream = new FileInputStream(file1);
				this.readProjectFromStream(mod, archiveBaseName, s, inputstream, new File(this.baseFolder, s + "/").toURI());
				return true;
			}
			catch (final Throwable var10)
			{
				flag = false;
			}
			finally
			{
				IOUtils.closeQuietly(inputstream);
			}

			return flag;
		}
	}

	/**
	 * reads a template from the minecraft jar
	 */
	private boolean readProjectFromJar(Object mod, String archiveBaseName, final ResourceLocation id)
	{
		final String s = id.getResourceDomain();
		final String s1 = id.getResourcePath();
		InputStream inputstream = null;
		boolean flag;

		try
		{
			inputstream = MinecraftServer.class.getResourceAsStream("/assets/" + s + "/orbis/" + s1 + "/project_data.project");
			this.readProjectFromStream(mod, archiveBaseName, s1, inputstream, URI.create("/assets/" + s + "/orbis/" + s1 + "/"));
			return true;
		}
		catch (final IOException var10)
		{
			flag = false;
			OrbisAPI.LOGGER.error(var10);
		}
		finally
		{
			IOUtils.closeQuietly(inputstream);
		}

		return flag;
	}

	/**
	 * reads a template from an inputstream
	 */
	private void readProjectFromStream(Object mod, String archiveBaseName, final String id, final InputStream stream, final URI location) throws IOException
	{
		final NBTTagCompound tag = CompressedStreamTools.readCompressed(stream);

		final NBTFunnel funnel = new NBTFunnel(tag);

		final IProject project = funnel.get("project");

		project.setJarLocation(location);
		project.setModAndArchiveLoadingFrom(mod, archiveBaseName);

		project.loadAndCacheData();

		this.loadedProjects.put(id, project);
	}

	@Override
	public OrbisLootTableCache lootTableCache()
	{
		return this.lootTableCache;
	}

	@Override
	public IPrepRegistry sectors()
	{
		return this.sectors;
	}

	@Override
	public INetworkMultipleParts network()
	{
		return this.network;
	}

	@Override
	public void listen(IOrbisServicesListener listener)
	{
		if (!this.listeners.contains(listener))
		{
			this.listeners.add(listener);
		}
	}

	@Override
	public boolean unlisten(IOrbisServicesListener listener)
	{
		return this.listeners.remove(listener);
	}

	@Override
	public GameRegistrar registrar()
	{
		return this.gameRegistrar;
	}

	@Override
	public synchronized void startProjectManager()
	{
		if (this.projectManager != null)
		{
			return;
		}

		if (OrbisAPI.isClient())
		{
			final ServerData data = Minecraft.getMinecraft().getCurrentServerData();

			if (data != null)
			{
				this.projectManager = new OrbisProjectManager(
						new File(Minecraft.getMinecraft().mcDataDir, "/orbis/servers/" + data.serverIP.replace(":", "_") + "/projects/"), this.mod,
						this.archiveBaseName);
			}
			else
			{
				this.projectManager = new OrbisProjectManager(new File(Minecraft.getMinecraft().mcDataDir, "/orbis/local/projects/"), this.mod,
						this.archiveBaseName);
			}
		}

		if (this.projectManager == null)
		{
			this.projectManager = new OrbisProjectManager(new File(DimensionManager.getCurrentSaveRootDirectory(), "/orbis/projects/"), this.mod,
					this.archiveBaseName);
		}

		this.listeners.forEach((l) -> l.onStartProjectManager(this.projectManager));

		if (this.scanAndCacheProjectsOnStartup)
		{
			this.projectManager.scanAndCacheProjects();
		}
	}

	@Override
	public synchronized void stopProjectManager()
	{
		if (this.projectManager != null)
		{
			this.projectManager.flushProjects();
			this.projectManager = null;
		}
	}

	@Override
	public IInstanceRegistry instances()
	{
		return this.instancesRegistry;
	}

	@Override
	public IWorldDataManager getWorldDataManager(World world)
	{
		return world.getCapability(OrbisAPICapabilities.WORLD_DATA, null).get();
	}

	@Override
	public synchronized IProjectManager getProjectManager()
	{
		this.verifyProjectManagerStarted();

		return this.projectManager;
	}

	@Override
	public void verifyProjectManagerStarted()
	{
		if (this.projectManager == null)
		{
			this.startProjectManager();
		}
	}

	@Override
	public void setProjectManagerInitSource(Object mod, String archiveBaseName)
	{
		this.mod = mod;
		this.archiveBaseName = archiveBaseName;
	}

	@Override
	public void enableScanAndCacheProjectsOnStartup(boolean flag)
	{
		this.scanAndCacheProjectsOnStartup = flag;
	}

}