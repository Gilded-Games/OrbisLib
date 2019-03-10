package com.gildedgames.orbis.lib.core;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.core.baking.BakedBlueprint;
import com.gildedgames.orbis.lib.core.baking.IBakedPosAction;
import com.gildedgames.orbis.lib.data.region.Region;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class PlacedBlueprint implements NBT
{
	private List<IBakedPosAction> pendingPosActions;

	private BlueprintDefinition def;

	private int definitionID;

	private String registryId;

	private ICreationData<?> data;

	private BakedBlueprint baked;

	private Region region;

	public PlacedBlueprint()
	{

	}

	public PlacedBlueprint(BakedBlueprint baked, final ICreationData<?> data)
	{
		this.def = baked.getDefinition();

		this.registryId = this.def.getRegistry().getRegistryId();
		this.definitionID = this.def.getRegistry().getID(this.def);

		this.data = data;
		this.baked = baked;

		this.pendingPosActions = this.baked.getBakedPositionActions();

		for (int i = 0; i < this.pendingPosActions.size(); i++)
		{
			IBakedPosAction action = this.pendingPosActions.get(i).copy();
			action.setPos(action.getPos().add(this.getCreationData().getPos()));

			this.pendingPosActions.set(i, action);
		}

		this.region = new Region(this.baked.getBakedRegion());
		this.region.add(this.getCreationData().getPos());
	}

	public BakedBlueprint getBaked()
	{
		if (this.baked == null)
		{
			this.baked = new BakedBlueprint(this.def, this.data);
		}

		return this.baked;
	}

	public BlueprintDefinition getDef()
	{
		return this.def;
	}

	public ICreationData<?> getCreationData()
	{
		return this.data;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		tag.setString("registryId", this.registryId);
		tag.setInteger("definitionId", this.definitionID);

		funnel.set("creation", this.data);
		funnel.set("region", this.region);

		funnel.setList("pendingPosActions", this.pendingPosActions);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.registryId = tag.getString("registryId");
		this.definitionID = tag.getInteger("definitionId");

		this.def = OrbisLib.services().findDefinitionRegistry(this.registryId).get(this.definitionID);

		this.data = funnel.get("creation");
		this.region = funnel.get("region");

		this.pendingPosActions = funnel.getList("pendingPosActions");
	}

	public Region getRegion()
	{
		return this.region;
	}

	public List<IBakedPosAction> getPendingPosActions()
	{
		return this.pendingPosActions;
	}
}
