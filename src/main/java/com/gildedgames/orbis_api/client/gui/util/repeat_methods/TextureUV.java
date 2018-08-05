package com.gildedgames.orbis_api.client.gui.util.repeat_methods;

public class TextureUV
{
	private final int u, v, width, height;

	public TextureUV(int u, int v, int width, int height)
	{
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
	}

	public int getU()
	{
		return this.u;
	}

	public int getV()
	{
		return this.v;
	}

	public int getWidth()
	{
		return this.width;
	}

	public int getHeight()
	{
		return this.height;
	}

	public int getMaxU()
	{
		return this.u + this.width;
	}

	public int getMaxV()
	{
		return this.v + this.height;
	}
}