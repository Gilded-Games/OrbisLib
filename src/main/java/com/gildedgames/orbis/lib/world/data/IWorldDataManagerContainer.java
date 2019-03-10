package com.gildedgames.orbis.lib.world.data;

public interface IWorldDataManagerContainer
{
	IWorldDataManager get();

	WorldDataStorageMethod getLastStorageMethod();

	void setLastStorageMethod(WorldDataStorageMethod method);
}
