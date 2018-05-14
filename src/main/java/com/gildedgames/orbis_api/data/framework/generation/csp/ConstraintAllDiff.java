package com.gildedgames.orbis_api.data.framework.generation.csp;

public class ConstraintAllDiff<VAR> extends AbstractConstraint<VAR>
{

	@SafeVarargs
	public ConstraintAllDiff(VAR... vars)
	{
		super(vars);
	}

	@Override
	public boolean constraint(Object... values)
	{
		for (int i = 0; i < values.length; i++)
		{
			for (int j = i + 1; j < values.length; j++)
			{
				if (values[i].equals(values[j]))
				{
					return false;
				}
			}
		}
		return true;
	}

}
