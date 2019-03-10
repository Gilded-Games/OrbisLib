package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.core.tree.ConditionLink;
import com.gildedgames.orbis.lib.core.tree.NodeTree;
import com.gildedgames.orbis.lib.core.variables.conditions.IGuiCondition;
import com.gildedgames.orbis.lib.core.variables.post_resolve_actions.IPostResolveAction;
import com.gildedgames.orbis.lib.data.IDataChild;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.region.IRegionHolder;
import com.gildedgames.orbis.lib.util.mc.NBT;

public interface ISchedule extends NBT, IDataChild<BlueprintData>, IRegionHolder
{

	String getTriggerId();

	void setTriggerId(String triggerId);

	IScheduleRecord getParent();

	void setParent(IScheduleRecord parent);

	NodeTree<IGuiCondition, ConditionLink> getConditionNodeTree();

	NodeTree<IPostResolveAction, NBT> getPostResolveActionNodeTree();

	Pos2D getConditionGuiPos();

	void setConditionGuiPos(Pos2D pos);

	Pos2D getPostResolveActionGuiPos();

	void setPostResolveActionGuiPos(Pos2D pos);

}
