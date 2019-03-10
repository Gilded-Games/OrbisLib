package com.gildedgames.orbis.lib.data.pathway;

import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.core.tree.ConditionLink;
import com.gildedgames.orbis.lib.core.tree.NodeTree;
import com.gildedgames.orbis.lib.core.variables.conditions.IGuiConditionEntrance;
import com.gildedgames.orbis.lib.data.IDataChild;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.framework.interfaces.EnumFacingMultiple;
import com.gildedgames.orbis.lib.data.region.IColored;
import com.gildedgames.orbis.lib.data.region.IRegionHolder;
import com.gildedgames.orbis.lib.util.mc.NBT;

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
