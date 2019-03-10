package com.gildedgames.orbis.lib.util.io;

import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class Instantiator<T> implements Function<World, T>
{

	private final Class<T> clazz;

	private Constructor<T> emptyConstructor;

	private Constructor<T> worldConstructor;

	/**
	 * Requires the passed class to have a default constructor with World parameter (can be private). Will throw a NPE if it has no default constructor.
	 * @param clazz
	 */
	public Instantiator(final Class<T> clazz)
	{
		this.clazz = clazz;
	}

	@Override
	public T apply(final World world)
	{
		try
		{
			final Constructor<T> constructor;

			if (world == null)
			{
				if (this.emptyConstructor == null)
				{
					try
					{
						this.emptyConstructor = this.clazz.getDeclaredConstructor();
						this.emptyConstructor.setAccessible(true);
					}
					catch (NoSuchMethodException e)
					{
						throw new NullPointerException("Couldn't find public constructor()" + this.clazz.getName());
					}
				}

				constructor = this.emptyConstructor;
			}
			else
			{
				if (this.worldConstructor == null)
				{
					try
					{
						this.worldConstructor = this.clazz.getDeclaredConstructor(World.class);
						this.worldConstructor.setAccessible(true);
					}
					catch (NoSuchMethodException e)
					{
						throw new NullPointerException("Couldn't find public constructor(World) for " + this.clazz.getName());
					}
				}

				constructor = this.worldConstructor;
			}

			return world != null ? constructor.newInstance(world) : constructor.newInstance();
		}
		catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}

}
