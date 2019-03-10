package com.gildedgames.orbis.lib.world;

public interface IWorldObjectManagerObserver
{

	void onObjectAdded(WorldObjectManager manager, IWorldObject obj);

	void onObjectRemoved(WorldObjectManager manager, IWorldObject obj);

	void onReloaded(WorldObjectManager manager);

}
