package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.block.BlockFilter;
import com.gildedgames.orbis_api.data.region.IDimensions;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.gildedgames.orbis_api.world.IWorldObjectChild;

public interface IScheduleLayer extends NBT, IWorldObjectChild
{

	void listen(IScheduleLayerListener listener);

	boolean unlisten(IScheduleLayerListener listener);

	IFilterOptions getOptions();

	String getDisplayName();

	void setDisplayName(String displayName);

	IPositionRecord<BlockFilter> getFilterRecord();

	IScheduleRecord getScheduleRecord();

	void setDimensions(IDimensions dimensions);

	int getLayerId();

	void setLayerId(int layerId);

}
