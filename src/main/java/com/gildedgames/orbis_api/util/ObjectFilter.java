package com.gildedgames.orbis_api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObjectFilter
{
	@SuppressWarnings("unchecked")
	public static <T> List<T> getTypesFrom(final Collection<?> list, final Class<? extends T> typeClass)
	{
		final List<T> returnList = new ArrayList<>();

		for (final Object obj : list)
		{
			if (obj != null && typeClass.isAssignableFrom(obj.getClass()))
			{
				returnList.add((T) obj);
			}
		}

		return returnList;
	}

	public static <T> List<T> getTypesFrom(final Collection<T> list, final FilterCondition<T> condition)
	{
		final List<T> returnList = new ArrayList<>();

		for (final T obj : list)
		{
			if (obj != null && condition.isType(obj))
			{
				returnList.add(obj);
			}
		}

		return returnList;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFirstFrom(final Collection<?> list, final Class<? extends T> typeClass)
	{
		for (final Object obj : list)
		{
			if (obj != null && obj.getClass() == typeClass)
			{
				return (T) obj;
			}
		}

		return null;
	}

	public static abstract class FilterCondition<T>
	{

		private final List<T> data;

		public FilterCondition(final List<T> data)
		{
			this.data = data;
		}

		public List<T> data()
		{
			return this.data;
		}

		public abstract boolean isType(T object);

	}

}
