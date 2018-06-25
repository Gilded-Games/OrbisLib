package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.core.tree.INode;
import com.gildedgames.orbis_api.core.tree.LayerLink;

public interface IScheduleLayerHolderListener
{

	void onChangeScheduleLayerNode(INode<IScheduleLayer, LayerLink> prevLayer, int prevIndex, INode<IScheduleLayer, LayerLink> newLayer, int newIndex);

}
