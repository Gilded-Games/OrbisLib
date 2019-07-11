package com.gildedgames.orbis.lib;

import com.gildedgames.orbis.lib.util.io.IClassSerializer;
import com.gildedgames.orbis.lib.util.io.IClassSerializerRegistry;
import com.gildedgames.orbis.lib.util.io.Instantiator;
import com.gildedgames.orbis.lib.util.mc.NBT;
import com.gildedgames.orbis.lib.util.mc.NBTHelper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class IOHelper implements IClassSerializerRegistry
{

	public BiMap<String, IClassSerializer> idToSerializer = HashBiMap.create();

	public IOHelper()
	{

	}

	public static <T extends NBT> void register(IClassSerializer s, int id, Class<T> clazz)
	{
		s.register(id, clazz, new Instantiator<>(clazz));
	}

	public CompoundNBT write(final NBT nbt)
	{
		return NBTHelper.write(nbt);
	}

	public <T extends NBT> T read(final CompoundNBT tag)
	{
		return NBTHelper.read(tag);
	}

	public <T extends NBT> T read(final World world, final CompoundNBT tag)
	{
		return NBTHelper.read(world, tag);
	}

	@Override
	public void register(IClassSerializer serializer)
	{
		this.idToSerializer.put(serializer.identifier(), serializer);
	}

	@Override
	public IClassSerializer findSerializer(String id)
	{
		return this.idToSerializer.get(id);
	}

	@Override
	public IClassSerializer findSerializer(NBT nbt)
	{
		return this.findSerializer(nbt.getClass());
	}

	@Override
	public IClassSerializer findSerializer(Class<? extends NBT> clazz)
	{
		for (IClassSerializer s : this.idToSerializer.values())
		{
			if (s.isRegistered(clazz))
			{
				return s;
			}
		}

		throw new RuntimeException("This object has not been registered to a serializer: " + clazz);
	}

	@Override
	public String findID(IClassSerializer serializer)
	{
		return this.idToSerializer.inverse().get(serializer);
	}
}
