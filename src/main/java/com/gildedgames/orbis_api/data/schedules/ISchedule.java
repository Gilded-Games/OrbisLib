package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.tree.ConditionLink;
import com.gildedgames.orbis_api.core.tree.NodeTree;
import com.gildedgames.orbis_api.core.variables.conditions.IGuiCondition;
import com.gildedgames.orbis_api.core.variables.post_resolve_actions.IPostResolveAction;
import com.gildedgames.orbis_api.data.IDataChild;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.util.mc.NBT;

public interface ISchedule extends NBT, IDataChild<BlueprintData>
{

	String getTriggerId();

	void setTriggerId(String triggerId);

	IScheduleRecord getParent();

	void setParent(IScheduleRecord parent);

	IRegion getBounds();

	NodeTree<IGuiCondition, ConditionLink> getConditionNodeTree();

	NodeTree<IPostResolveAction, NBT> getPostResolveActionNodeTree();

	Pos2D getConditionGuiPos();

	void setConditionGuiPos(Pos2D pos);

	Pos2D getPostResolveActionGuiPos();

	void setPostResolveActionGuiPos(Pos2D pos);

}
