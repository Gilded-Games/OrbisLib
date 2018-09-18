package com.gildedgames.orbis_api.data.blueprint;

import com.gildedgames.orbis_api.data.pathway.IEntrance;

public interface IBlueprintDataListener
{
	void onDataChanged();

	void onAddEntrance(IEntrance entrance);

	void onRemoveEntrance(IEntrance entrance);
}
