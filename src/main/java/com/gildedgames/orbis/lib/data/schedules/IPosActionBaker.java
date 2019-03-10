package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.core.baking.IBakedPosAction;
import com.gildedgames.orbis.lib.data.region.IRegion;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.util.Rotation;

import java.util.List;
import java.util.Random;

public interface IPosActionBaker extends NBT
{
	List<IBakedPosAction> bakeActions(IRegion bounds, Random rand, Rotation rotation);
}