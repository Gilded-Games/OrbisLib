package com.gildedgames.orbis.lib.core.variables.var_mutators;

import com.gildedgames.orbis.lib.core.variables.IGuiVar;

public class NumberDecrease<NUMBER extends Number & Comparable<NUMBER>> extends NumberMutateBase<NUMBER>
{
	private NumberDecrease()
	{

	}

	public NumberDecrease(IGuiVar<NUMBER, ?> valueVar)
	{
		super(valueVar);
	}

	@Override
	public NUMBER mutate(NUMBER input)
	{
		if (input instanceof Double && this.value.getData() instanceof Double)
		{
			return (NUMBER) new Double(input.doubleValue() - this.value.getData().doubleValue());
		}

		if (input instanceof Float && this.value.getData() instanceof Float)
		{
			return (NUMBER) new Float(input.floatValue() - this.value.getData().floatValue());
		}

		if (input instanceof Long && this.value.getData() instanceof Long)
		{
			return (NUMBER) new Long(input.longValue() - this.value.getData().longValue());
		}

		if (input instanceof Integer && this.value.getData() instanceof Integer)
		{
			return (NUMBER) new Integer(input.intValue() - this.value.getData().intValue());
		}

		return input;
	}

	@Override
	public String getDisplayString()
	{
		return "orbis.gui.decrease";
	}
}
