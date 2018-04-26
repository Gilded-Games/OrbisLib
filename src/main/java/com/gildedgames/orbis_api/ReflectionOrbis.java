package com.gildedgames.orbis_api;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionOrbis
{
	public static final Method POPULATE;

	public static final Method GENERATE_HEIGHT_MAP;

	static
	{
		POPULATE = ReflectionOrbis.getMethod(Chunk.class, new Class<?>[] { IChunkGenerator.class }, "populate", "func_185931_b");
		GENERATE_HEIGHT_MAP = ReflectionOrbis.getMethod(Chunk.class, new Class<?>[] {}, "generateHeightMap", "func_185978_a");
	}

	public static Field getField(final Class clazz, final String... names)
	{
		for (final Field field : clazz.getDeclaredFields())
		{
			for (final String name : names)
			{
				if (field.getName().equals(name))
				{
					field.setAccessible(true);

					return field;
				}
			}
		}

		throw new RuntimeException("Couldn't find field");
	}

	public static Method getMethod(final Class clazz, final Class<?>[] args, final String... names)
	{
		for (final Method method : clazz.getDeclaredMethods())
		{
			for (final String name : names)
			{
				if (method.getName().equals(name))
				{
					final Class<?>[] matching = method.getParameterTypes();

					boolean matches = true;

					if (matching.length != args.length)
					{
						matches = false;
					}
					else
					{
						for (int i = 0; i < args.length; i++)
						{
							if (matching[i] != args[i])
							{
								matches = false;

								break;
							}
						}
					}

					if (matches)
					{
						method.setAccessible(true);

						return method;
					}
				}
			}
		}

		throw new RuntimeException("Couldn't find method");
	}

	public static void invokeMethod(final Method method, final Object obj, final Object... args)
	{
		try
		{
			method.invoke(obj, args);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			throw new RuntimeException("Failed to invoke method through reflection", e);
		}
	}

	public static Object getValue(final Field field, final Object obj)
	{
		try
		{
			return field.get(obj);
		}
		catch (final IllegalAccessException e)
		{
			throw new RuntimeException("Failed to fetch field value", e);
		}
	}

	public static void setValue(final Field field, final Object instance, final Object value)
	{
		try
		{
			field.set(instance, value);
		}
		catch (final IllegalAccessException e)
		{
			throw new RuntimeException("Failed to fetch field value", e);
		}
	}

	public static class ReflectionEntry
	{
		private final String[] mappings;

		private ReflectionEntry(final String... mappings)
		{
			this.mappings = mappings;
		}

		public String[] getMappings()
		{
			return this.mappings;
		}
	}
}
