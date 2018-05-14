package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.core.ICreationData;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.processing.DataPrimer;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.gildedgames.orbis_api.world.IWorldObjectChild;

public interface ISchedule extends NBT, IWorldObjectChild
{

	String getTriggerId();

	void setTriggerId(String triggerId);

	IScheduleRecord getParent();

	void setParent(IScheduleRecord parent);

	IRegion getBounds();

	void onGenerateLayer(DataPrimer primer, ICreationData<?> data);

}
