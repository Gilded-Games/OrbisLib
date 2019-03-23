package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.core.tree.ConditionLink;
import com.gildedgames.orbis.lib.core.tree.NodeTree;
import com.gildedgames.orbis.lib.core.variables.conditions.IGuiCondition;
import com.gildedgames.orbis.lib.core.variables.post_resolve_actions.IPostResolveAction;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.region.IColored;
import com.gildedgames.orbis.lib.data.region.IMutableRegion;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.nbt.NBTTagCompound;

public class ScheduleRegion implements NBT, IColored, ISchedule
{
	private NodeTree<IGuiCondition, ConditionLink> conditionNodeTree = new NodeTree<>();

	private NodeTree<IPostResolveAction, NBT> postResolveActionNodeTree = new NodeTree<>();

	private String triggerId;

	private IMutableRegion bounds;

	private BlueprintData dataParent;

	private IScheduleRecord parent;

	private int color = 0xd19044;

	private Pos2D conditionGuiPos = Pos2D.ORIGIN, postResolveActionGuiPos = Pos2D.ORIGIN;

	private ScheduleRegion()
	{

	}

	public ScheduleRegion(String uniqueName, IMutableRegion bounds)
	{
		this.triggerId = uniqueName;
		this.bounds = bounds;
	}

	@Override
	public String getTriggerId()
	{
		return this.triggerId;
	}

	@Override
	public void setTriggerId(String triggerId)
	{
		this.triggerId = triggerId;
	}

	@Override
	public IScheduleRecord getParent()
	{
		return this.parent;
	}

	@Override
	public void setParent(IScheduleRecord parent)
	{
		this.parent = parent;
	}

	@Override
	public IMutableRegion getBounds()
	{
		return this.bounds;
	}

	@Override
	public NodeTree<IGuiCondition, ConditionLink> getConditionNodeTree()
	{
		return this.conditionNodeTree;
	}

	@Override
	public NodeTree<IPostResolveAction, NBT> getPostResolveActionNodeTree()
	{
		return this.postResolveActionNodeTree;
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
	public void write(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		tag.putString("triggerId", this.triggerId);
		funnel.set("bounds", this.bounds);
		tag.putInt("color", this.color);
		funnel.set("conditionNodeTree", this.conditionNodeTree);
		funnel.set("postResolveActionNodeTree", this.postResolveActionNodeTree);

		funnel.set("conditionGuiPos", this.conditionGuiPos, NBTFunnel.POS2D_SETTER);
		funnel.set("postResolveActionGuiPos", this.postResolveActionGuiPos, NBTFunnel.POS2D_SETTER);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.triggerId = tag.getString("triggerId");
		this.bounds = funnel.get("bounds");
		this.color = tag.getInt("color");
		this.conditionNodeTree = funnel.getWithDefault("conditionNodeTree", this::getConditionNodeTree);
		this.postResolveActionNodeTree = funnel.getWithDefault("postResolveActionNodeTree", this::getPostResolveActionNodeTree);

		this.conditionGuiPos = funnel.getWithDefault("conditionGuiPos", NBTFunnel.POS2D_GETTER, () -> this.conditionGuiPos);
		this.postResolveActionGuiPos = funnel.getWithDefault("postResolveActionGuiPos", NBTFunnel.POS2D_GETTER, () -> this.postResolveActionGuiPos);
	}

	@Override
	public int getColor()
	{
		return this.color;
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
	}
}
