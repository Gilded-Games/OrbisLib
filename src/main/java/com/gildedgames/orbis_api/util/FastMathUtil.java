package com.gildedgames.orbis_api.util;

// Partial reproduction of FastMath from the Apache Commons Math library.
// https://github.com/apache/commons-math
public class FastMathUtil
{
	private static final long MASK_NON_SIGN_LONG = 0x7fffffffffffffffL;

	// Significantly faster than Math.hypot
	public static double hypot(final double x, final double y)
	{
		if (Double.isInfinite(x) || Double.isInfinite(y))
		{
			return Double.POSITIVE_INFINITY;
		}
		else if (Double.isNaN(x) || Double.isNaN(y))
		{
			return Double.NaN;
		}
		else
		{
			final int expX = getExponent(x);
			final int expY = getExponent(y);

			if (expX > expY + 27)
			{
				return abs(x);
			}
			else if (expY > expX + 27)
			{
				return abs(y);
			}
			else
			{
				final int middleExp = (expX + expY) / 2;

				final double scaledX = scalb(x, -middleExp);
				final double scaledY = scalb(y, -middleExp);

				final double scaledH = sqrt(scaledX * scaledX + scaledY * scaledY);

				return scalb(scaledH, middleExp);
			}

		}
	}

	public static double scalb(final double d, final int n)
	{
		if ((n > -1023) && (n < 1024))
		{
			return d * Double.longBitsToDouble(((long) (n + 1023)) << 52);
		}

		if (Double.isNaN(d) || Double.isInfinite(d) || (d == 0))
		{
			return d;
		}

		if (n < -2098)
		{
			return (d > 0) ? 0.0 : -0.0;
		}

		if (n > 2097)
		{
			return (d > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
		}

		final long bits = Double.doubleToRawLongBits(d);
		final long sign = bits & 0x8000000000000000L;

		int exponent = ((int) (bits >>> 52)) & 0x7ff;

		long mantissa = bits & 0x000fffffffffffffL;

		int scaledExponent = exponent + n;

		if (n < 0)
		{
			if (scaledExponent > 0)
			{
				return Double.longBitsToDouble(sign | (((long) scaledExponent) << 52) | mantissa);
			}
			else if (scaledExponent > -53)
			{
				mantissa |= 1L << 52;

				final long mostSignificantLostBit = mantissa & (1L << (-scaledExponent));
				mantissa >>>= 1 - scaledExponent;

				if (mostSignificantLostBit != 0)
				{
					mantissa++;
				}

				return Double.longBitsToDouble(sign | mantissa);
			}
			else
			{
				return (sign == 0L) ? 0.0 : -0.0;
			}
		}
		else
		{
			if (exponent == 0)
			{

				while ((mantissa >>> 52) != 1)
				{
					mantissa <<= 1;

					--scaledExponent;
				}

				++scaledExponent;

				mantissa &= 0x000fffffffffffffL;

				if (scaledExponent < 2047)
				{
					return Double.longBitsToDouble(sign | (((long) scaledExponent) << 52) | mantissa);
				}
				else
				{
					return (sign == 0L) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
				}
			}
			else if (scaledExponent < 2047)
			{
				return Double.longBitsToDouble(sign | (((long) scaledExponent) << 52) | mantissa);
			}
			else
			{
				return (sign == 0L) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
			}
		}
	}

	public static double abs(double x)
	{
		return Double.longBitsToDouble(MASK_NON_SIGN_LONG & Double.doubleToRawLongBits(x));
	}

	public static double sqrt(final double a)
	{
		return Math.sqrt(a);
	}

	public static int getExponent(final double d)
	{
		return (int) ((Double.doubleToRawLongBits(d) >>> 52) & 0x7ff) - 1023;
	}
}
