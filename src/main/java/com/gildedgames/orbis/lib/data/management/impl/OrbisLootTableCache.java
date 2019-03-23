package com.gildedgames.orbis.lib.data.management.impl;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.data.management.IDataIdentifier;
import com.gildedgames.orbis.lib.data.management.IProject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class OrbisLootTableCache
{
	private static final Logger LOGGER = LogManager.getLogger();

	private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer())
			.registerTypeAdapter(LootPool.class, new OrbisLootTableLoader.LootPoolSerializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer())
			.registerTypeHierarchyAdapter(LootEntry.class, new OrbisLootTableLoader.LootEntrySerializer())
			.registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer())
			.registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer())
			.registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();

	private final LoadingCache<IDataIdentifier, LootTable> registeredLootTables = CacheBuilder.newBuilder().build(
			new OrbisLootTableCache.Loader());

	public OrbisLootTableCache()
	{
		this.reloadLootTables();
	}

	public LootTable getLootTableFromLocation(IDataIdentifier id)
	{
		return this.registeredLootTables.getUnchecked(id);
	}

	public void reloadLootTables()
	{
		this.registeredLootTables.invalidateAll();
	}

	@OnlyIn(Dist.CLIENT)
	public void attachReloadListener()
	{
		final IResourceManager resManager = Minecraft.getMinecraft().getResourceManager();

		if (resManager instanceof IReloadableResourceManager)
		{
			((IReloadableResourceManager) resManager).registerReloadListener(new ReloadListener(this));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class ReloadListener implements IResourceManagerReloadListener
	{
		private final OrbisLootTableCache cache;

		public ReloadListener(final OrbisLootTableCache cache)
		{
			this.cache = cache;
		}

		@Override
		public void onResourceManagerReload(final IResourceManager resourceManager)
		{
			this.cache.reloadLootTables();
		}
	}

	class Loader extends CacheLoader<IDataIdentifier, LootTable>
	{
		private Loader()
		{
		}

		@Override
		public LootTable load(IDataIdentifier id)
		{
			LootTable loottable = this.loadLootTable(id);

			if (loottable == null)
			{
				loottable = LootTable.EMPTY_LOOT_TABLE;
				OrbisLootTableCache.LOGGER.warn("Couldn't find resource table {}", id);
			}

			return loottable;
		}

		@Nullable
		private LootTable loadLootTable(IDataIdentifier id)
		{
			Optional<IProject> projectOp = OrbisLib.services().getProjectManager().findProject(id.getProjectIdentifier());

			if (!projectOp.isPresent())
			{
				OrbisLootTableCache.LOGGER.warn("Couldn't load loot table {} from {}", id);
				return LootTable.EMPTY_LOOT_TABLE;
			}

			IProject project = projectOp.get();

			File file = project.getFileForId(id);

			if (file != null)
			{
				if (file.exists())
				{
					if (file.isFile())
					{
						String s;

						try
						{
							s = Files.toString(file, StandardCharsets.UTF_8);
						}
						catch (IOException ioexception)
						{
							OrbisLootTableCache.LOGGER.warn("Couldn't load loot table {} from {}", id, file, ioexception);
							return LootTable.EMPTY_LOOT_TABLE;
						}

						try
						{
							return OrbisLootTableLoader.loadLootTable(OrbisLootTableCache.GSON_INSTANCE, id, s, true);
						}
						catch (IllegalArgumentException | JsonParseException jsonparseexception)
						{
							OrbisLootTableCache.LOGGER.error("Couldn't load loot table {} from {}", id, file, jsonparseexception);
							return LootTable.EMPTY_LOOT_TABLE;
						}
					}
					else
					{
						OrbisLootTableCache.LOGGER.warn("Expected to find loot table {} at {} but it was a folder.", id, file);
						return LootTable.EMPTY_LOOT_TABLE;
					}
				}
			}
			else
			{
				String resourceLocation = project.getResourceLocationForId(id);

				if (resourceLocation != null)
				{
					URL url = MinecraftServer.class.getResource(resourceLocation);

					String s;

					try
					{
						s = Resources.toString(url, StandardCharsets.UTF_8);
					}
					catch (IOException ioexception)
					{
						OrbisLootTableCache.LOGGER.warn("Couldn't load loot table {} from {}", id, url, ioexception);
						return LootTable.EMPTY_LOOT_TABLE;
					}

					try
					{
						return OrbisLootTableLoader.loadLootTable(OrbisLootTableCache.GSON_INSTANCE, id, s, false);
					}
					catch (JsonParseException jsonparseexception)
					{
						OrbisLootTableCache.LOGGER.error("Couldn't load loot table {} from {}", id, url, jsonparseexception);
						return LootTable.EMPTY_LOOT_TABLE;
					}
				}
			}

			return null;
		}
	}
}