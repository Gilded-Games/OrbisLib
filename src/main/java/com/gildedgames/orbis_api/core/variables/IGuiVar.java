package com.gildedgames.orbis_api.core.variables;

import com.gildedgames.orbis_api.client.gui.data.DropdownElementWithData;
import com.gildedgames.orbis_api.client.gui.util.GuiFrame;
import com.gildedgames.orbis_api.util.mc.NBT;

import java.util.List;
import java.util.function.Supplier;

public interface IGuiVar<T, DISPLAY extends GuiFrame> extends NBT, IGuiVarDisplayChild
{
	String getVariableName();

	String getDataName();

	T getData();

	void setData(T data);

	DISPLAY createDisplay(int maxWidth);

	void updateDataFromDisplay(DISPLAY display);

	void resetDisplayFromData(DISPLAY display);

	List<DropdownElementWithData<Supplier<IGuiVarCompareExpression>>> getCompareExpressions();

	List<DropdownElementWithData<Supplier<IGuiVarMutateExpression>>> getMutateExpressions();
}
