package com.gildedgames.orbis_api.data.management.impl;

import com.google.gson.*;

import java.lang.reflect.Type;

public class GenericSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T>
{
	private Class clazz;

	public GenericSerializer(Class clazz)
	{
		this.clazz = clazz;
	}

	@Override
	public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
	{
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		return jsonDeserializationContext.deserialize(jsonObject, this.clazz);
	}

	@Override
	public JsonElement serialize(T jsonElement, Type type, JsonSerializationContext jsonSerializationContext)
	{
		return jsonSerializationContext.serialize(jsonElement);
	}
}