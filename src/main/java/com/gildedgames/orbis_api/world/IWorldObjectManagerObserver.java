package com.gildedgames.orbis_api.world;

public interface IWorldObjectManagerObserver
{

	void onObjectAdded(WorldObjectManager manager, IWorldObject obj);

	void onObjectRemoved(WorldObjectManager manager, IWorldObject obj);

	void onReloaded(WorldObjectManager manager);

}
