package com.gildedgames.orbis_api.core.variables;

import com.gildedgames.orbis_api.util.mc.NBT;

import java.util.List;

public interface IGuiVarExpression<DATA> extends NBT
{
	String getDisplayString();

	List<IGuiVar<DATA, ?>> getInputs();

	void transferData(List<IGuiVar<DATA, ?>> prevInputs);
}
