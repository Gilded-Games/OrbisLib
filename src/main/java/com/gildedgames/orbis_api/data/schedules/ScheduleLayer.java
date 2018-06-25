package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.block.BlockFilter;
import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.tree.*;
import com.gildedgames.orbis_api.core.variables.conditions.IGuiCondition;
import com.gildedgames.orbis_api.data.region.IDimensions;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.world.IWorldObject;
import net.minecraft.nbt.NBTTagCompound;

public class ScheduleLayer implements IScheduleLayer, INodeTreeListener<IGuiCondition, ConditionLink>
{
	private IDimensions dimensions;

	private IPositionRecord<BlockFilter> positionRecord;

	private IScheduleRecord scheduleRecord = new ScheduleRecord();

	private IWorldObject worldObjectParent;

	private IFilterOptions options = new FilterOptions();

	private NodeTree<IGuiCondition, ConditionLink> conditionNodeTree = new NodeTree<>();

	private Pos2D guiPos = Pos2D.ORIGIN, conditionGuiPos = Pos2D.ORIGIN;

	private INode<IScheduleLayer, LayerLink> nodeParent;

	private ScheduleLayer()
	{
		this.scheduleRecord.setParent(this);
	}

	public ScheduleLayer(final String displayName, final IDimensions dimensions)
	{
		this.getOptions().getDisplayNameVar().setData(displayName);
		this.dimensions = dimensions;

		this.positionRecord = new FilterRecord(this.dimensions.getWidth(), this.dimensions.getHeight(), this.dimensions.getLength());

		this.scheduleRecord.setParent(this);
		this.conditionNodeTree.listen(this);
	}

	@Override
	public INode<IScheduleLayer, LayerLink> getNodeParent()
	{
		return this.nodeParent;
	}

	@Override
	public void setNodeParent(INode<IScheduleLayer, LayerLink> nodeParent)
	{
		this.nodeParent = nodeParent;
	}

	@Override
	public IFilterOptions getOptions()
	{
		return this.options;
	}

	@Override
	public IPositionRecord<BlockFilter> getFilterRecord()
	{
		return this.positionRecord;
	}

	@Override
	public IScheduleRecord getScheduleRecord()
	{
		return this.scheduleRecord;
	}

	@Override
	public void setDimensions(final IDimensions dimensions)
	{
		this.dimensions = dimensions;
	}

	@Override
	public NodeTree<IGuiCondition, ConditionLink> getConditionNodeTree()
	{
		return this.conditionNodeTree;
	}

	@Override
	public void setConditionNodeTree(NodeTree<IGuiCondition, ConditionLink> tree)
	{
		this.conditionNodeTree = tree;
	}

	@Override
	public Pos2D getGuiPos()
	{
		return this.guiPos;
	}

	@Override
	public void setGuiPos(Pos2D pos)
	{
		this.guiPos = pos;
	}

	@Override
	public Pos2D getConditionGuiPos()
	{
		return this.conditionGuiPos;
	}

	@Override
	public void setConditionGuiPos(Pos2D pos)
	{
		this.conditionGuiPos = pos;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("positionRecord", this.positionRecord);
		funnel.set("scheduleRecord", this.scheduleRecord);

		funnel.set("options", this.options);

		funnel.set("guiPos", this.guiPos, NBTFunnel.POS2D_SETTER);
		funnel.set("conditionGuiPos", this.conditionGuiPos, NBTFunnel.POS2D_SETTER);

		funnel.set("conditionNodeTree", this.conditionNodeTree);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.positionRecord = funnel.get("positionRecord");

		this.scheduleRecord = funnel.getWithDefault("scheduleRecord", this::getScheduleRecord);

		this.options = funnel.getWithDefault("options", FilterOptions::new);

		if (this.scheduleRecord != null)
		{
			this.scheduleRecord.setParent(this);
		}

		this.guiPos = funnel.get("guiPos", NBTFunnel.POS2D_GETTER);
		this.conditionGuiPos = funnel.getWithDefault("conditionGuiPos", NBTFunnel.POS2D_GETTER, () -> this.conditionGuiPos);

		this.conditionNodeTree = funnel.getWithDefault("conditionNodeTree", () -> this.conditionNodeTree);

		this.conditionNodeTree.listen(this);
	}

	@Override
	public IWorldObject getWorldObjectParent()
	{
		return this.worldObjectParent;
	}

	@Override
	public void setWorldObjectParent(IWorldObject parent)
	{
		this.worldObjectParent = parent;

		this.scheduleRecord.setWorldObjectParent(parent);
		this.conditionNodeTree.setWorldObjectParent(parent);
	}

	@Override
	public void onPut(INode<IGuiCondition, ConditionLink> node, int id)
	{
		if (this.worldObjectParent != null)
		{
			this.worldObjectParent.markDirty();
		}
	}

	@Override
	public void onRemove(INode<IGuiCondition, ConditionLink> node, int id)
	{
		if (this.worldObjectParent != null)
		{
			this.worldObjectParent.markDirty();
		}
	}
}
