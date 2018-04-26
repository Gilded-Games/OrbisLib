package com.gildedgames.orbis_api.util;

import net.minecraft.util.math.MathHelper;

public class PointSerializer
{
	private static final int NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));

	private static final int NUM_Z_BITS = NUM_X_BITS;

	private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;

	private static final int Y_SHIFT = NUM_Z_BITS;

	private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;

	private static final long X_MASK = (1L << NUM_X_BITS) - 1L;

	private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;

	private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

	public static long toLong(int x, int y)
	{
		return ((long) x & X_MASK) << X_SHIFT | ((long) 0 & Y_MASK) << Y_SHIFT | ((long) y & Z_MASK);
	}

	public static int x(long serialized)
	{
		return (int) (serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
	}

	public static int y(long serialized)
	{
		return (int) (serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
	}

}
