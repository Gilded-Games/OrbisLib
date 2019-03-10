package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.core.tree.INode;
import com.gildedgames.orbis.lib.core.tree.LayerLink;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;

public interface IBlueprint
{

	BlueprintData getData();

	int getCurrentScheduleLayerIndex();

	void setCurrentScheduleLayerIndex(final int index);

	INode<IScheduleLayer, LayerLink> getCurrentScheduleLayerNode();

	void listen(IScheduleLayerHolderListener listener);

	boolean unlisten(IScheduleLayerHolderListener listener);

}
