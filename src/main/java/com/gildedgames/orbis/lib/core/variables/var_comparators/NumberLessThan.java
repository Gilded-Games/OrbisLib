package com.gildedgames.orbis.lib.core.variables.var_comparators;

import com.gildedgames.orbis.lib.core.variables.IGuiVar;

public class NumberLessThan<NUMBER extends Number & Comparable<NUMBER>> extends NumberCompareBase<NUMBER>
{
	private NumberLessThan()
	{

	}

	public NumberLessThan(IGuiVar<NUMBER, ?> valueVar)
	{
		super(valueVar);
	}

	@Override
	public boolean compare(Object obj)
	{
		if (obj instanceof Comparable)
		{
			Comparable input = (Comparable) obj;

			return input.compareTo(this.value.getData()) <= -1;
		}

		return false;
	}

	@Override
	public String getDisplayString()
	{
		return "orbis.gui.less_than";
	}
}