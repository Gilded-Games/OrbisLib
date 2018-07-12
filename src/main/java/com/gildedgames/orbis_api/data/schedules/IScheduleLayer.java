package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.block.BlockFilter;
import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.tree.ConditionLink;
import com.gildedgames.orbis_api.core.tree.INode;
import com.gildedgames.orbis_api.core.tree.LayerLink;
import com.gildedgames.orbis_api.core.tree.NodeTree;
import com.gildedgames.orbis_api.core.variables.conditions.IGuiCondition;
import com.gildedgames.orbis_api.core.variables.post_resolve_actions.IPostResolveAction;
import com.gildedgames.orbis_api.data.IDataChild;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.region.IDimensions;
import com.gildedgames.orbis_api.util.mc.NBT;

import javax.annotation.Nonnull;

public interface IScheduleLayer extends NBT, IDataChild<BlueprintData>
{
	INode<IScheduleLayer, LayerLink> getNodeParent();

	void setNodeParent(INode<IScheduleLayer, LayerLink> nodeParent);

	IFilterOptions getOptions();

	IPositionRecord<BlockFilter> getFilterRecord();

	IScheduleRecord getScheduleRecord();

	void setDimensions(IDimensions dimensions);

	@Nonnull
	NodeTree<IPostResolveAction, NBT> getPostResolveActionNodeTree();

	void setPostResolveActionNodeTree(NodeTree<IPostResolveAction, NBT> tree);

	@Nonnull
	NodeTree<IGuiCondition, ConditionLink> getConditionNodeTree();

	void setConditionNodeTree(NodeTree<IGuiCondition, ConditionLink> tree);

	Pos2D getGuiPos();

	void setGuiPos(Pos2D pos);

	Pos2D getConditionGuiPos();

	void setConditionGuiPos(Pos2D pos);

	Pos2D getPostResolveActionGuiPos();

	void setPostResolveActionGuiPos(Pos2D pos);
}
