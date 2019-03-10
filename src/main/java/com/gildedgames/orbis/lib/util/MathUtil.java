package com.gildedgames.orbis.lib.util;

import net.minecraft.util.math.MathHelper;

public class MathUtil
{
	public static boolean epsilonEquals(double d1, double d2)
	{
		return Math.abs(d2 - d1) < 1.0E-5F;
	}

	public static boolean epsilonEquals(float f1, float f2)
	{
		return MathHelper.abs(f2 - f1) < 1.0E-5F;
	}
}
