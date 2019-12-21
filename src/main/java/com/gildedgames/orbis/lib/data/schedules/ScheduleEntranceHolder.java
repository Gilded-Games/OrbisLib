package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.core.tree.ConditionLink;
import com.gildedgames.orbis.lib.core.tree.NodeTree;
import com.gildedgames.orbis.lib.core.variables.conditions.IGuiCondition;
import com.gildedgames.orbis.lib.core.variables.post_resolve_actions.IPostResolveAction;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.management.IDataIdentifier;
import com.gildedgames.orbis.lib.data.region.IColored;
import com.gildedgames.orbis.lib.data.region.IMutableRegion;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Rotation;

public class ScheduleEntranceHolder implements NBT, IColored, ISchedule
{
	private String triggerId;

	private IMutableRegion bounds;

	private BlueprintData dataParent;

	private IScheduleRecord parent;

	private IDataIdentifier entranceHolderId;

	private Rotation rotation;

	private ScheduleEntranceHolder()
	{

	}

	public ScheduleEntranceHolder(String triggerId, IDataIdentifier entranceHolderId, IMutableRegion bounds, Rotation rotation)
	{
		this.triggerId = triggerId;
		this.entranceHolderId = entranceHolderId;
		this.bounds = bounds;
		this.rotation = rotation;
	}

	public IDataIdentifier getEntranceHolder()
	{
		return this.entranceHolderId;
	}

	public Rotation getRotation()
	{
		return this.rotation;
	}

	public void setRotation(Rotation rotation)
	{
		this.rotation = rotation;
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
		return null;
	}

	@Override
	public NodeTree<IPostResolveAction, NBT> getPostResolveActionNodeTree()
	{
		return null;
	}

	@Override
	public Pos2D getConditionGuiPos()
	{
		return null;
	}

	@Override
	public void setConditionGuiPos(Pos2D pos)
	{

	}

	@Override
	public Pos2D getPostResolveActionGuiPos()
	{
		return null;
	}

	@Override
	public void setPostResolveActionGuiPos(Pos2D pos)
	{

	}

	@Override
	public void write(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		tag.setString("triggerId", this.triggerId);
		funnel.set("bounds", this.bounds);
		funnel.set("entranceHolderId", this.entranceHolderId);
		tag.setString("rotation", this.rotation.name());
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.triggerId = tag.getString("triggerId");
		this.bounds = funnel.get("bounds");
		this.entranceHolderId = funnel.get("entranceHolderId");

		String rotationName = tag.getString("rotation");
		this.rotation = rotationName.isEmpty() ? Rotation.NONE : Rotation.valueOf(rotationName);
	}

	@Override
	public int getColor()
	{
		return 0xd19044;
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
