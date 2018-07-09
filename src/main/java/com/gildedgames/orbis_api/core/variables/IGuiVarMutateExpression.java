package com.gildedgames.orbis_api.core.variables;

public interface IGuiVarMutateExpression<DATA> extends IGuiVarExpression<DATA>
{
	DATA mutate(DATA input);
}
