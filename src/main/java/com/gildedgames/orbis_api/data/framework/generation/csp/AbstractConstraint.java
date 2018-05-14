package com.gildedgames.orbis_api.data.framework.generation.csp;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractConstraint<VAR> implements IConstraint<VAR>
{
	private final List<VAR> vars;

	@SafeVarargs
	public AbstractConstraint(VAR... vars)
	{
		this.vars = Arrays.asList(vars);
	}

	@Override
	public List<VAR> scope()
	{
		return this.vars;
	}
}
