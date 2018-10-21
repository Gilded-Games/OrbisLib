package com.gildedgames.orbis_api;

import com.gildedgames.orbis_api.core.GameRegistrar;
import com.gildedgames.orbis_api.core.registry.IOrbisDefinitionRegistry;
import com.gildedgames.orbis_api.data.management.IProject;
import com.gildedgames.orbis_api.data.management.IProjectManager;
import com.gildedgames.orbis_api.data.management.impl.OrbisLootTableCache;
import com.gildedgames.orbis_api.network.INetworkMultipleParts;
import com.gildedgames.orbis_api.preparation.IPrepRegistry;
import com.gildedgames.orbis_api.world.data.IWorldDataManager;
import com.gildedgames.orbis_api.world.instances.IInstanceRegistry;
import com.google.gson.Gson;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IOrbisServices
{

	Gson getGson();

	OrbisLootTableCache lootTableCache();

	IPrepRegistry sectors();

	INetworkMultipleParts network();

	void listen(IOrbisServicesListener listener);

	boolean unlisten(IOrbisServicesListener listener);

	GameRegistrar registrar();

	/**
	 * Searches for the definition registry linked with the
	 * provided registry id. If it cannot find it, it will
	 * return null.
	 * @param registryId The unique registry id associated
	 *                   with the definition registry you're
	 *                   attempting to find.
	 */
	@Nullable
	IOrbisDefinitionRegistry findDefinitionRegistry(String registryId);

	/**
	 *
	 * @param registry The registry you're registering.
	 */
	void register(IOrbisDefinitionRegistry registry);

	/**
	 * Loads a project with the provided resource location.
	 *
	 * Marks it as a "mod project" to prevent removal of caching and flushing to the disk
	 * @param location The location of the project.
	 * @return The loaded project.
	 */
	IProject loadProject(MinecraftServer server, ResourceLocation location, Object mod, String archiveBaseName);

	IOHelper io();

	IProjectManager getProjectManager();

	/**
	 * Makes sure the project manager is started before a mod uses projects loaded from it.
	 */
	void verifyProjectManagerStarted();

	void setProjectManagerInitSource(Object mod, String archiveBaseName);

	void enableScanAndCacheProjectsOnStartup(boolean flag);

	void startProjectManager();

	void stopProjectManager();

	IInstanceRegistry instances();

	IWorldDataManager getWorldDataManager(World world);
}