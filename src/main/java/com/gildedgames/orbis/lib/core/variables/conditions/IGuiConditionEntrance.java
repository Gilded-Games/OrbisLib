package com.gildedgames.orbis.lib.core.variables.conditions;

import com.gildedgames.orbis.lib.data.pathway.IEntrance;

public interface IGuiConditionEntrance extends IGuiCondition
{
	boolean canConnectTo(IEntrance entrance);
}
