package com.gildedgames.orbis_api.core.variables;

public interface IGuiVarCompareExpression<DATA> extends IGuiVarExpression<DATA>
{
	boolean compare(Object input);
}
