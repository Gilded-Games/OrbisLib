package com.gildedgames.orbis_api.world.data;

public class WorldDataManagerNOOP implements IWorldDataManager
{

	@Override
	public void register(IWorldData data)
	{

	}

	@Override
	public byte[] readBytes(IWorldData data, String path)
	{
		return new byte[0];
	}

	@Override
	public void writeBytes(IWorldData data, String path, byte[] bytes)
	{

	}

	@Override
	public void flush()
	{

	}

	@Override
	public void close()
	{

	}
}
