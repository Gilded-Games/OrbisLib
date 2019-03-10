package com.gildedgames.orbis.lib.core.variables;

public interface IGuiVarCompareExpression<DATA> extends IGuiVarExpression<DATA>
{
	boolean compare(Object input);
}
