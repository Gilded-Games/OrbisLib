package com.gildedgames.orbis_api.data.management;

import com.gildedgames.orbis_api.util.mc.IText;
import com.gildedgames.orbis_api.util.mc.NBT;

import java.util.List;

public interface IMetadata extends NBT
{
	/**
	 * Used for displaying this metadata to users in the GUI.
	 * @return The text that will be displayed.
	 */
	List<IText> getMetadataDisplay();
}
