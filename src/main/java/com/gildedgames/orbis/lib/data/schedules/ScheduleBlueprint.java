package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.core.tree.ConditionLink;
import com.gildedgames.orbis.lib.core.tree.NodeTree;
import com.gildedgames.orbis.lib.core.variables.conditions.IGuiCondition;
import com.gildedgames.orbis.lib.core.variables.post_resolve_actions.IPostResolveAction;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintDataPalette;
import com.gildedgames.orbis.lib.data.region.IColored;
import com.gildedgames.orbis.lib.data.region.IMutableRegion;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Rotation;

//TODO: Convert into schedule processor
public class ScheduleBlueprint implements NBT, IColored, ISchedule
{
	private String triggerId;

	private IMutableRegion bounds;

	private BlueprintData dataParent;

	private IScheduleRecord parent;

	private BlueprintDataPalette palette;

	private Rotation rotation;

	protected ScheduleBlueprint()
	{

	}

	public ScheduleBlueprint(String uniqueName, BlueprintDataPalette palette, IMutableRegion bounds, Rotation rotation)
	{
		this.triggerId = uniqueName;
		this.palette = palette;
		this.bounds = bounds;
		this.rotation = rotation;
	}

	public BlueprintDataPalette getPalette()
	{
		return this.palette;
	}

	public Rotation getRotation()
	{
		return this.rotation;
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
		funnel.set("palette", this.palette);
		tag.setString("rotation", this.rotation.name());
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.triggerId = tag.getString("triggerId");
		this.bounds = funnel.get("bounds");
		this.palette = funnel.get("palette");

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
