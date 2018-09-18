package com.gildedgames.orbis_api.data.pathway;

import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.tree.ConditionLink;
import com.gildedgames.orbis_api.core.tree.NodeTree;
import com.gildedgames.orbis_api.core.variables.conditions.IGuiConditionEntrance;
import com.gildedgames.orbis_api.data.IDataChild;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.framework.interfaces.EnumFacingMultiple;
import com.gildedgames.orbis_api.data.region.IColored;
import com.gildedgames.orbis_api.data.region.IRegionHolder;
import com.gildedgames.orbis_api.util.mc.NBT;

public interface IEntrance extends NBT, IColored, IDataChild<BlueprintData>, IRegionHolder
{
	PathwayData toConnectTo();

	EnumFacingMultiple getFacing();

	void setFacing(EnumFacingMultiple facing);

	String getTriggerId();

	void setTriggerId(String triggerId);

	NodeTree<IGuiConditionEntrance, ConditionLink> getConditionNodeTree();

	Pos2D getConditionGuiPos();

	void setConditionGuiPos(Pos2D pos);
}
