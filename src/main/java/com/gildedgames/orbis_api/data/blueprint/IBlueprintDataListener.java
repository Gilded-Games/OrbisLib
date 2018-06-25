package com.gildedgames.orbis_api.data.blueprint;

import com.gildedgames.orbis_api.core.tree.INode;
import com.gildedgames.orbis_api.core.tree.LayerLink;
import com.gildedgames.orbis_api.data.pathway.Entrance;
import com.gildedgames.orbis_api.data.schedules.IScheduleLayer;

public interface IBlueprintDataListener
{
	void onDataChanged();

	void onAddEntrance(Entrance entrance);

	void onRemoveEntrance(Entrance entrance);
}
