package com.gildedgames.orbis.lib.client.rect;

import java.util.List;

public interface RectListener
{

	void notifyDimChange(List<RectModifier.ModifierType> changedType);

}
