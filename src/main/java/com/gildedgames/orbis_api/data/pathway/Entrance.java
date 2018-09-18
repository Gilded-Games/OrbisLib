package com.gildedgames.orbis_api.data.pathway;

import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.tree.ConditionLink;
import com.gildedgames.orbis_api.core.tree.NodeTree;
import com.gildedgames.orbis_api.core.variables.conditions.IGuiConditionEntrance;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.framework.interfaces.EnumFacingMultiple;
import com.gildedgames.orbis_api.data.region.IMutableRegion;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import net.minecraft.nbt.NBTTagCompound;

public class Entrance implements IEntrance
{
	private NodeTree<IGuiConditionEntrance, ConditionLink> conditionNodeTree = new NodeTree<>();

	private IMutableRegion bounds;

	private PathwayData toConnectTo;

	private EnumFacingMultiple facing;

	private BlueprintData dataParent;

	private String triggerId = "";

	private Pos2D conditionGuiPos = Pos2D.ORIGIN;

	private Entrance()
	{

	}

	public Entrance(IMutableRegion bounds, PathwayData toConnectTo, EnumFacingMultiple facing)
	{
		this.bounds = bounds;
		this.toConnectTo = toConnectTo;
		this.facing = facing;
	}

	@Override
	public IMutableRegion getBounds()
	{
		return this.bounds;
	}

	@Override
	public PathwayData toConnectTo()
	{
		return this.toConnectTo;
	}

	@Override
	public EnumFacingMultiple getFacing()
	{
		return this.facing;
	}

	@Override
	public void setFacing(EnumFacingMultiple facing)
	{
		this.facing = facing;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("bounds", this.bounds);
		funnel.set("pathway", this.toConnectTo);
		tag.setString("facing", this.facing.getName());

		tag.setString("triggerId", this.triggerId);

		funnel.set("conditionNodeTree", this.conditionNodeTree);
		funnel.set("conditionGuiPos", this.conditionGuiPos, NBTFunnel.POS2D_SETTER);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.bounds = funnel.get("bounds");
		this.toConnectTo = funnel.get("pathway");
		this.facing = EnumFacingMultiple.byName(tag.getString("facing"));

		this.triggerId = tag.getString("triggerId");

		this.conditionNodeTree = funnel.getWithDefault("conditionNodeTree", this::getConditionNodeTree);
		this.conditionGuiPos = funnel.getWithDefault("conditionGuiPos", NBTFunnel.POS2D_GETTER, () -> this.conditionGuiPos);
	}

	@Override
	public int getColor()
	{
		return 0xd38dc7;
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
	public NodeTree<IGuiConditionEntrance, ConditionLink> getConditionNodeTree()
	{
		return this.conditionNodeTree;
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
}
