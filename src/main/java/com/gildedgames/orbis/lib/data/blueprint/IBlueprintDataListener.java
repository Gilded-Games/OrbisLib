package com.gildedgames.orbis.lib.data.blueprint;

import com.gildedgames.orbis.lib.data.pathway.IEntrance;

public interface IBlueprintDataListener
{
	void onDataChanged();

	void onAddEntrance(IEntrance entrance);

	void onRemoveEntrance(IEntrance entrance);
}
