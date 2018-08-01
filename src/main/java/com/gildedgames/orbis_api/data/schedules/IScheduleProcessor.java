package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.core.baking.IBakedPosAction;
import com.gildedgames.orbis_api.data.IDataChild;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.util.mc.NBT;

import java.util.List;
import java.util.Random;

public interface IScheduleProcessor extends NBT, IDataChild<BlueprintData>
{
	List<IBakedPosAction> bakeActions(IRegion bounds, Random rand);
}