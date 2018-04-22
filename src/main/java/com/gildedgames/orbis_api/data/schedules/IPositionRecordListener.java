package com.gildedgames.orbis_api.data.schedules;

public interface IPositionRecordListener<DATA>
{

	void onMarkPos(DATA data, int x, int y, int z);

	void onUnmarkPos(int x, int y, int z);

}
