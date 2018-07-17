package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.core.tree.INode;
import com.gildedgames.orbis_api.core.tree.LayerLink;
import com.gildedgames.orbis_api.core.variables.GuiVarBoolean;
import com.gildedgames.orbis_api.core.variables.IGuiVarDisplayContents;

public interface IBlueprint extends IGuiVarDisplayContents
{

	int getCurrentScheduleLayerIndex();

	void setCurrentScheduleLayerIndex(final int index);

	GuiVarBoolean getLayerTransparencyVar();

	INode<IScheduleLayer, LayerLink> getCurrentScheduleLayerNode();

	void listen(IScheduleLayerHolderListener listener);

	boolean unlisten(IScheduleLayerHolderListener listener);

}
