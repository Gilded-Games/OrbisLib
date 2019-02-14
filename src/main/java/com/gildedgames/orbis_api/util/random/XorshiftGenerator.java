package com.gildedgames.orbis_api.util.random;

// Extremely fast Xorshift implementation that doesn't derive Java's Random class. This avoids the initialization cost
// and minor penalties associated with it.
//
// This implementation does not produce very high quality randomness. XoRoShiRoRandom should almost always be used
// unless you know exactly what you're doing.
//
// The seed can never be zero. If zero is supplied, the seed 0xBADF00D (decimal 195948557) will instead be used.
public class XorshiftGenerator
{
	private long state;

	private static final SplitMixRandom seedUniquifier = new SplitMixRandom(System.nanoTime());

	public static long randomSeed()
	{
		final long x;

		synchronized (XorshiftGenerator.seedUniquifier)
		{
			x = XorshiftGenerator.seedUniquifier.nextLong();
		}

		return x ^ System.nanoTime();
	}

	public XorshiftGenerator()
	{
		this(XorshiftGenerator.randomSeed());
	}

	public XorshiftGenerator(final long seed)
	{
		this.setSeed(seed);
	}

	public long nextLong()
	{
		long x = this.state;
		x ^= x << 13;
		x ^= x >> 17;
		x ^= x << 5;

		this.state = x;

		return x;
	}

	public int nextInt()
	{
		return (int) this.nextLong();
	}

	public int nextInt(final int n)
	{
		return (int) this.nextLong(n);
	}

	private long nextLong(final long n)
	{
		if (n <= 0)
		{
			throw new IllegalArgumentException("illegal bound " + n + " (must be positive)");
		}

		long t = this.nextLong();

		final long nMinus1 = n - 1;

		// Shortcut for powers of two--high bits
		if ((n & nMinus1) == 0)
		{
			return (t >>> Long.numberOfLeadingZeros(nMinus1)) & nMinus1;
		}

		// Rejection-based algorithm to get uniform integers in the general case
		long u = t >>> 1;

		while (u + nMinus1 - (t = u % n) < 0)
		{
			u = this.nextLong() >>> 1;
		}

		return t;

	}

	public double nextDouble()
	{
		return Double.longBitsToDouble(0x3FFL << 52 | this.nextLong() >>> 12) - 1.0;
	}

	public float nextFloat()
	{
		return (this.nextLong() >>> 40) * 0x1.0p-24f;
	}

	public boolean nextBoolean()
	{
		return this.nextLong() < 0;
	}

	public void setSeed(final long seed)
	{
		this.state = seed == 0 ? 0xBADF00D : seed;
	}
}