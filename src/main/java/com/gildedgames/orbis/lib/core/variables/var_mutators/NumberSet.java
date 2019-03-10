package com.gildedgames.orbis.lib.core.variables.var_mutators;

import com.gildedgames.orbis.lib.core.variables.IGuiVar;

public class NumberSet<NUMBER extends Number & Comparable<NUMBER>> extends NumberMutateBase<NUMBER>
{
	private NumberSet()
	{

	}

	public NumberSet(IGuiVar<NUMBER, ?> valueVar)
	{
		super(valueVar);
	}

	@Override
	public NUMBER mutate(NUMBER input)
	{
		return this.value.getData();
	}

	@Override
	public String getDisplayString()
	{
		return "orbis.gui.set";
	}
}
