package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.core.tree.ConditionLink;
import com.gildedgames.orbis.lib.core.tree.INode;
import com.gildedgames.orbis.lib.core.tree.LayerLink;
import com.gildedgames.orbis.lib.core.tree.NodeTree;
import com.gildedgames.orbis.lib.core.variables.conditions.IGuiCondition;
import com.gildedgames.orbis.lib.core.variables.post_resolve_actions.IPostResolveAction;
import com.gildedgames.orbis.lib.data.IDataChild;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.region.IDimensions;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.block.BlockState;

import javax.annotation.Nonnull;

public interface IScheduleLayer extends NBT, IDataChild<BlueprintData>
{
	void listen(IScheduleLayerListener listener);

	boolean unlisten(IScheduleLayerListener listener);

	/**
	 * Should be true by default, when created.
	 * @return Whether or not this layer renders.
	 */
	boolean isVisible();

	void setVisible(boolean visible);

	INode<IScheduleLayer, LayerLink> getNodeParent();

	void setNodeParent(INode<IScheduleLayer, LayerLink> nodeParent);

	IPositionRecord<BlockState> getStateRecord();

	IScheduleRecord getScheduleRecord();

	IScheduleLayerOptions getOptions();

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
