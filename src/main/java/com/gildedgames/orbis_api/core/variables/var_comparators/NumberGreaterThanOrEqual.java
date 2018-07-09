package com.gildedgames.orbis_api.core.variables.var_comparators;

import com.gildedgames.orbis_api.core.variables.IGuiVar;
import com.gildedgames.orbis_api.util.MathUtil;

public class NumberGreaterThanOrEqual<NUMBER extends Number & Comparable<NUMBER>> extends NumberCompareBase<NUMBER>
{
	private NumberGreaterThanOrEqual()
	{

	}

	public NumberGreaterThanOrEqual(IGuiVar<NUMBER, ?> valueVar)
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
				if (MathUtil.epsilonEquals(input.doubleValue(), this.value.getData().doubleValue()))
				{
					return true;
				}
			}

			if (input instanceof Float && this.value.getData() instanceof Float)
			{
				if (MathUtil.epsilonEquals(input.floatValue(), this.value.getData().floatValue()))
				{
					return true;
				}
			}
		}

		if (obj instanceof Comparable)
		{
			Comparable input = (Comparable) obj;

			return input.compareTo(this.value.getData()) >= 1 || input.compareTo(this.value.getData()) == 0;
		}

		return false;
	}

	@Override
	public String getDisplayString()
	{
		return "orbis.gui.greater_than_or_equal";
	}
}