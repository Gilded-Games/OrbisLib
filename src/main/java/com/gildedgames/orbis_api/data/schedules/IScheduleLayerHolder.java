package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.core.tree.INode;
import com.gildedgames.orbis_api.core.tree.LayerLink;

public interface IScheduleLayerHolder
{

	int getCurrentScheduleLayerIndex();

	void setCurrentScheduleLayerIndex(final int index);

	INode<IScheduleLayer, LayerLink> getCurrentScheduleLayerNode();

	void listen(IScheduleLayerHolderListener listener);

	boolean unlisten(IScheduleLayerHolderListener listener);

}
