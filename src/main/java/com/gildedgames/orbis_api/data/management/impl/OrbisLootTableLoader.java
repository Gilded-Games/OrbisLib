package com.gildedgames.orbis_api.data.management.impl;

import com.gildedgames.orbis_api.ReflectionOrbis;
import com.gildedgames.orbis_api.data.management.IDataIdentifier;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.item.Item;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;

public class OrbisLootTableLoader
{
	private static ThreadLocal<Deque<LootTableContext>> lootContext = new ThreadLocal<>();

	private static LootTableContext getLootTableContext()
	{
		LootTableContext ctx = lootContext.get().peek();

		if (ctx == null)
		{
			throw new JsonParseException(
					"Invalid call stack, could not grab json context!"); // Should I throw this? Do we care about custom deserializers outside the manager?
		}

		return ctx;
	}

	@Nullable
	public static LootTable loadLootTable(Gson gson, IDataIdentifier id, String data, boolean custom)
	{
		Deque<LootTableContext> que = lootContext.get();

		if (que == null)
		{
			que = Queues.newArrayDeque();
			lootContext.set(que);
		}

		LootTable ret = null;
		try
		{
			que.push(new LootTableContext(id, custom));
			ret = gson.fromJson(data, LootTable.class);
			que.pop();
		}
		catch (JsonParseException e)
		{
			que.pop();
			throw e;
		}

		if (ret != null)
		{
			ret.freeze();
		}

		return ret;
	}

	public static String readPoolName(JsonObject json)
	{
		LootTableContext ctx = getLootTableContext();
		ctx.resetPoolCtx();

		if (json.has("id"))
		{
			return JsonUtils.getString(json, "id");
		}

		if (ctx.custom)
		{
			return "custom#" + json.hashCode(); //We don't care about custom ones modders shouldn't be editing them!
		}

		ctx.poolCount++;

		throw new JsonParseException("Loot Table \"" + ctx.id.toString() + "\" Missing `id` entry for pool #" + (ctx.poolCount - 1));
	}

	public static String readLootEntryName(JsonObject json, String type)
	{
		LootTableContext ctx = getLootTableContext();
		ctx.entryCount++;

		if (json.has("entryName"))
		{
			return ctx.validateEntryName(JsonUtils.getString(json, "entryName"));
		}

		if (ctx.custom)
		{
			return "custom#" + json.hashCode(); //We don't care about custom ones modders shouldn't be editing them!
		}

		String name = null;
		if ("item".equals(type))
		{
			name = JsonUtils.getString(json, "id");
		}
		else if ("loot_table".equals(type))
		{
			name = JsonUtils.getString(json, "id");
		}
		else if ("empty".equals(type))
		{
			name = "empty";
		}

		return ctx.validateEntryName(name);
	}

	public static LootEntryItem deserializeItem(JsonObject object, JsonDeserializationContext deserializationContext, int weightIn, int qualityIn,
			LootCondition[] conditionsIn)
	{
		String name = readLootEntryName(object, "item");
		Item item = JsonUtils.getItem(object, "name");
		LootFunction[] alootfunction;

		if (object.has("functions"))
		{
			alootfunction = (LootFunction[]) JsonUtils.deserializeClass(object, "functions", deserializationContext, LootFunction[].class);
		}
		else
		{
			alootfunction = new LootFunction[0];
		}

		return new LootEntryItem(item, weightIn, qualityIn, alootfunction, conditionsIn, name);
	}

	public static LootEntryTable deserializeTable(JsonObject object, JsonDeserializationContext deserializationContext, int weightIn, int qualityIn,
			LootCondition[] conditionsIn)
	{
		String name = readLootEntryName(object, "loot_table");
		ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(object, "name"));
		return new LootEntryTable(resourcelocation, weightIn, qualityIn, conditionsIn, name);
	}

	public static LootEntryEmpty deserializeEmpty(JsonObject object, JsonDeserializationContext deserializationContext, int weightIn, int qualityIn,
			LootCondition[] conditionsIn)
	{
		return new LootEntryEmpty(weightIn, qualityIn, conditionsIn, readLootEntryName(object, "empty"));
	}

	private static class LootTableContext
	{
		public final IDataIdentifier id;

		public final boolean custom;

		public int poolCount = 0;

		public int entryCount = 0;

		private HashSet<String> entryNames = Sets.newHashSet();

		private LootTableContext(IDataIdentifier id, boolean custom)
		{
			this.id = id;
			this.custom = custom;
		}

		private void resetPoolCtx()
		{
			this.entryCount = 0;
			this.entryNames.clear();
		}

		public String validateEntryName(@Nullable String name)
		{
			if (name != null && !this.entryNames.contains(name))
			{
				this.entryNames.add(name);
				return name;
			}

			throw new JsonParseException(
					"Loot Table \"" + this.id.toString() + "\" Duplicate entry id \"" + name + "\" for pool #" + (this.poolCount - 1) + " entry #" + (
							this.entryCount - 1));
		}
	}

	public static class LootPoolSerializer implements JsonDeserializer<LootPool>, JsonSerializer<LootPool>
	{
		@Override
		public LootPool deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException
		{
			JsonObject jsonobject = JsonUtils.getJsonObject(element, "loot pool");
			String name = readPoolName(jsonobject);
			LootEntry[] alootentry = JsonUtils.deserializeClass(jsonobject, "entries", context, LootEntry[].class);
			LootCondition[] alootcondition = JsonUtils.deserializeClass(jsonobject, "conditions", new LootCondition[0], context, LootCondition[].class);
			RandomValueRange randomvaluerange = JsonUtils.deserializeClass(jsonobject, "rolls", context, RandomValueRange.class);
			RandomValueRange randomvaluerange1 = JsonUtils
					.deserializeClass(jsonobject, "bonus_rolls", new RandomValueRange(0.0F, 0.0F), context, RandomValueRange.class);
			return new LootPool(alootentry, alootcondition, randomvaluerange, randomvaluerange1, name);
		}

		@Override
		public JsonElement serialize(LootPool pool, Type type, JsonSerializationContext context)
		{
			final List<LootEntry> lootEntries = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, pool, ReflectionOrbis.LOOT_ENTRIES.getMappings());
			final List<LootCondition> poolConditions = ObfuscationReflectionHelper
					.getPrivateValue(LootPool.class, pool, ReflectionOrbis.POOL_CONDITIONS.getMappings());
			RandomValueRange rolls = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, pool, ReflectionOrbis.ROLLS.getMappings());
			RandomValueRange bonusRolls = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, pool, ReflectionOrbis.BONUS_ROLLS.getMappings());
			final String name = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, pool, ReflectionOrbis.NAME.getMappings());

			JsonObject jsonobject = new JsonObject();
			if (name != null && !name.startsWith("custom#"))
			{
				jsonobject.add("name", context.serialize(name));
			}
			jsonobject.add("entries", context.serialize(lootEntries));
			jsonobject.add("rolls", context.serialize(rolls));

			if (bonusRolls.getMin() != 0.0F && bonusRolls.getMax() != 0.0F)
			{
				jsonobject.add("bonus_rolls", context.serialize(bonusRolls));
			}

			if (!poolConditions.isEmpty())
			{
				jsonobject.add("conditions", context.serialize(poolConditions));
			}

			return jsonobject;
		}
	}

	public static class LootEntrySerializer implements JsonDeserializer<LootEntry>, JsonSerializer<LootEntry>
	{
		@Override
		public LootEntry deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException
		{
			JsonObject jsonobject = JsonUtils.getJsonObject(element, "loot item");
			String s = JsonUtils.getString(jsonobject, "type");
			int i = JsonUtils.getInt(jsonobject, "weight", 1);
			int j = JsonUtils.getInt(jsonobject, "quality", 0);
			LootCondition[] alootcondition;

			if (jsonobject.has("conditions"))
			{
				alootcondition = (LootCondition[]) JsonUtils.deserializeClass(jsonobject, "conditions", context, LootCondition[].class);
			}
			else
			{
				alootcondition = new LootCondition[0];
			}

			LootEntry ret = net.minecraftforge.common.ForgeHooks.deserializeJsonLootEntry(s, jsonobject, i, j, alootcondition);
			if (ret != null)
			{
				return ret;
			}

			if ("item".equals(s))
			{
				return deserializeItem(jsonobject, context, i, j, alootcondition);
			}
			else if ("loot_table".equals(s))
			{
				return deserializeTable(jsonobject, context, i, j, alootcondition);
			}
			else if ("empty".equals(s))
			{
				return deserializeEmpty(jsonobject, context, i, j, alootcondition);
			}
			else
			{
				throw new JsonSyntaxException("Unknown loot entry type '" + s + "'");
			}
		}

		@Override
		public JsonElement serialize(LootEntry entry, Type reflectType, JsonSerializationContext context)
		{
			final String entryName = ObfuscationReflectionHelper.getPrivateValue(LootEntry.class, entry, ReflectionOrbis.ENTRY_NAME.getMappings());
			final int weight = ObfuscationReflectionHelper.getPrivateValue(LootEntry.class, entry, ReflectionOrbis.WEIGHT.getMappings());
			final int quality = ObfuscationReflectionHelper.getPrivateValue(LootEntry.class, entry, ReflectionOrbis.QUALITY.getMappings());
			final LootCondition[] conditions = ObfuscationReflectionHelper.getPrivateValue(LootEntry.class, entry, ReflectionOrbis.CONDITIONS.getMappings());

			JsonObject jsonobject = new JsonObject();

			if (entryName != null && !entryName.startsWith("custom#"))
			{
				jsonobject.addProperty("entryName", entryName);
			}

			jsonobject.addProperty("weight", Integer.valueOf(weight));
			jsonobject.addProperty("quality", Integer.valueOf(quality));

			if (conditions.length > 0)
			{
				jsonobject.add("conditions", context.serialize(conditions));
			}

			String type = net.minecraftforge.common.ForgeHooks.getLootEntryType(entry);

			if (type != null)
			{
				jsonobject.addProperty("type", type);
			}

			else if (entry instanceof LootEntryItem)
			{
				jsonobject.addProperty("type", "item");
			}
			else if (entry instanceof LootEntryTable)
			{
				jsonobject.addProperty("type", "loot_table");
			}
			else
			{
				if (!(entry instanceof LootEntryEmpty))
				{
					throw new IllegalArgumentException("Don't know how to serialize " + entry);
				}

				jsonobject.addProperty("type", "empty");
			}

			ReflectionOrbis.invokeMethod(ReflectionOrbis.SERIALIZE, entry, jsonobject, context);

			return jsonobject;
		}
	}
}
