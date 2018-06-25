package com.gildedgames.orbis_api.core.variables;

import com.gildedgames.orbis_api.client.gui.util.GuiFrame;
import com.gildedgames.orbis_api.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis_api.util.mc.NBT;

public interface IGuiVar<T, DISPLAY extends GuiFrame> extends NBT
{

	void setParentDisplay(GuiVarDisplay parentDisplay);

	String getName();

	T getData();

	void setData(T data);

	DISPLAY createDisplay(int maxWidth);

	void updateDataFromDisplay(DISPLAY display);

	void resetDisplayFromData(DISPLAY display);

}
