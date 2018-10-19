package com.gildedgames.orbis_api.util;

public class StringHelp
{
	public static String replaceLast(String text, String regex, String replacement)
	{
		return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
	}
}
