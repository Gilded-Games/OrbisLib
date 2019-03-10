package com.gildedgames.orbis.lib.core.variables;

public interface IGuiVarMutateExpression<DATA> extends IGuiVarExpression<DATA>
{
	DATA mutate(DATA input);
}
