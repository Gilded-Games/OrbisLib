package com.gildedgames.orbis.lib.util;

public class ArrayHelper
{
	public static <T> boolean contains(T[] array, T obj)
	{
		for (T other : array)
		{
			if (other == obj)
			{
				return true;
			}
		}

		return false;
	}
}
