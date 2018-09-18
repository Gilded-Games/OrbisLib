package com.gildedgames.orbis_api.core.variables.conditions;

import com.gildedgames.orbis_api.data.pathway.IEntrance;

public interface IGuiConditionEntrance extends IGuiCondition
{
	boolean canConnectTo(IEntrance entrance);
}
