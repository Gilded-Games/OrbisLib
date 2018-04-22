package com.gildedgames.orbis_api.data.blueprint;

import com.gildedgames.orbis_api.data.pathway.Entrance;
import com.gildedgames.orbis_api.data.schedules.IScheduleLayer;

public interface IBlueprintDataListener
{
	void onRemoveScheduleLayer(IScheduleLayer layer, int index);

	void onAddScheduleLayer(IScheduleLayer layer, int index);

	void onDataChanged();

	void onAddEntrance(Entrance entrance);

	void onRemoveEntrance(Entrance entrance);
}
