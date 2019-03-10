package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.data.region.IDimensions;
import com.gildedgames.orbis.lib.data.region.IRegion;
import com.gildedgames.orbis.lib.data.region.IShape;

public interface IPositionRecord<DATA> extends IShape, IDimensions
{

	int getVolume();

	void listen(IPositionRecordListener<DATA> listener);

	boolean unlisten(IPositionRecordListener<DATA> listener);

	boolean contains(int index);

	DATA[] getData();

	DATA get(int index);

	DATA get(int x, int y, int z);

	void markPos(DATA data, int x, int y, int z);

	void unmarkPos(int x, int y, int z);

	IRegion getRegion();
}