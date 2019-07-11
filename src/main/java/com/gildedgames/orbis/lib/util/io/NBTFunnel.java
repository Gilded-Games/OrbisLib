package com.gildedgames.orbis.lib.util.io;

import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.util.mc.NBT;
import com.gildedgames.orbis.lib.util.mc.NBTHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.IStringSerializable;
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

	public static Function<BlockState, CompoundNBT> BLOCKSTATE_SETTER = NBTUtil::writeBlockState;

	public static Function<CompoundNBT, BlockState> BLOCKSTATE_GETTER = NBTUtil::readBlockState;

	public static Function<ItemStack, CompoundNBT> STACK_SETTER = o ->
	{
		CompoundNBT tag = new CompoundNBT();

		tag.put("s", NBTHelper.writeStack(o));

		return tag;
	};

	public static Function<CompoundNBT, ItemStack> STACK_GETTER = n -> NBTHelper.readStack(n.getCompound("s"));

	public static Function<UUID, CompoundNBT> UUID_SETTER = o ->
	{
		CompoundNBT tag = new CompoundNBT();

		tag.putUniqueId("u", o);

		return tag;
	};

	public static Function<CompoundNBT, UUID> UUID_GETTER = n -> n.getUniqueId("u");

	public static Function<String, CompoundNBT> STRING_SETTER = o ->
	{
		CompoundNBT f = new CompoundNBT();

		f.putString("s", o);

		return f;
	};

	public static Function<CompoundNBT, String> STRING_GETTER = n -> n.getString("s");

	public static Function<Boolean, CompoundNBT> BOOLEAN_SETTER = o ->
	{
		CompoundNBT f = new CompoundNBT();

		f.putBoolean("b", o);

		return f;
	};

	public static Function<CompoundNBT, Boolean> BOOLEAN_GETTER = n -> n.getBoolean("b");

	public static Function<Integer, CompoundNBT> INTEGER_SETTER = o ->
	{
		CompoundNBT f = new CompoundNBT();

		f.putInt("i", o);

		return f;
	};

	public static Function<CompoundNBT, Integer> INTEGER_GETTER = n -> n.getInt("i");

	public static Function<ResourceLocation, CompoundNBT> LOC_SETTER = o ->
	{
		CompoundNBT f = new CompoundNBT();

		f.putString("loc", o.toString());

		return f;
	};

	public static Function<CompoundNBT, ResourceLocation> LOC_GETTER = n -> new ResourceLocation(n.getString("loc"));

	public static Function<BlockPos, CompoundNBT> POS_SETTER = o ->
	{
		NBTFunnel f = new NBTFunnel(new CompoundNBT());

		f.setPos("p", o);

		return f.getTag();
	};

	public static Function<CompoundNBT, BlockPos> POS_GETTER = n ->
	{
		NBTFunnel f = new NBTFunnel(n);

		return f.getPos("p");
	};

	public static Function<Pos2D, CompoundNBT> POS2D_SETTER = o ->
	{
		CompoundNBT tag = new CompoundNBT();

		tag.putFloat("x", o.x());
		tag.putFloat("y", o.y());

		return tag;
	};

	public static Function<CompoundNBT, Pos2D> POS2D_GETTER = n -> Pos2D.flush(n.getFloat("x"), n.getFloat("y"));

	public static Function<ChunkPos, CompoundNBT> CHUNK_POS_SETTER = o ->
	{
		CompoundNBT tag = new CompoundNBT();

		tag.putInt("x", o.x);
		tag.putInt("z", o.z);

		return tag;
	};

	public static Function<CompoundNBT, ChunkPos> CHUNK_POS_GETTER = n -> new ChunkPos(n.getInt("x"), n.getInt("z"));

	private final CompoundNBT tag;

	public NBTFunnel(final CompoundNBT tag)
	{
		this.tag = tag;
	}

	public static <T extends NBT> Function<CompoundNBT, T> getter()
	{
		return n ->
		{
			NBTFunnel funnel = new NBTFunnel(n);

			return funnel.get("o");
		};
	}

	public static <T extends NBT> Function<T, CompoundNBT> setter()
	{
		return o ->
		{
			CompoundNBT tag = new CompoundNBT();
			NBTFunnel funnel = new NBTFunnel(tag);

			funnel.set("o", o);

			return tag;
		};
	}

	public static <T extends List<? extends NBT>> Function<CompoundNBT, T> listGetter()
	{
		return n ->
		{
			NBTFunnel funnel = new NBTFunnel(n);

			return (T) funnel.getList("l");
		};
	}

	public static <T extends List<? extends NBT>> Function<T, CompoundNBT> listSetter()
	{
		return l ->
		{
			CompoundNBT tag = new CompoundNBT();
			NBTFunnel funnel = new NBTFunnel(tag);

			funnel.setList("l", l);

			return tag;
		};
	}

	/**wha
	 * @return The tag we're reading and writing from.
	 */
	public CompoundNBT getTag()
	{
		return this.tag;
	}

	public void setStack(final String key, ItemStack stack)
	{
		this.tag.put(key, NBTHelper.writeStack(stack));
	}

	public ItemStack getStack(String key)
	{
		return NBTHelper.readStack(this.tag.getCompound(key));
	}

	public void set(final String key, final NBT nbt)
	{
		this.tag.put(key, NBTHelper.write(nbt));
	}

	public LocalDateTime getDate(final String key)
	{
		final CompoundNBT tag = this.tag.getCompound(key);

		if (tag.getBoolean("_null") || !tag.contains("_null"))
		{
			return null;
		}

		return LocalDateTime.parse(tag.getString("date"));
	}

	public void setDate(final String key, final LocalDateTime date)
	{
		final CompoundNBT tag = new CompoundNBT();

		if (date == null)
		{
			tag.putBoolean("_null", true);

			return;
		}

		tag.putBoolean("_null", false);
		tag.putString("date", date.toString());

		this.tag.put(key, tag);
	}

	public <NBT_OBJECT> void setSet(final String key, final Set<NBT_OBJECT> nbtList, @Nullable Function<NBT_OBJECT, CompoundNBT> setter)
	{
		final ListNBT writtenObjects = new ListNBT();

		for (final NBT_OBJECT nbt : nbtList)
		{
			if (setter != null)
			{
				writtenObjects.add(setter.apply(nbt));
			}
			else
			{
				writtenObjects.add(NBTHelper.write((NBT) nbt));
			}
		}

		this.tag.put(key, writtenObjects);
	}

	public <NBT_OBJECT> Set<NBT_OBJECT> getSet(final String key, @Nullable Function<CompoundNBT, NBT_OBJECT> getter)
	{
		final Set<NBT_OBJECT> readObjects = Sets.newHashSet();
		final ListNBT nbtList = this.tag.getList(key, 10);

		for (int i = 0; i < nbtList.size(); i++)
		{
			final CompoundNBT data = nbtList.getCompound(i);

			readObjects.add(getter != null ? getter.apply(data) : NBTHelper.read(data));
		}

		return readObjects;
	}

	public <NBT_OBJECT> void set(final String key, NBT_OBJECT nbt, @Nullable Function<NBT_OBJECT, CompoundNBT> setter)
	{
		final CompoundNBT tag = new CompoundNBT();

		if (nbt == null)
		{
			tag.putBoolean("_null", true);
		}
		else
		{
			tag.putBoolean("_null", false);

			if (setter != null)
			{
				tag.put("data", setter.apply(nbt));
			}
			else
			{
				tag.put("data", NBTHelper.write((NBT) nbt));
			}
		}

		this.tag.put(key, tag);
	}

	public <NBT_OBJECT> NBT_OBJECT get(final String key, @Nullable Function<CompoundNBT, NBT_OBJECT> getter)
	{
		final CompoundNBT tag = this.tag.getCompound(key);

		if (tag.getBoolean("_null") || !tag.contains("_null"))
		{
			return null;
		}

		final CompoundNBT data = tag.getCompound("data");

		return getter != null ? getter.apply(data) : NBTHelper.read(data);
	}

	public <NBT_OBJECT> NBT_OBJECT getWithDefault(final String key, @Nullable Function<CompoundNBT, NBT_OBJECT> getter, Supplier<NBT_OBJECT> def)
	{
		final CompoundNBT tag = this.tag.getCompound(key);

		if (tag.getBoolean("_null") || !tag.contains("_null"))
		{
			return def.get();
		}

		final CompoundNBT data = tag.getCompound("data");

		return getter != null ? getter.apply(data) : NBTHelper.read(data);
	}

	public <T extends NBT> T get(final String key)
	{
		return NBTHelper.read(this.tag.getCompound(key));
	}

	public <T extends NBT> T getWithDefault(final String key, Supplier<T> def)
	{
		return NBTHelper.readWithDefault(this.tag.getCompound(key), def);
	}

	public <T extends NBT> T get(final World world, final String key)
	{
		return NBTHelper.read(world, this.tag.getCompound(key));
	}

	public <T extends NBT> T getWithDefault(final World world, final String key, Supplier<T> def)
	{
		return NBTHelper.readWithDefault(world, this.tag.getCompound(key), def);
	}

	public void setPos(final String key, final BlockPos pos)
	{
		this.tag.put(key, NBTHelper.writeBlockPos(pos));
	}

	public BlockPos getPos(final String key)
	{
		return NBTHelper.readBlockPos(this.tag.getCompound(key));
	}

	public void setIntToStringMap(final String key, final Map<Integer, String> nbtMap)
	{
		final ListNBT writtenKeys = new ListNBT();
		final ListNBT writtenObjects = new ListNBT();

		for (final Map.Entry<Integer, String> entrySet : nbtMap.entrySet())
		{
			final int intKey = entrySet.getKey();
			final String string = entrySet.getValue();

			writtenKeys.add(new IntNBT(intKey));
			writtenObjects.add(new StringNBT(string));
		}

		this.tag.put(key + "_keys", writtenKeys);
		this.tag.put(key + "_obj", writtenObjects);
	}

	public Map<Integer, String> getIntToStringMap(final String key)
	{
		final Map<Integer, String> readObjects = Maps.newHashMap();

		final ListNBT keys = this.tag.getList(key + "_keys", 3);
		final ListNBT objects = this.tag.getList(key + "_obj", 8);

		for (int i = 0; i < keys.size(); i++)
		{
			final int intKey = keys.getInt(i);
			final String data = objects.getString(i);

			readObjects.put(intKey, data);
		}

		return readObjects;
	}

	private <T extends NBT> Map<String, T> getStringMapInner(final World world, final String key)
	{
		final Map<String, T> readObjects = Maps.newHashMap();

		final ListNBT keys = this.tag.getList(key + "_keys", 8);
		final ListNBT objects = this.tag.getList(key + "_obj", 10);

		for (int i = 0; i < keys.size(); i++)
		{
			final String stringKey = keys.getString(i);
			final CompoundNBT data = objects.getCompound(i);

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
		final ListNBT writtenKeys = new ListNBT();
		final ListNBT writtenObjects = new ListNBT();

		for (final Map.Entry<? extends NBT, ? extends NBT> entrySet : nbtMap.entrySet())
		{
			final NBT keyNBT = entrySet.getKey();
			final NBT valueNBT = entrySet.getValue();

			writtenKeys.add(NBTHelper.write(keyNBT));
			writtenObjects.add(NBTHelper.write(valueNBT));
		}

		this.tag.put(key + "_keys", writtenKeys);
		this.tag.put(key + "_obj", writtenObjects);
	}

	public <R> void setLongMap(final String key, final Long2ObjectOpenHashMap<R> nbtMap,
			@Nullable Function<R, CompoundNBT> rightSetter)
	{
		final ListNBT writtenKeys = new ListNBT();
		final ListNBT writtenObjects = new ListNBT();

		nbtMap.long2ObjectEntrySet().forEach((entry) ->
		{
			final long chunkPos = entry.getLongKey();
			final R valueNBT = entry.getValue();

			writtenKeys.add(new LongNBT(chunkPos));

			if (rightSetter != null)
			{
				writtenObjects.add(rightSetter.apply(valueNBT));
			}
			else
			{
				writtenObjects.add(NBTHelper.write((NBT) valueNBT));
			}
		});

		this.tag.put(key + "_keys", writtenKeys);
		this.tag.put(key + "_obj", writtenObjects);
	}

	public <R> Long2ObjectOpenHashMap<R> getLongMap(final String key, @Nullable Function<CompoundNBT, R> rightGetter)
	{
		final Long2ObjectOpenHashMap<R> readObjects = new Long2ObjectOpenHashMap<>();

		final ListNBT keys = this.tag.getList(key + "_keys", 4);
		final ListNBT objects = this.tag.getList(key + "_obj", 10);

		for (int i = 0; i < keys.size(); i++)
		{
			final LongNBT keyData = (LongNBT) keys.get(i);
			final CompoundNBT valueData = objects.getCompound(i);

			readObjects.put(keyData.getLong(), rightGetter != null ? rightGetter.apply(valueData) : NBTHelper.read(valueData));
		}

		return readObjects;
	}

	public <L, R> void setMap(final String key, final Map<L, R> nbtMap, @Nullable Function<L, CompoundNBT> leftSetter,
			@Nullable Function<R, CompoundNBT> rightSetter)
	{
		final ListNBT writtenKeys = new ListNBT();
		final ListNBT writtenObjects = new ListNBT();

		for (final Map.Entry<L, R> entrySet : nbtMap.entrySet())
		{
			final L keyNBT = entrySet.getKey();
			final R valueNBT = entrySet.getValue();

			if (leftSetter != null)
			{
				writtenKeys.add(leftSetter.apply(keyNBT));
			}
			else
			{
				writtenKeys.add(NBTHelper.write((NBT) keyNBT));
			}

			if (rightSetter != null)
			{
				writtenObjects.add(rightSetter.apply(valueNBT));
			}
			else
			{
				writtenObjects.add(NBTHelper.write((NBT) valueNBT));
			}
		}

		this.tag.put(key + "_keys", writtenKeys);
		this.tag.put(key + "_obj", writtenObjects);
	}

	public <L, R> Map<L, R> getMap(final String key, @Nullable Function<CompoundNBT, L> leftGetter,
			@Nullable Function<CompoundNBT, R> rightGetter)
	{
		final Map<L, R> readObjects = Maps.newHashMap();

		final ListNBT keys = this.tag.getList(key + "_keys", 10);
		final ListNBT objects = this.tag.getList(key + "_obj", 10);

		for (int i = 0; i < keys.size(); i++)
		{
			final CompoundNBT keyData = keys.getCompound(i);
			final CompoundNBT valueData = objects.getCompound(i);

			readObjects.put(leftGetter != null ? leftGetter.apply(keyData) : NBTHelper.read(keyData),
					rightGetter != null ? rightGetter.apply(valueData) : NBTHelper.read(valueData));
		}

		return readObjects;
	}

	private <K extends NBT, T extends NBT> Map<K, T> getMapInner(final World world, final String key)
	{
		final Map<K, T> readObjects = Maps.newHashMap();

		final ListNBT keys = this.tag.getList(key + "_keys", 10);
		final ListNBT objects = this.tag.getList(key + "_obj", 10);

		for (int i = 0; i < keys.size(); i++)
		{
			final CompoundNBT keyData = keys.getCompound(i);
			final CompoundNBT valueData = objects.getCompound(i);

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
		final ListNBT writtenKeys = new ListNBT();
		final ListNBT writtenObjects = new ListNBT();

		for (final Map.Entry<String, ? extends NBT> entrySet : nbtMap.entrySet())
		{
			final String stringKey = entrySet.getKey();
			final NBT nbt = entrySet.getValue();

			writtenKeys.add(new StringNBT(stringKey));
			writtenObjects.add(NBTHelper.write(nbt));
		}

		this.tag.put(key + "_keys", writtenKeys);
		this.tag.put(key + "_obj", writtenObjects);
	}

	public void setIntMap(final String key, final Map<Integer, ? extends NBT> nbtMap)
	{
		final ListNBT writtenKeys = new ListNBT();
		final ListNBT writtenObjects = new ListNBT();

		for (final Map.Entry<Integer, ? extends NBT> entrySet : nbtMap.entrySet())
		{
			final int intKey = entrySet.getKey();
			final NBT nbt = entrySet.getValue();

			writtenKeys.add(new IntNBT(intKey));
			writtenObjects.add(NBTHelper.write(nbt));
		}

		this.tag.put(key + "_keys", writtenKeys);
		this.tag.put(key + "_obj", writtenObjects);
	}

	private <T extends NBT> Map<Integer, T> getIntMapInner(final World world, final String key)
	{
		final Map<Integer, T> readObjects = Maps.newHashMap();

		final ListNBT keys = this.tag.getList(key + "_keys", 3);
		final ListNBT objects = this.tag.getList(key + "_obj", 10);

		for (int i = 0; i < keys.size(); i++)
		{
			final int intKey = keys.getInt(i);
			final CompoundNBT data = objects.getCompound(i);

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
		final ListNBT writtenObjects = new ListNBT();

		for (final Pair<? extends NBT, ? extends NBT> pair : nbtList)
		{
			writtenObjects.add(NBTHelper.write(pair.getLeft()));
			writtenObjects.add(NBTHelper.write(pair.getRight()));
		}

		this.tag.put(key, writtenObjects);
	}

	public <T extends Pair, L extends NBT, R extends NBT> List<T> getPairList(final World world, final String key)
	{
		final List<Pair> readObjects = Lists.newArrayList();
		final ListNBT nbtList = this.tag.getList(key, 10);

		for (int i = 0; i < nbtList.size(); i++)
		{
			final CompoundNBT leftData = nbtList.getCompound(i);
			final CompoundNBT rightData = nbtList.getCompound(i++);

			final L left = NBTHelper.read(world, leftData);
			final R right = NBTHelper.read(world, rightData);

			readObjects.add(Pair.of(left, right));
		}

		return (List<T>) readObjects;
	}

	public <T extends Pair, L extends NBT, R extends NBT> List<T> getPairList(final String key)
	{
		final List<Pair> readObjects = Lists.newArrayList();
		final ListNBT nbtList = this.tag.getList(key, 10);

		for (int i = 0; i < nbtList.size(); i++)
		{
			final CompoundNBT leftData = nbtList.getCompound(i);
			final CompoundNBT rightData = nbtList.getCompound(++i);

			final L left = NBTHelper.read(leftData);
			final R right = NBTHelper.read(rightData);

			readObjects.add(Pair.of(left, right));
		}

		return (List<T>) readObjects;
	}

	public void setSet(final String key, final Set<? extends NBT> nbtList)
	{
		final ListNBT writtenObjects = new ListNBT();

		for (final NBT nbt : nbtList)
		{
			writtenObjects.add(NBTHelper.write(nbt));
		}

		this.tag.put(key, writtenObjects);
	}

	public <T extends NBT> Set<T> getSet(final World world, final String key)
	{
		final Set<T> readObjects = Sets.newHashSet();
		final ListNBT nbtList = this.tag.getList(key, 10);

		for (int i = 0; i < nbtList.size(); i++)
		{
			final CompoundNBT data = nbtList.getCompound(i);

			readObjects.add(NBTHelper.read(world, data));
		}

		return readObjects;
	}

	public <T extends NBT> Set<T> getSet(final String key)
	{
		final Set<T> readObjects = Sets.newHashSet();
		final ListNBT nbtList = this.tag.getList(key, 10);

		for (int i = 0; i < nbtList.size(); i++)
		{
			final CompoundNBT data = nbtList.getCompound(i);

			readObjects.add(NBTHelper.read(data));
		}

		return readObjects;
	}

	public void setStackList(final String key, final List<ItemStack> stackList)
	{
		final ListNBT writtenObjects = new ListNBT();

		for (final ItemStack stack : stackList)
		{
			CompoundNBT tag = new CompoundNBT();

			stack.write(tag);

			writtenObjects.add(tag);
		}

		this.tag.put(key, writtenObjects);
	}

	public List<ItemStack> getStackList(final String key)
	{
		final List<ItemStack> readObjects = Lists.newArrayList();
		final ListNBT nbtList = this.tag.getList(key, 10);

		for (int i = 0; i < nbtList.size(); i++)
		{
			final CompoundNBT data = nbtList.getCompound(i);

			readObjects.add(ItemStack.read(data));
		}

		return readObjects;
	}

	public <NBT_OBJECT> void setList(final String key, final List<NBT_OBJECT> nbtList, @Nullable Function<NBT_OBJECT, CompoundNBT> setter)
	{
		final ListNBT writtenObjects = new ListNBT();

		for (final NBT_OBJECT nbt : nbtList)
		{
			if (setter != null)
			{
				writtenObjects.add(setter.apply(nbt));
			}
			else
			{
				writtenObjects.add(NBTHelper.write((NBT) nbt));
			}
		}

		this.tag.put(key, writtenObjects);
	}

	public <NBT_OBJECT> void setArray(final String key, final NBT_OBJECT[] array, @Nullable Function<NBT_OBJECT, CompoundNBT> setter)
	{
		final boolean nul = array == null;

		this.tag.putBoolean(key + "_null", nul);

		if (nul)
		{
			return;
		}

		final ListNBT writtenObjects = new ListNBT();

		for (final NBT_OBJECT obj : array)
		{
			if (setter != null)
			{
				writtenObjects.add(setter.apply(obj));
			}
			else
			{
				writtenObjects.add(NBTHelper.write((NBT) obj));
			}
		}

		this.tag.put(key, writtenObjects);
	}

	public <NBT_OBJECT> NBT_OBJECT[] getArray(final String key, final Class<? extends NBT_OBJECT> clazz, @Nullable Function<CompoundNBT, NBT_OBJECT> getter)
	{
		if (this.tag.getBoolean(key + "_null"))
		{
			return null;
		}

		final ListNBT nbtList = this.tag.getList(key, 10);
		final NBT_OBJECT[] readObjects = (NBT_OBJECT[]) Array.newInstance(clazz, nbtList.size());

		for (int i = 0; i < nbtList.size(); i++)
		{
			readObjects[i] = getter != null ? getter.apply(nbtList.getCompound(i)) : NBTHelper.read(nbtList.getCompound(i));
		}

		return readObjects;
	}

	public <NBT_OBJECT> List<NBT_OBJECT> getList(final String key, @Nullable Function<CompoundNBT, NBT_OBJECT> getter)
	{
		final List<NBT_OBJECT> readObjects = Lists.newArrayList();
		final ListNBT nbtList = this.tag.getList(key, 10);

		for (int i = 0; i < nbtList.size(); i++)
		{
			final CompoundNBT data = nbtList.getCompound(i);

			readObjects.add(getter != null ? getter.apply(data) : NBTHelper.read(data));
		}

		return readObjects;
	}

	public void setList(final String key, final List<? extends NBT> nbtList)
	{
		final ListNBT writtenObjects = new ListNBT();

		for (final NBT nbt : nbtList)
		{
			writtenObjects.add(NBTHelper.write(nbt));
		}

		this.tag.put(key, writtenObjects);
	}

	public <T extends NBT> List<T> getList(final World world, final String key)
	{
		final List<T> readObjects = Lists.newArrayList();
		final ListNBT nbtList = this.tag.getList(key, 10);

		for (int i = 0; i < nbtList.size(); i++)
		{
			final CompoundNBT data = nbtList.getCompound(i);

			readObjects.add(NBTHelper.read(world, data));
		}

		return readObjects;
	}

	public <T extends NBT> List<T> getList(final String key)
	{
		final List<T> readObjects = Lists.newArrayList();
		final ListNBT nbtList = this.tag.getList(key, 10);

		for (int i = 0; i < nbtList.size(); i++)
		{
			final CompoundNBT data = nbtList.getCompound(i);

			readObjects.add(NBTHelper.read(data));
		}

		return readObjects;
	}

	public List<String> getStringList(final String key)
	{
		final List<String> readObjects = Lists.newArrayList();
		final ListNBT nbtList = this.tag.getList(key, 8);

		for (int i = 0; i < nbtList.size(); i++)
		{
			readObjects.add(nbtList.getString(i));
		}

		return readObjects;
	}

	public void setStringList(final String key, final List<String> stringList)
	{
		final ListNBT writtenObjects = new ListNBT();

		for (final String string : stringList)
		{
			writtenObjects.add(new StringNBT(string));
		}

		this.tag.put(key, writtenObjects);
	}

	public void setBooleanArray(final String key, final boolean[] array)
	{
		final ListNBT writtenObjects = new ListNBT();

		for (final boolean bool : array)
		{
			writtenObjects.add(new ByteNBT((byte) (bool ? 1 : 0)));
		}

		this.tag.put(key, writtenObjects);
	}

	public boolean[] getBooleanArray(final String key)
	{
		final ListNBT nbtList = this.tag.getList(key, 1);
		final boolean[] readObjects = new boolean[nbtList.size()];

		for (int i = 0; i < nbtList.size(); i++)
		{
			readObjects[i] = (((ByteNBT) nbtList.get(i)).getByte()) == 1;
		}

		return readObjects;
	}

	public <T extends IStringSerializable> void setStringSerializeArray(final String key, final T[] array)
	{
		final boolean nul = array == null;

		this.tag.putBoolean(key + "_null", nul);

		if (nul)
		{
			return;
		}

		final ListNBT writtenObjects = new ListNBT();

		for (final T obj : array)
		{
			writtenObjects.add(new StringNBT(obj.getName()));
		}

		this.tag.put(key, writtenObjects);
	}

	public <T extends Enum> void setEnumArray(final String key, final T[] array)
	{
		final boolean nul = array == null;

		this.tag.putBoolean(key + "_null", nul);

		if (nul)
		{
			return;
		}

		final ListNBT writtenObjects = new ListNBT();

		for (final T obj : array)
		{
			writtenObjects.add(new StringNBT(obj.name()));
		}

		this.tag.put(key, writtenObjects);
	}

	public <T extends NBT> void setArray(final String key, final T[] array)
	{
		final boolean nul = array == null;

		this.tag.putBoolean(key + "_null", nul);

		if (nul)
		{
			return;
		}

		final ListNBT writtenObjects = new ListNBT();

		for (final T obj : array)
		{
			writtenObjects.add(NBTHelper.write(obj));
		}

		this.tag.put(key, writtenObjects);
	}

	public String[] getStringSerializeArrayNames(final String key)
	{
		if (this.tag.getBoolean(key + "_null"))
		{
			return null;
		}

		final ListNBT nbtList = this.tag.getList(key, 8);
		final String[] readObjects = new String[nbtList.size()];

		for (int i = 0; i < nbtList.size(); i++)
		{
			readObjects[i] = nbtList.getString(i);
		}

		return readObjects;
	}

	public String[] getEnumArrayNames(final String key)
	{
		if (this.tag.getBoolean(key + "_null"))
		{
			return null;
		}

		final ListNBT nbtList = this.tag.getList(key, 8);
		final String[] readObjects = new String[nbtList.size()];

		for (int i = 0; i < nbtList.size(); i++)
		{
			readObjects[i] = nbtList.getString(i);
		}

		return readObjects;
	}

	public <T extends NBT> T[] getArray(final String key, final Class<? extends T> clazz)
	{
		if (this.tag.getBoolean(key + "_null"))
		{
			return null;
		}

		final ListNBT nbtList = this.tag.getList(key, 10);
		final T[] readObjects = (T[]) Array.newInstance(clazz, nbtList.size());

		for (int i = 0; i < nbtList.size(); i++)
		{
			readObjects[i] = NBTHelper.read(nbtList.getCompound(i));
		}

		return readObjects;
	}

	public void setIntArray(final String key, final int[] array)
	{
		final ListNBT writtenObjects = new ListNBT();

		for (final int obj : array)
		{
			writtenObjects.add(new IntNBT(obj));
		}

		this.tag.put(key, writtenObjects);
	}

	public int[] getIntArray(final String key)
	{
		final ListNBT nbtList = this.tag.getList(key, 3);
		final int[] readObjects = new int[nbtList.size()];

		for (int i = 0; i < nbtList.size(); i++)
		{
			readObjects[i] = nbtList.getInt(i);
		}

		return readObjects;
	}

	public void setBlockPos(final String key, BlockPos pos)
	{
		INBT tag = NBTHelper.writeBlockPos(pos);
		this.tag.put(key, tag);
	}

	public BlockPos getBlockPos(final String key)
	{
		CompoundNBT tag = this.tag.getCompound(key);
		return NBTHelper.readBlockPos(tag);
	}

}
