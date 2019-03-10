package com.gildedgames.orbis.lib.data.region;

import com.gildedgames.orbis.lib.util.mc.NBT;

/**
 * Make your data class implement this if you want it to have
 * a visible region
 */
public interface IDimensions extends NBT
{

	int getWidth();

	int getHeight();

	int getLength();

}
