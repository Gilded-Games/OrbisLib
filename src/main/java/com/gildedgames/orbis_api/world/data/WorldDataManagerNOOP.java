package com.gildedgames.orbis_api.world.data;

import java.io.IOException;

public class WorldDataManagerNOOP implements IWorldDataManager
{

	@Override
	public void register(IWorldData data)
	{

	}

	@Override
	public byte[] readBytes(IWorldData data, String path) throws IOException
	{
		return new byte[0];
	}

	@Override
	public void writeBytes(IWorldData data, String path, byte[] bytes) throws IOException
	{

	}

	@Override
	public void flush()
	{

	}
}
