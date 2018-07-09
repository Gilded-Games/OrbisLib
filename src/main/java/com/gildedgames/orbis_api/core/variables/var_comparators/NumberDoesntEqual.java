package com.gildedgames.orbis_api.core.variables.var_comparators;

import com.gildedgames.orbis_api.core.variables.IGuiVar;

public class NumberDoesntEqual<NUMBER extends Number & Comparable<NUMBER>> extends NumberEquals<NUMBER>
{
	private NumberDoesntEqual()
	{

	}

	public NumberDoesntEqual(IGuiVar<NUMBER, ?> valueVar)
	{
		super(valueVar);
	}

	@Override
	public boolean compare(Object input)
	{
		return !super.compare(input);
	}

	@Override
	public String getDisplayString()
	{
		return "orbis.gui.doesnt_equal";
	}
}