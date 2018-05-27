package com.gildedgames.orbis_api.util;

import java.util.Random;

// XoShiRo256** implementation from DSI Utilities, adopted in a minimal implementation to not
// import Apache Commons.
//
// http://xoshiro.di.unimi.it/
public class XoShiRoRandom extends Random
{
	private static final long serialVersionUID = 1L;

	private long s0, s1, s2, s3;

	private static final SplitMixRandom seedUniquifier = new SplitMixRandom(System.nanoTime());

	public static long randomSeed()
	{
		final long x;

		synchronized (XoShiRoRandom.seedUniquifier)
		{
			x = XoShiRoRandom.seedUniquifier.nextLong();
		}

		return x ^ System.nanoTime();
	}

	public XoShiRoRandom()
	{
		this(XoShiRoRandom.randomSeed());
	}

	public XoShiRoRandom(final long seed)
	{
		this.setSeed(seed);
	}

	@Override
	public long nextLong()
	{
		long result = this.s1;
		result = Long.rotateLeft(result + (result << 2), 7);
		result += result << 3;

		final long t = this.s1 << 17;

		this.s2 ^= this.s0;
		this.s3 ^= this.s1;
		this.s1 ^= this.s2;
		this.s0 ^= this.s3;

		this.s2 ^= t;

		this.s3 = Long.rotateLeft(this.s3, 45);

		return result;
	}

	@Override
	public int nextInt()
	{
		return (int) this.nextLong();
	}

	@Override
	public int nextInt(final int n)
	{
		return (int) this.nextLong(n);
	}

	public long nextLong(final long n)
	{
		if (n <= 0)
		{
			throw new IllegalArgumentException("illegal bound " + n + " (must be positive)");
		}

		long t = this.nextLong();

		final long nMinus1 = n - 1;

		long u = t >>> 1;

		while (u + nMinus1 - (t = u % n) < 0)
		{
			u = this.nextLong() >>> 1;
		}
		return t;
	}

	@Override
	public double nextDouble()
	{
		return (this.nextLong() >>> 11) * 0x1.0p-53;
	}

	public double nextDoubleFast()
	{
		return Double.longBitsToDouble(0x3FFL << 52 | this.nextLong() >>> 12) - 1.0;
	}

	@Override
	public float nextFloat()
	{
		return (this.nextLong() >>> 40) * 0x1.0p-24f;
	}

	@Override
	public boolean nextBoolean()
	{
		return this.nextLong() < 0;
	}

	@Override
	public void nextBytes(final byte[] bytes)
	{
		int i = bytes.length, n = 0;

		while (i != 0)
		{
			n = Math.min(i, 8);

			for (long bits = this.nextLong(); n-- != 0; bits >>= 8)
			{
				bytes[--i] = (byte) bits;
			}
		}
	}

	@Override
	public void setSeed(final long seed)
	{
		final SplitMixRandom r = new SplitMixRandom(seed);

		this.s0 = r.nextLong();
		this.s1 = r.nextLong();
		this.s2 = r.nextLong();
		this.s3 = r.nextLong();
	}

	public void setState(final long[] state)
	{
		if (state.length != 4)
		{
			throw new IllegalArgumentException("The argument array contains " + state.length + " longs instead of " + 2);
		}

		this.s0 = state[0];
		this.s1 = state[1];
		this.s2 = state[2];
		this.s3 = state[3];
	}
}