package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.core.tree.INode;
import com.gildedgames.orbis.lib.core.tree.LayerLink;

public interface IScheduleLayerHolderListener
{

	void onChangeScheduleLayerNode(INode<IScheduleLayer, LayerLink> prevLayer, int prevIndex, INode<IScheduleLayer, LayerLink> newLayer, int newIndex);

}
