package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.util.mc.NBT;

public interface IFilterOptions extends NBT
{

	IFilterOptions setChoosesPerBlock(boolean choosesPerBlock);

	boolean choosesPerBlock();

	float getEdgeNoise();

	IFilterOptions setEdgeNoise(float edgeNoise);

}
