package com.gildedgames.orbis_api;

import com.gildedgames.orbis_api.core.GameRegistrar;
import com.gildedgames.orbis_api.core.registry.IOrbisDefinitionRegistry;
import com.gildedgames.orbis_api.data.management.IProject;
import com.gildedgames.orbis_api.data.management.IProjectManager;
import com.gildedgames.orbis_api.network.INetworkMultipleParts;
import com.gildedgames.orbis_api.world.instances.IInstanceRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public interface IOrbisServices
{

	INetworkMultipleParts network();

	void listen(IOrbisServicesListener listener);

	boolean unlisten(IOrbisServicesListener listener);

	GameRegistrar registrar();

	Logger log();

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
	 * @param location The location of the project.
	 * @return The loaded project.
	 */
	IProject loadProject(MinecraftServer server, ResourceLocation location, Object mod, String archiveBaseName);

	IOHelper io();

	IProjectManager getProjectManager();

	void setProjectManagerInitSource(Object mod, String archiveBaseName);

	void startProjectManager();

	void stopProjectManager();

	IInstanceRegistry instances();

}