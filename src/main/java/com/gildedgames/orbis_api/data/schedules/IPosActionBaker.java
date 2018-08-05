package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.core.baking.IBakedPosAction;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.util.mc.NBT;

import java.util.List;
import java.util.Random;

public interface IPosActionBaker extends NBT
{
	List<IBakedPosAction> bakeActions(IRegion bounds, Random rand);
}