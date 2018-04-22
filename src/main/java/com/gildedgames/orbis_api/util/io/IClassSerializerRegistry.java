package com.gildedgames.orbis_api.util.io;

import com.gildedgames.orbis_api.util.mc.NBT;

public interface IClassSerializerRegistry
{
	void register(IClassSerializer serializer);

	IClassSerializer findSerializer(String id);

	IClassSerializer findSerializer(NBT nbt);

	IClassSerializer findSerializer(Class<? extends NBT> clazz);

	String findID(IClassSerializer serializer);
}
