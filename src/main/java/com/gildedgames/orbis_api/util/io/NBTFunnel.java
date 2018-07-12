package com.gildedgames.orbis_api.util.io;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.gildedgames.orbis_api.util.mc.NBTHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class NBTFunnel
{

	public static Function<IBlockState, NBTTagCompound> BLOCKSTATE_SETTER = state ->
	{
		NBTTagCompound t = new NBTTagCompound();

		final ResourceLocation identifier = OrbisAPI.services().registrar().getIdentifierFor(state.getBlock());
		short meta = (short) (state.getBlock().getMetaFromState(state));

		t.setString("mod", identifier.getResourceDomain());
		t.setString("name", identifier.getResourcePath());
		t.setShort("meta", meta);

		return t;
	};

	public static Function<NBTTagCompound, IBlockState> BLOCKSTATE_GETTER = tag ->
	{
		String mod = tag.getString("mod");
		String name = tag.getString("name");

		final Block block = OrbisAPI.services().registrar().findBlock(new ResourceLocation(mod, name));

		int meta = tag.getShort("meta");

		return block.getStateFromMeta(meta);
	};

	public static Function<ItemStack, NBTTagCompound> STACK_SETTER = o ->
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setTag("s", NBTHelper.writeStack(o));

		return tag;
	};

	public static Function<NBTTagCompound, ItemStack> STACK_GETTER = n -> NBTHelper.readStack(n.getCompoundTag("s"));

	public static Function<UUID, NBTTagCompound> UUID_SETTER = o ->
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setString("u", o.toString());

		return tag;
	};

	public static Function<NBTTagCompound, UUID> UUID_GETTER = n -> UUID.fromString(n.getString("u"));

	public static Function<String, NBTTagCompound> STRING_SETTER = o ->
	{
		NBTTagCompound f = new NBTTagCompound();

		f.setString("s", o);

		return f;
	};

	public static Function<NBTTagCompound, String> STRING_GETTER = n -> n.getString("s");

	public static Function<Boolean, NBTTagCompound> BOOLEAN_SETTER = o ->
	{
		NBTTagCompound f = new NBTTagCompound();

		f.setBoolean("b", o);

		return f;
	};

	public static Function<NBTTagCompound, Boolean> BOOLEAN_GETTER = n -> n.getBoolean("b");

	public static Function<Integer, NBTTagCompound> INTEGER_SETTER = o ->
	{
		NBTTagCompound f = new NBTTagCompound();

		f.setInteger("i", o);

		return f;
	};

	public static Function<NBTTagCompound, Integer> INTEGER_GETTER = n -> n.getInteger("i");

	public static Function<ResourceLocation, NBTTagCompound> LOC_SETTER = o ->
	{
		NBTTagCompound f = new NBTTagCompound();

		f.setString("loc", o.toString());

		return f;
	};

	public static Function<NBTTagCompound, ResourceLocation> LOC_GETTER = n -> new ResourceLocation(n.getString("loc"));

	public static Function<BlockPos, NBTTagCompound> POS_SETTER = o ->
	{
		NBTFunnel f = new NBTFunnel(new NBTTagCompound());

		f.setPos("p", o);

		return f.getTag();
	};

	public static Function<NBTTagCompound, BlockPos> POS_GETTER = n ->
	{
		NBTFunnel f = new NBTFunnel(n);

		return f.getPos("p");
	};

	public static Function<Pos2D, NBTTagCompound> POS2D_SETTER = o ->
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setFloat("x", o.x());
		tag.setFloat("y", o.y());

		return tag;
	};

	public static Function<NBTTagCompound, Pos2D> POS2D_GETTER = n -> Pos2D.flush(n.getFloat("x"), n.getFloat("y"));

	public static Function<ChunkPos, NBTTagCompound> CHUNK_POS_SETTER = o ->
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setInteger("x", o.x);
		tag.setInteger("z", o.z);

		return tag;
	};

	public static Function<NBTTagCompound, ChunkPos> CHUNK_POS_GETTER = n -> new ChunkPos(n.getInteger("x"), n.getInteger("z"));

	private final NBTTagCompound tag;

	public NBTFunnel(final NBTTagCompound tag)
	{
		this.tag = tag;
	}

	public static <T extends NBT> Function<NBTTagCompound, T> getter()
	{
		return n ->
		{
			NBTFunnel funnel = new NBTFunnel(n);

			return funnel.get("o");
		};
	}

	public static <T extends NBT> Function<T, NBTTagCompound> setter()
	{
		return o ->
		{
			NBTTagCompound tag = new NBTTagCompound();
			NBTFunnel funnel = new NBTFunnel(tag);

			funnel.set("o", o);

			return tag;
		};
	}

	public static <T extends List<? extends NBT>> Function<NBTTagCompound, T> listGetter()
	{
		return n ->
		{
			NBTFunnel funnel = new NBTFunnel(n);

			return (T) funnel.getList("l");
		};
	}

	public static <T extends List<? extends NBT>> Function<T, NBTTagCompound> listSetter()
	{
		return l ->
		{
			NBTTagCompound tag = new NBTTagCompound();
			NBTFunnel funnel = new NBTFunnel(tag);

			funnel.setList("l", l);

			return tag;
		};
	}

	/**wha
	 * @return The tag we're reading and writing from.
	 */
	public NBTTagCompound getTag()
	{
		return this.tag;
	}

	public void setStack(final String key, ItemStack stack)
	{
		this.tag.setTag(key, NBTHelper.writeStack(stack));
	}

	public ItemStack getStack(String key)
	{
		return NBTHelper.readStack(this.tag.getCompoundTag(key));
	}

	public void set(final String key, final NBT nbt)
	{
		this.tag.setTag(key, NBTHelper.write(nbt));
	}

	public LocalDateTime getDate(final String key)
	{
		final NBTTagCompound tag = this.tag.getCompoundTag(key);

		if (tag.getBoolean("_null") || !tag.hasKey("_null"))
		{
			return null;
		}

		return LocalDateTime.parse(tag.getString("date"));
	}

	public void setDate(final String key, final LocalDateTime date)
	{
		final NBTTagCompound tag = new NBTTagCompound();

		if (date == null)
		{
			tag.setBoolean("_null", true);

			return;
		}

		tag.setBoolean("_null", false);
		tag.setString("date", date.toString());

		this.tag.setTag(key, tag);
	}

	public <NBT_OBJECT> void setSet(final String key, final Set<NBT_OBJECT> nbtList, @Nullable Function<NBT_OBJECT, NBTTagCompound> setter)
	{
		final NBTTagList writtenObjects = new NBTTagList();

		for (final NBT_OBJECT nbt : nbtList)
		{
			if (setter != null)
			{
				writtenObjects.appendTag(setter.apply(nbt));
			}
			else
			{
				writtenObjects.appendTag(NBTHelper.write((NBT) nbt));
			}
		}

		this.tag.setTag(key, writtenObjects);
	}

	public <NBT_OBJECT> Set<NBT_OBJECT> getSet(final String key, @Nullable Function<NBTTagCompound, NBT_OBJECT> getter)
	{
		final Set<NBT_OBJECT> readObjects = Sets.newHashSet();
		final NBTTagList nbtList = this.tag.getTagList(key, 10);

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			final NBTTagCompound data = nbtList.getCompoundTagAt(i);

			readObjects.add(getter != null ? getter.apply(data) : NBTHelper.read(data));
		}

		return readObjects;
	}

	public <NBT_OBJECT> void set(final String key, NBT_OBJECT nbt, @Nullable Function<NBT_OBJECT, NBTTagCompound> setter)
	{
		final NBTTagCompound tag = new NBTTagCompound();

		if (nbt == null)
		{
			tag.setBoolean("_null", true);
		}
		else
		{
			tag.setBoolean("_null", false);

			if (setter != null)
			{
				tag.setTag("data", setter.apply(nbt));
			}
			else
			{
				tag.setTag("data", NBTHelper.write((NBT) nbt));
			}
		}

		this.tag.setTag(key, tag);
	}

	public <NBT_OBJECT> NBT_OBJECT get(final String key, @Nullable Function<NBTTagCompound, NBT_OBJECT> getter)
	{
		final NBTTagCompound tag = this.tag.getCompoundTag(key);

		if (tag.getBoolean("_null") || !tag.hasKey("_null"))
		{
			return null;
		}

		final NBTTagCompound data = tag.getCompoundTag("data");

		return getter != null ? getter.apply(data) : NBTHelper.read(data);
	}

	public <NBT_OBJECT> NBT_OBJECT getWithDefault(final String key, @Nullable Function<NBTTagCompound, NBT_OBJECT> getter, Supplier<NBT_OBJECT> def)
	{
		final NBTTagCompound tag = this.tag.getCompoundTag(key);

		if (tag.getBoolean("_null") || !tag.hasKey("_null"))
		{
			return def.get();
		}

		final NBTTagCompound data = tag.getCompoundTag("data");

		return getter != null ? getter.apply(data) : NBTHelper.read(data);
	}

	public <T extends NBTMeta> T loadWithoutReading(final String key)
	{
		return NBTHelper.loadWithoutReading(this.tag.getCompoundTag(key));
	}

	public <T extends NBT> T get(final String key)
	{
		return NBTHelper.read(this.tag.getCompoundTag(key));
	}

	public <T extends NBT> T getWithDefault(final String key, Supplier<T> def)
	{
		return NBTHelper.readWithDefault(this.tag.getCompoundTag(key), def);
	}

	public <T extends NBT> T get(final World world, final String key)
	{
		return NBTHelper.read(world, this.tag.getCompoundTag(key));
	}

	public <T extends NBT> T getWithDefault(final World world, final String key, Supplier<T> def)
	{
		return NBTHelper.readWithDefault(world, this.tag.getCompoundTag(key), def);
	}

	public void setPos(final String key, final BlockPos pos)
	{
		this.tag.setTag(key, NBTHelper.writeBlockPos(pos));
	}

	public BlockPos getPos(final String key)
	{
		return NBTHelper.readBlockPos(this.tag.getCompoundTag(key));
	}

	public void setIntToStringMap(final String key, final Map<Integer, String> nbtMap)
	{
		final NBTTagList writtenKeys = new NBTTagList();
		final NBTTagList writtenObjects = new NBTTagList();

		for (final Map.Entry<Integer, String> entrySet : nbtMap.entrySet())
		{
			final int intKey = entrySet.getKey();
			final String string = entrySet.getValue();

			writtenKeys.appendTag(new NBTTagInt(intKey));
			writtenObjects.appendTag(new NBTTagString(string));
		}

		this.tag.setTag(key + "_keys", writtenKeys);
		this.tag.setTag(key + "_obj", writtenObjects);
	}

	public Map<Integer, String> getIntToStringMap(final String key)
	{
		final Map<Integer, String> readObjects = Maps.newHashMap();

		final NBTTagList keys = this.tag.getTagList(key + "_keys", 3);
		final NBTTagList objects = this.tag.getTagList(key + "_obj", 8);

		for (int i = 0; i < keys.tagCount(); i++)
		{
			final int intKey = keys.getIntAt(i);
			final String data = objects.getStringTagAt(i);

			readObjects.put(intKey, data);
		}

		return readObjects;
	}

	private <T extends NBT> Map<String, T> getStringMapInner(final World world, final String key)
	{
		final Map<String, T> readObjects = Maps.newHashMap();

		final NBTTagList keys = this.tag.getTagList(key + "_keys", 8);
		final NBTTagList objects = this.tag.getTagList(key + "_obj", 10);

		for (int i = 0; i < keys.tagCount(); i++)
		{
			final String stringKey = keys.getStringTagAt(i);
			final NBTTagCompound data = objects.getCompoundTagAt(i);

			readObjects.put(stringKey, world == null ? NBTHelper.read(data) : NBTHelper.read(world, data));
		}

		return readObjects;
	}

	public <T extends NBT> Map<String, T> getStringMap(final String key)
	{
		return this.getStringMapInner(null, key);
	}

	public <T extends NBT> Map<String, T> getStringMap(final World world, final String key)
	{
		return this.getStringMapInner(world, key);
	}

	public void setMap(final String key, final Map<? extends NBT, ? extends NBT> nbtMap)
	{
		final NBTTagList writtenKeys = new NBTTagList();
		final NBTTagList writtenObjects = new NBTTagList();

		for (final Map.Entry<? extends NBT, ? extends NBT> entrySet : nbtMap.entrySet())
		{
			final NBT keyNBT = entrySet.getKey();
			final NBT valueNBT = entrySet.getValue();

			writtenKeys.appendTag(NBTHelper.write(keyNBT));
			writtenObjects.appendTag(NBTHelper.write(valueNBT));
		}

		this.tag.setTag(key + "_keys", writtenKeys);
		this.tag.setTag(key + "_obj", writtenObjects);
	}

	public <R> void setLongMap(final String key, final TLongObjectMap<R> nbtMap,
			@Nullable Function<R, NBTTagCompound> rightSetter)
	{
		final NBTTagList writtenKeys = new NBTTagList();
		final NBTTagList writtenObjects = new NBTTagList();

		nbtMap.forEachEntry((chunkPos, valueNBT) ->
		{
			writtenKeys.appendTag(new NBTTagLong(chunkPos));

			if (rightSetter != null)
			{
				writtenObjects.appendTag(rightSetter.apply(valueNBT));
			}
			else
			{
				writtenObjects.appendTag(NBTHelper.write((NBT) valueNBT));
			}

			return true;
		});

		this.tag.setTag(key + "_keys", writtenKeys);
		this.tag.setTag(key + "_obj", writtenObjects);
	}

	public <R> TLongObjectMap<R> getLongMap(final String key, @Nullable Function<NBTTagCompound, R> rightGetter)
	{
		final TLongObjectMap<R> readObjects = new TLongObjectHashMap<>();

		final NBTTagList keys = this.tag.getTagList(key + "_keys", 4);
		final NBTTagList objects = this.tag.getTagList(key + "_obj", 10);

		for (int i = 0; i < keys.tagCount(); i++)
		{
			final NBTTagLong keyData = (NBTTagLong) keys.get(i);
			final NBTTagCompound valueData = objects.getCompoundTagAt(i);

			readObjects.put(keyData.getLong(), rightGetter != null ? rightGetter.apply(valueData) : NBTHelper.read(valueData));
		}

		return readObjects;
	}

	public <L, R> void setMap(final String key, final Map<L, R> nbtMap, @Nullable Function<L, NBTTagCompound> leftSetter,
			@Nullable Function<R, NBTTagCompound> rightSetter)
	{
		final NBTTagList writtenKeys = new NBTTagList();
		final NBTTagList writtenObjects = new NBTTagList();

		for (final Map.Entry<L, R> entrySet : nbtMap.entrySet())
		{
			final L keyNBT = entrySet.getKey();
			final R valueNBT = entrySet.getValue();

			if (leftSetter != null)
			{
				writtenKeys.appendTag(leftSetter.apply(keyNBT));
			}
			else
			{
				writtenKeys.appendTag(NBTHelper.write((NBT) keyNBT));
			}

			if (rightSetter != null)
			{
				writtenObjects.appendTag(rightSetter.apply(valueNBT));
			}
			else
			{
				writtenObjects.appendTag(NBTHelper.write((NBT) valueNBT));
			}
		}

		this.tag.setTag(key + "_keys", writtenKeys);
		this.tag.setTag(key + "_obj", writtenObjects);
	}

	public <L, R> Map<L, R> getMap(final String key, @Nullable Function<NBTTagCompound, L> leftGetter,
			@Nullable Function<NBTTagCompound, R> rightGetter)
	{
		final Map<L, R> readObjects = Maps.newHashMap();

		final NBTTagList keys = this.tag.getTagList(key + "_keys", 10);
		final NBTTagList objects = this.tag.getTagList(key + "_obj", 10);

		for (int i = 0; i < keys.tagCount(); i++)
		{
			final NBTTagCompound keyData = keys.getCompoundTagAt(i);
			final NBTTagCompound valueData = objects.getCompoundTagAt(i);

			readObjects.put(leftGetter != null ? leftGetter.apply(keyData) : NBTHelper.read(keyData),
					rightGetter != null ? rightGetter.apply(valueData) : NBTHelper.read(valueData));
		}

		return readObjects;
	}

	private <K extends NBT, T extends NBT> Map<K, T> getMapInner(final World world, final String key)
	{
		final Map<K, T> readObjects = Maps.newHashMap();

		final NBTTagList keys = this.tag.getTagList(key + "_keys", 10);
		final NBTTagList objects = this.tag.getTagList(key + "_obj", 10);

		for (int i = 0; i < keys.tagCount(); i++)
		{
			final NBTTagCompound keyData = keys.getCompoundTagAt(i);
			final NBTTagCompound valueData = objects.getCompoundTagAt(i);

			readObjects.put(world == null ? NBTHelper.read(keyData) : NBTHelper.read(world, keyData),
					world == null ? NBTHelper.read(valueData) : NBTHelper.read(world, valueData));
		}

		return readObjects;
	}

	public <K extends NBT, T extends NBT> Map<K, T> getMap(final String key)
	{
		return this.getMapInner(null, key);
	}

	public <K extends NBT, T extends NBT> Map<K, T> getMap(final World world, final String key)
	{
		return this.getMapInner(world, key);
	}

	public void setStringMap(final String key, final Map<String, ? extends NBT> nbtMap)
	{
		final NBTTagList writtenKeys = new NBTTagList();
		final NBTTagList writtenObjects = new NBTTagList();

		for (final Map.Entry<String, ? extends NBT> entrySet : nbtMap.entrySet())
		{
			final String stringKey = entrySet.getKey();
			final NBT nbt = entrySet.getValue();

			writtenKeys.appendTag(new NBTTagString(stringKey));
			writtenObjects.appendTag(NBTHelper.write(nbt));
		}

		this.tag.setTag(key + "_keys", writtenKeys);
		this.tag.setTag(key + "_obj", writtenObjects);
	}

	public void setIntMap(final String key, final Map<Integer, ? extends NBT> nbtMap)
	{
		final NBTTagList writtenKeys = new NBTTagList();
		final NBTTagList writtenObjects = new NBTTagList();

		for (final Map.Entry<Integer, ? extends NBT> entrySet : nbtMap.entrySet())
		{
			final int intKey = entrySet.getKey();
			final NBT nbt = entrySet.getValue();

			writtenKeys.appendTag(new NBTTagInt(intKey));
			writtenObjects.appendTag(NBTHelper.write(nbt));
		}

		this.tag.setTag(key + "_keys", writtenKeys);
		this.tag.setTag(key + "_obj", writtenObjects);
	}

	private <T extends NBT> Map<Integer, T> getIntMapInner(final World world, final String key)
	{
		final Map<Integer, T> readObjects = Maps.newHashMap();

		final NBTTagList keys = this.tag.getTagList(key + "_keys", 3);
		final NBTTagList objects = this.tag.getTagList(key + "_obj", 10);

		for (int i = 0; i < keys.tagCount(); i++)
		{
			final int intKey = keys.getIntAt(i);
			final NBTTagCompound data = objects.getCompoundTagAt(i);

			readObjects.put(intKey, world == null ? NBTHelper.read(data) : NBTHelper.read(world, data));
		}

		return readObjects;
	}

	public <T extends NBT> Map<Integer, T> getIntMap(final String key)
	{
		return this.getIntMapInner(null, key);
	}

	public <T extends NBT> Map<Integer, T> getIntMap(final World world, final String key)
	{
		return this.getIntMapInner(world, key);
	}

	public void setPairList(final String key, final List<? extends Pair<? extends NBT, ? extends NBT>> nbtList)
	{
		final NBTTagList writtenObjects = new NBTTagList();

		for (final Pair<? extends NBT, ? extends NBT> pair : nbtList)
		{
			writtenObjects.appendTag(NBTHelper.write(pair.getLeft()));
			writtenObjects.appendTag(NBTHelper.write(pair.getRight()));
		}

		this.tag.setTag(key, writtenObjects);
	}

	public <T extends Pair, L extends NBT, R extends NBT> List<T> getPairList(final World world, final String key)
	{
		final List<Pair> readObjects = Lists.newArrayList();
		final NBTTagList nbtList = this.tag.getTagList(key, 10);

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			final NBTTagCompound leftData = nbtList.getCompoundTagAt(i);
			final NBTTagCompound rightData = nbtList.getCompoundTagAt(i++);

			final L left = NBTHelper.read(world, leftData);
			final R right = NBTHelper.read(world, rightData);

			readObjects.add(Pair.of(left, right));
		}

		return (List<T>) readObjects;
	}

	public <T extends Pair, L extends NBT, R extends NBT> List<T> getPairList(final String key)
	{
		final List<Pair> readObjects = Lists.newArrayList();
		final NBTTagList nbtList = this.tag.getTagList(key, 10);

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			final NBTTagCompound leftData = nbtList.getCompoundTagAt(i);
			final NBTTagCompound rightData = nbtList.getCompoundTagAt(++i);

			final L left = NBTHelper.read(leftData);
			final R right = NBTHelper.read(rightData);

			readObjects.add(Pair.of(left, right));
		}

		return (List<T>) readObjects;
	}

	public void setSet(final String key, final Set<? extends NBT> nbtList)
	{
		final NBTTagList writtenObjects = new NBTTagList();

		for (final NBT nbt : nbtList)
		{
			writtenObjects.appendTag(NBTHelper.write(nbt));
		}

		this.tag.setTag(key, writtenObjects);
	}

	public <T extends NBT> Set<T> getSet(final World world, final String key)
	{
		final Set<T> readObjects = Sets.newHashSet();
		final NBTTagList nbtList = this.tag.getTagList(key, 10);

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			final NBTTagCompound data = nbtList.getCompoundTagAt(i);

			readObjects.add(NBTHelper.read(world, data));
		}

		return readObjects;
	}

	public <T extends NBT> Set<T> getSet(final String key)
	{
		final Set<T> readObjects = Sets.newHashSet();
		final NBTTagList nbtList = this.tag.getTagList(key, 10);

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			final NBTTagCompound data = nbtList.getCompoundTagAt(i);

			readObjects.add(NBTHelper.read(data));
		}

		return readObjects;
	}

	public void setStackList(final String key, final List<ItemStack> stackList)
	{
		final NBTTagList writtenObjects = new NBTTagList();

		for (final ItemStack stack : stackList)
		{
			NBTTagCompound tag = new NBTTagCompound();

			stack.writeToNBT(tag);

			writtenObjects.appendTag(tag);
		}

		this.tag.setTag(key, writtenObjects);
	}

	public List<ItemStack> getStackList(final String key)
	{
		final List<ItemStack> readObjects = Lists.newArrayList();
		final NBTTagList nbtList = this.tag.getTagList(key, 10);

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			final NBTTagCompound data = nbtList.getCompoundTagAt(i);

			readObjects.add(new ItemStack(data));
		}

		return readObjects;
	}

	public <NBT_OBJECT> void setList(final String key, final List<NBT_OBJECT> nbtList, @Nullable Function<NBT_OBJECT, NBTTagCompound> setter)
	{
		final NBTTagList writtenObjects = new NBTTagList();

		for (final NBT_OBJECT nbt : nbtList)
		{
			if (setter != null)
			{
				writtenObjects.appendTag(setter.apply(nbt));
			}
			else
			{
				writtenObjects.appendTag(NBTHelper.write((NBT) nbt));
			}
		}

		this.tag.setTag(key, writtenObjects);
	}

	public <NBT_OBJECT> void setArray(final String key, final NBT_OBJECT[] array, @Nullable Function<NBT_OBJECT, NBTTagCompound> setter)
	{
		final boolean nul = array == null;

		this.tag.setBoolean(key + "_null", nul);

		if (nul)
		{
			return;
		}

		final NBTTagList writtenObjects = new NBTTagList();

		for (final NBT_OBJECT obj : array)
		{
			if (setter != null)
			{
				writtenObjects.appendTag(setter.apply(obj));
			}
			else
			{
				writtenObjects.appendTag(NBTHelper.write((NBT) obj));
			}
		}

		this.tag.setTag(key, writtenObjects);
	}

	public <NBT_OBJECT> NBT_OBJECT[] getArray(final String key, final Class<? extends NBT_OBJECT> clazz, @Nullable Function<NBTTagCompound, NBT_OBJECT> getter)
	{
		if (this.tag.getBoolean(key + "_null"))
		{
			return null;
		}

		final NBTTagList nbtList = this.tag.getTagList(key, 10);
		final NBT_OBJECT[] readObjects = (NBT_OBJECT[]) Array.newInstance(clazz, nbtList.tagCount());

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			readObjects[i] = getter != null ? getter.apply(nbtList.getCompoundTagAt(i)) : NBTHelper.read(nbtList.getCompoundTagAt(i));
		}

		return readObjects;
	}

	public <NBT_OBJECT> List<NBT_OBJECT> getList(final String key, @Nullable Function<NBTTagCompound, NBT_OBJECT> getter)
	{
		final List<NBT_OBJECT> readObjects = Lists.newArrayList();
		final NBTTagList nbtList = this.tag.getTagList(key, 10);

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			final NBTTagCompound data = nbtList.getCompoundTagAt(i);

			readObjects.add(getter != null ? getter.apply(data) : NBTHelper.read(data));
		}

		return readObjects;
	}

	public void setList(final String key, final List<? extends NBT> nbtList)
	{
		final NBTTagList writtenObjects = new NBTTagList();

		for (final NBT nbt : nbtList)
		{
			writtenObjects.appendTag(NBTHelper.write(nbt));
		}

		this.tag.setTag(key, writtenObjects);
	}

	public <T extends NBT> List<T> getList(final World world, final String key)
	{
		final List<T> readObjects = Lists.newArrayList();
		final NBTTagList nbtList = this.tag.getTagList(key, 10);

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			final NBTTagCompound data = nbtList.getCompoundTagAt(i);

			readObjects.add(NBTHelper.read(world, data));
		}

		return readObjects;
	}

	public <T extends NBT> List<T> getList(final String key)
	{
		final List<T> readObjects = Lists.newArrayList();
		final NBTTagList nbtList = this.tag.getTagList(key, 10);

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			final NBTTagCompound data = nbtList.getCompoundTagAt(i);

			readObjects.add(NBTHelper.read(data));
		}

		return readObjects;
	}

	public List<String> getStringList(final String key)
	{
		final List<String> readObjects = Lists.newArrayList();
		final NBTTagList nbtList = this.tag.getTagList(key, 8);

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			readObjects.add(nbtList.getStringTagAt(i));
		}

		return readObjects;
	}

	public void setStringList(final String key, final List<String> stringList)
	{
		final NBTTagList writtenObjects = new NBTTagList();

		for (final String string : stringList)
		{
			writtenObjects.appendTag(new NBTTagString(string));
		}

		this.tag.setTag(key, writtenObjects);
	}

	public void setBooleanArray(final String key, final boolean[] array)
	{
		final NBTTagList writtenObjects = new NBTTagList();

		for (final boolean bool : array)
		{
			writtenObjects.appendTag(new NBTTagByte((byte) (bool ? 1 : 0)));
		}

		this.tag.setTag(key, writtenObjects);
	}

	public boolean[] getBooleanArray(final String key)
	{
		final NBTTagList nbtList = this.tag.getTagList(key, 1);
		final boolean[] readObjects = new boolean[nbtList.tagCount()];

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			readObjects[i] = (((NBTTagByte) nbtList.get(i)).getByte()) == 1;
		}

		return readObjects;
	}

	public <T extends Enum> void setEnumArray(final String key, final T[] array)
	{
		final boolean nul = array == null;

		this.tag.setBoolean(key + "_null", nul);

		if (nul)
		{
			return;
		}

		final NBTTagList writtenObjects = new NBTTagList();

		for (final T obj : array)
		{
			writtenObjects.appendTag(new NBTTagString(obj.name()));
		}

		this.tag.setTag(key, writtenObjects);
	}

	public <T extends NBT> void setArray(final String key, final T[] array)
	{
		final boolean nul = array == null;

		this.tag.setBoolean(key + "_null", nul);

		if (nul)
		{
			return;
		}

		final NBTTagList writtenObjects = new NBTTagList();

		for (final T obj : array)
		{
			writtenObjects.appendTag(NBTHelper.write(obj));
		}

		this.tag.setTag(key, writtenObjects);
	}

	public String[] getEnumArrayNames(final String key)
	{
		if (this.tag.getBoolean(key + "_null"))
		{
			return null;
		}

		final NBTTagList nbtList = this.tag.getTagList(key, 8);
		final String[] readObjects = new String[nbtList.tagCount()];

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			readObjects[i] = nbtList.getStringTagAt(i);
		}

		return readObjects;
	}

	public <T extends NBT> T[] getArray(final String key, final Class<? extends T> clazz)
	{
		if (this.tag.getBoolean(key + "_null"))
		{
			return null;
		}

		final NBTTagList nbtList = this.tag.getTagList(key, 10);
		final T[] readObjects = (T[]) Array.newInstance(clazz, nbtList.tagCount());

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			readObjects[i] = NBTHelper.read(nbtList.getCompoundTagAt(i));
		}

		return readObjects;
	}

	public void setIntArray(final String key, final int[] array)
	{
		final NBTTagList writtenObjects = new NBTTagList();

		for (final int obj : array)
		{
			writtenObjects.appendTag(new NBTTagInt(obj));
		}

		this.tag.setTag(key, writtenObjects);
	}

	public int[] getIntArray(final String key)
	{
		final NBTTagList nbtList = this.tag.getTagList(key, 3);
		final int[] readObjects = new int[nbtList.tagCount()];

		for (int i = 0; i < nbtList.tagCount(); i++)
		{
			readObjects[i] = nbtList.getIntAt(i);
		}

		return readObjects;
	}

	public void setBlockPos(final String key, BlockPos pos)
	{
		NBTBase tag = NBTHelper.writeBlockPos(pos);
		this.tag.setTag(key, tag);
	}

	public BlockPos getBlockPos(final String key)
	{
		NBTTagCompound tag = this.tag.getCompoundTag(key);
		return NBTHelper.readBlockPos(tag);
	}

}
