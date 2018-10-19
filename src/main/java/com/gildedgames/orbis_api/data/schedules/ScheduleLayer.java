package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.tree.*;
import com.gildedgames.orbis_api.core.variables.conditions.IGuiCondition;
import com.gildedgames.orbis_api.core.variables.post_resolve_actions.IPostResolveAction;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.region.IDimensions;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.List;

public class ScheduleLayer implements IScheduleLayer, INodeTreeListener<IGuiCondition, ConditionLink>
{
	private IDimensions dimensions;

	private IPositionRecord<IBlockState> stateRecord;

	private IScheduleRecord scheduleRecord = new ScheduleRecord();

	private BlueprintData dataParent;

	private NodeTree<IGuiCondition, ConditionLink> conditionNodeTree = new NodeTree<>();

	private NodeTree<IPostResolveAction, NBT> postResolveActionNodeTree = new NodeTree<>();

	private Pos2D guiPos = Pos2D.ORIGIN, conditionGuiPos = Pos2D.ORIGIN, postResolveActionGuiPos = Pos2D.ORIGIN;

	private INode<IScheduleLayer, LayerLink> nodeParent;

	private boolean visible = true;

	private List<IScheduleLayerListener> listeners = Lists.newArrayList();

	private IScheduleLayerOptions options;

	private INodeTreeListener<IPostResolveAction, NBT> postResolveListener = new INodeTreeListener<IPostResolveAction, NBT>()
	{
		@Override
		public void onSetData(INode<IPostResolveAction, NBT> node, IPostResolveAction iPostResolveAction, int id)
		{
			if (ScheduleLayer.this.dataParent != null)
			{
				ScheduleLayer.this.dataParent.markDirty();
			}
		}

		@Override
		public void onPut(INode<IPostResolveAction, NBT> node, int id)
		{
			if (ScheduleLayer.this.dataParent != null)
			{
				ScheduleLayer.this.dataParent.markDirty();
			}
		}

		@Override
		public void onRemove(INode<IPostResolveAction, NBT> node, int id)
		{
			if (ScheduleLayer.this.dataParent != null)
			{
				ScheduleLayer.this.dataParent.markDirty();
			}
		}
	};

	private ScheduleLayer()
	{
		this.scheduleRecord.setParent(this);

		this.options = new ScheduleLayerOptions();
	}

	public ScheduleLayer(final String displayName, final IDimensions dimensions)
	{
		this();

		this.options.getDisplayNameVar().setData(displayName);

		this.dimensions = dimensions;

		this.stateRecord = new BlockStateRecord(this.dimensions.getWidth(), this.dimensions.getHeight(), this.dimensions.getLength());

		this.conditionNodeTree.listen(this);
		this.postResolveActionNodeTree.listen(this.postResolveListener);
	}

	@Override
	public void listen(IScheduleLayerListener listener)
	{
		if (!this.listeners.contains(listener))
		{
			this.listeners.add(listener);
		}
	}

	@Override
	public boolean unlisten(IScheduleLayerListener listener)
	{
		return this.listeners.remove(listener);
	}

	@Override
	public boolean isVisible()
	{
		return this.visible;
	}

	@Override
	public void setVisible(boolean visible)
	{
		this.visible = visible;

		if (this.dataParent != null)
		{
			this.dataParent.markDirty();
		}

		this.listeners.forEach((l) -> l.onSetVisible(visible));
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
	public IPositionRecord<IBlockState> getStateRecord()
	{
		return this.stateRecord;
	}

	@Override
	public IScheduleRecord getScheduleRecord()
	{
		return this.scheduleRecord;
	}

	@Override
	public IScheduleLayerOptions getOptions()
	{
		return this.options;
	}

	@Override
	public void setDimensions(final IDimensions dimensions)
	{
		this.dimensions = dimensions;

		if (this.stateRecord == null)
		{
			this.stateRecord = new BlockStateRecord(this.dimensions.getWidth(), this.dimensions.getHeight(), this.dimensions.getLength());
		}
	}

	@Nonnull
	@Override
	public NodeTree<IPostResolveAction, NBT> getPostResolveActionNodeTree()
	{
		return this.postResolveActionNodeTree;
	}

	@Override
	public void setPostResolveActionNodeTree(NodeTree<IPostResolveAction, NBT> tree)
	{
		this.postResolveActionNodeTree = tree;
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
	public Pos2D getPostResolveActionGuiPos()
	{
		return this.postResolveActionGuiPos;
	}

	@Override
	public void setPostResolveActionGuiPos(Pos2D pos)
	{
		this.postResolveActionGuiPos = pos;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("stateRecord", this.stateRecord);
		funnel.set("scheduleRecord", this.scheduleRecord);

		funnel.set("guiPos", this.guiPos, NBTFunnel.POS2D_SETTER);
		funnel.set("conditionGuiPos", this.conditionGuiPos, NBTFunnel.POS2D_SETTER);
		funnel.set("postResolveActionGuiPos", this.postResolveActionGuiPos, NBTFunnel.POS2D_SETTER);

		funnel.set("conditionNodeTree", this.conditionNodeTree);
		funnel.set("postResolveActionNodeTree", this.postResolveActionNodeTree);

		tag.setBoolean("visible", this.visible);

		funnel.set("scheduleOptions", this.options);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.stateRecord = funnel.get("stateRecord");

		this.scheduleRecord = funnel.getWithDefault("scheduleRecord", this::getScheduleRecord);

		if (this.scheduleRecord != null)
		{
			this.scheduleRecord.setParent(this);
		}

		this.guiPos = funnel.get("guiPos", NBTFunnel.POS2D_GETTER);
		this.conditionGuiPos = funnel.getWithDefault("conditionGuiPos", NBTFunnel.POS2D_GETTER, () -> this.conditionGuiPos);
		this.postResolveActionGuiPos = funnel.getWithDefault("postResolveActionGuiPos", NBTFunnel.POS2D_GETTER, () -> this.postResolveActionGuiPos);

		this.conditionNodeTree = funnel.getWithDefault("conditionNodeTree", () -> this.conditionNodeTree);
		this.postResolveActionNodeTree = funnel.getWithDefault("postResolveActionNodeTree", () -> this.postResolveActionNodeTree);

		this.conditionNodeTree.listen(this);
		this.postResolveActionNodeTree.listen(this.postResolveListener);

		if (tag.hasKey("visible"))
		{
			this.visible = tag.getBoolean("visible");
		}

		this.options = funnel.getWithDefault("scheduleOptions", () -> this.options);
	}

	@Override
	public void onSetData(INode<IGuiCondition, ConditionLink> node, IGuiCondition condition, int id)
	{
		if (this.dataParent != null)
		{
			this.dataParent.markDirty();
		}
	}

	@Override
	public void onPut(INode<IGuiCondition, ConditionLink> node, int id)
	{
		if (this.dataParent != null)
		{
			this.dataParent.markDirty();
		}
	}

	@Override
	public void onRemove(INode<IGuiCondition, ConditionLink> node, int id)
	{
		if (this.dataParent != null)
		{
			this.dataParent.markDirty();
		}
	}

	@Override
	public Class<? extends BlueprintData> getDataClass()
	{
		return BlueprintData.class;
	}

	@Override
	public BlueprintData getDataParent()
	{
		return this.dataParent;
	}

	@Override
	public void setDataParent(BlueprintData blueprintData)
	{
		this.dataParent = blueprintData;

		this.scheduleRecord.setDataParent(blueprintData);
		this.conditionNodeTree.setDataParent(blueprintData);
		this.postResolveActionNodeTree.setDataParent(blueprintData);
	}
}
