package com.gildedgames.orbis.lib.util.mc;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.util.io.IClassSerializer;
import com.google.common.collect.AbstractIterator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Supplier;

public class NBTHelper
{

	public static ListNBT getList(final CompoundNBT tag, final String key)
	{
		return tag.getList(key, 10);
	}

	public static Iterable<CompoundNBT> getIterator(final CompoundNBT tag, final String tagListKey)
	{
		return getIterator(getList(tag, tagListKey));
	}

	/**
	 * Get the iterator for a taglist in an NBTTagCompound.
	 * Simply a nice shortcut method.
	 */
	public static Iterable<CompoundNBT> getIterator(final ListNBT tagList)
	{
		return new Iterable<CompoundNBT>()
		{
			@Override
			public Iterator<CompoundNBT> iterator()
			{
				return new AbstractIterator<CompoundNBT>()
				{
					private int i = 0;

					@Override
					protected CompoundNBT computeNext()
					{
						if (this.i >= tagList.size())
						{
							return this.endOfData();
						}

						final CompoundNBT tag = tagList.getCompound(this.i);
						this.i++;
						return tag;
					}
				};
			}
		};
	}

	public static CompoundNBT readNBTFromFile(final MinecraftServer server, final String fileName)
	{
		return readNBTFromFile(new File(server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory(), fileName));
	}

	public static CompoundNBT readNBTFromFile(final File file)
	{
		try
		{
			if (!file.exists())
			{
				return null;
			}
			final FileInputStream inputStream = new FileInputStream(file);
			return CompressedStreamTools.readCompressed(inputStream);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void writeNBTToFile(final MinecraftServer server, final CompoundNBT tag, final String fileName)
	{
		writeNBTToFile(tag, new File(server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory(), fileName));
	}

	public static void writeNBTToFile(final CompoundNBT tag, final File file)
	{
		file.mkdirs();
		final File tmpFile = new File(file.getParentFile(), file.getName() + ".tmp");
		try
		{
			CompressedStreamTools.writeCompressed(tag, new FileOutputStream(tmpFile));
			if (file.exists())
			{
				file.delete();
			}
			tmpFile.renameTo(file);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	public static ItemStack readStack(CompoundNBT tag)
	{
		if (tag == null || tag.getBoolean("_null") || !tag.contains("_null"))
		{
			return ItemStack.EMPTY;
		}

		ItemStack itemstack = new ItemStack(OrbisLib.services().registrar().findItem(new ResourceLocation(tag.getString("id"))));
		itemstack.setCount(tag.getByte("count"));
		itemstack.setDamage(tag.getShort("damage"));

		if (tag.contains("data"))
		{
			itemstack.setTag(tag.getCompound("data"));
		}

		return itemstack;
	}

	public static INBT writeStack(ItemStack stack)
	{
		final CompoundNBT tag = new CompoundNBT();

		if (stack.isEmpty())
		{
			tag.putBoolean("_null", true);

			return tag;
		}

		tag.putBoolean("_null", false);

		tag.putString("id", OrbisLib.services().registrar().getIdentifierFor(stack.getItem()).toString());
		tag.putByte("count", (byte) stack.getCount());
		tag.putShort("damage", (short) stack.getDamage());

		CompoundNBT data = stack.getTag();

		if (data != null)
		{
			data.put("data", data);
		}

		return data;
	}

	public static BlockPos readBlockPos(final CompoundNBT tag)
	{
		if (tag == null || tag.getBoolean("_null") || !tag.contains("_null"))
		{
			return null;
		}

		return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
	}

	public static INBT writeBlockPos(final BlockPos pos)
	{
		final CompoundNBT tag = new CompoundNBT();

		if (pos == null)
		{
			tag.putBoolean("_null", true);

			return tag;
		}

		tag.putBoolean("_null", false);

		tag.putInt("x", pos.getX());
		tag.putInt("y", pos.getY());
		tag.putInt("z", pos.getZ());

		return tag;
	}

	public static ChunkPos readChunkPos(final CompoundNBT tag)
	{
		if (tag == null || tag.getBoolean("_null") || !tag.contains("_null"))
		{
			return null;
		}

		return new ChunkPos(tag.getInt("x"), tag.getInt("z"));
	}

	public static INBT writeChunkPos(final ChunkPos pos)
	{
		final CompoundNBT tag = new CompoundNBT();

		if (pos == null)
		{
			tag.putBoolean("_null", true);

			return tag;
		}

		tag.putBoolean("_null", false);

		tag.putInt("x", pos.x);
		tag.putInt("z", pos.z);

		return tag;
	}

	public static double[] readDoubleArray(final CompoundNBT tag)
	{
		if (tag == null || (tag.contains("_null") && tag.getBoolean("_null")))
		{
			return null;
		}

		final double[] array = new double[tag.getInt("length")];

		for (int i = 0; i < array.length; i++)
		{
			array[i] = tag.getDouble(String.valueOf(i));
		}

		return array;
	}

	public static INBT writeDoubleArray(final double[] array)
	{
		final CompoundNBT tag = new CompoundNBT();

		if (array == null)
		{
			tag.putBoolean("_null", true);

			return tag;
		}

		tag.putBoolean("_null", false);
		tag.putInt("length", array.length);

		int i = 0;

		for (final double value : array)
		{
			tag.putDouble(String.valueOf(i), value);

			i++;
		}

		return tag;
	}

	/**
	 * Writes the nbt data without any serialization
	 * @param nbt
	 * @return
	 */
	public static CompoundNBT writeRaw(final NBT nbt)
	{
		final CompoundNBT tag = new CompoundNBT();

		nbt.write(tag);

		return tag;
	}

	public static <T extends NBT> T clone(T nbt)
	{
		return clone(null, nbt);
	}

	public static <T extends NBT> T clone(World world, T nbt)
	{
		CompoundNBT tag = new CompoundNBT();

		nbt.write(tag);

		IClassSerializer serializer = OrbisLib.services().io().findSerializer(nbt);

		final int id = serializer.serialize(nbt);
		final T obj = serializer.deserialize(world, id);

		obj.read(tag);

		return obj;
	}

	public static CompoundNBT write(final NBT nbt)
	{
		final CompoundNBT tag = new CompoundNBT();

		if (nbt == null)
		{
			tag.putBoolean("_null", true);

			return tag;
		}

		IClassSerializer serializer = OrbisLib.services().io().findSerializer(nbt);

		tag.putBoolean("_null", false);
		tag.putString("s_id", serializer.identifier());
		tag.putInt("id", serializer.serialize(nbt));

		final CompoundNBT data = new CompoundNBT();

		nbt.write(data);

		tag.put("data", data);

		return tag;
	}

	public static <T extends NBT> T readWithDefault(final World world, final CompoundNBT tag, Supplier<T> def)
	{
		if (tag == null || tag.getBoolean("_null") || !tag.contains("_null"))
		{
			return def.get();
		}

		IClassSerializer serializer = OrbisLib.services().io().findSerializer(tag.getString("s_id"));

		final int id = tag.getInt("id");

		final T obj = serializer.deserialize(world, id);
		obj.read(tag.getCompound("data"));

		return obj;
	}

	public static <T extends NBT> T read(final World world, final CompoundNBT tag)
	{
		if (tag == null || tag.getBoolean("_null") || !tag.contains("_null"))
		{
			return null;
		}

		IClassSerializer serializer = OrbisLib.services().io().findSerializer(tag.getString("s_id"));

		final int id = tag.getInt("id");

		final T obj = serializer.deserialize(world, id);
		obj.read(tag.getCompound("data"));

		return obj;
	}

	public static <T extends NBT> T readWithDefault(final CompoundNBT tag, Supplier<T> def)
	{
		if (tag == null || tag.getBoolean("_null") || !tag.contains("_null"))
		{
			return def.get();
		}

		IClassSerializer serializer = OrbisLib.services().io().findSerializer(tag.getString("s_id"));

		final int id = tag.getInt("id");

		final T obj = serializer.deserialize(id);
		obj.read(tag.getCompound("data"));

		return obj;
	}

	public static <T extends NBT> T read(final CompoundNBT tag)
	{
		if (tag == null || tag.getBoolean("_null") || !tag.contains("_null"))
		{
			return null;
		}

		IClassSerializer serializer = OrbisLib.services().io().findSerializer(tag.getString("s_id"));

		final int id = tag.getInt("id");

		final T obj = serializer.deserialize(id);
		obj.read(tag.getCompound("data"));

		return obj;
	}

	public static <T extends NBT> T loadWithoutReading(final CompoundNBT tag)
	{
		if (tag == null || tag.getBoolean("_null") || !tag.contains("_null"))
		{
			return null;
		}

		IClassSerializer serializer = OrbisLib.services().io().findSerializer(tag.getString("s_id"));

		final int id = tag.getInt("id");

		return serializer.deserialize(id);
	}
}
