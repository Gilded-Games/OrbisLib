package com.gildedgames.orbis.lib.core.variables.var_comparators;

import com.gildedgames.orbis.lib.core.variables.IGuiVar;
import com.gildedgames.orbis.lib.util.MathUtil;

public class NumberEquals<NUMBER extends Number & Comparable<NUMBER>> extends NumberCompareBase<NUMBER>
{
	protected NumberEquals()
	{

	}

	public NumberEquals(IGuiVar<NUMBER, ?> valueVar)
	{
		super(valueVar);
	}

	@Override
	public boolean compare(Object obj)
	{
		if (obj instanceof Number)
		{
			Number input = (Number) obj;

			if (input instanceof Double && this.value.getData() instanceof Double)
			{
				return MathUtil.epsilonEquals(input.doubleValue(), this.value.getData().doubleValue());
			}

			if (input instanceof Float && this.value.getData() instanceof Float)
			{
				return MathUtil.epsilonEquals(input.floatValue(), this.value.getData().floatValue());
			}
		}

		if (obj instanceof Comparable)
		{
			Comparable input = (Comparable) obj;

			return input.compareTo(this.value.getData()) == 0;
		}

		return false;
	}

	@Override
	public String getDisplayString()
	{
		return "orbis.gui.equals";
	}
}