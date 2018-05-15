package com.gildedgames.orbis_api.world.data;

public interface IWorldDataManagerContainer
{
	IWorldDataManager get();

	WorldDataStorageMethod getLastStorageMethod();

	void setLastStorageMethod(WorldDataStorageMethod method);
}
