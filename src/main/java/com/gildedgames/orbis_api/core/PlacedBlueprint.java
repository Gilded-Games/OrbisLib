package com.gildedgames.orbis_api.core;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.core.baking.BakedBlueprint;
import com.gildedgames.orbis_api.core.baking.IBakedPosAction;
import com.gildedgames.orbis_api.data.region.Region;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class PlacedBlueprint implements NBT
{
	private List<IBakedPosAction> pendingPosActions;

	private World world;

	private BlueprintDefinition def;

	private int definitionID;

	private String registryId;

	private ICreationData<?> data;

	private boolean hasGeneratedAChunk;

	private BakedBlueprint baked;

	private PlacedBlueprint()
	{

	}

	private PlacedBlueprint(World world)
	{
		this.world = world;
	}

	public PlacedBlueprint(final World world, final BlueprintDefinition def, BakedBlueprint baked, final ICreationData<?> data)
	{
		this.world = world;
		this.def = def;

		this.registryId = def.getRegistry().getRegistryId();
		this.definitionID = def.getRegistry().getID(this.def);

		this.data = data;

		this.baked = baked;
		this.pendingPosActions = new ArrayList<>(this.baked.getBakedPosActions());

		for (int i = 0; i < this.pendingPosActions.size(); i++)
		{
			IBakedPosAction action = this.pendingPosActions.get(i).copy();
			action.setPos(action.getPos().add(this.getCreationData().getPos()));

			this.pendingPosActions.set(i, action);
		}
	}

	public PlacedBlueprint(final World world, final NBTTagCompound tag)
	{
		this.world = world;

		this.read(tag);
	}

	public BakedBlueprint getBaked()
	{
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

	public void markGeneratedAChunk()
	{
		this.hasGeneratedAChunk = true;
	}

	public boolean hasGeneratedAChunk()
	{
		return this.hasGeneratedAChunk;
	}

	@Override
	public boolean equals(final Object obj)
	{
		boolean flag = false;

		if (obj == this)
		{
			flag = true;
		}
		else if (obj instanceof PlacedBlueprint)
		{
			final PlacedBlueprint o = (PlacedBlueprint) obj;
			final EqualsBuilder builder = new EqualsBuilder();

			builder.append(this.definitionID, o.definitionID);
			builder.append(this.data, o.data);
			builder.append(this.hasGeneratedAChunk, o.hasGeneratedAChunk);

			flag = builder.isEquals();
		}

		return flag;
	}

	@Override
	public int hashCode()
	{
		final HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(this.definitionID);
		builder.append(this.data);
		builder.append(this.hasGeneratedAChunk);

		return builder.toHashCode();
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		tag.setString("registryId", this.registryId);
		tag.setInteger("definitionId", this.definitionID);

		funnel.set("creation", this.data);

		tag.setBoolean("hasGeneratedAChunk", this.hasGeneratedAChunk);

		funnel.set("baked", this.baked);
		funnel.setList("pendingPosActions", this.pendingPosActions);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.registryId = tag.getString("registryId");
		this.definitionID = tag.getInteger("definitionId");

		this.def = OrbisAPI.services().findDefinitionRegistry(this.registryId).get(this.definitionID);

		this.data = funnel.get("creation");

		this.hasGeneratedAChunk = tag.getBoolean("hasGeneratedAChunk");

		this.baked = funnel.get("baked");
		this.pendingPosActions = funnel.getList("pendingPosActions");
	}

	@Override
	public PlacedBlueprint clone()
	{
		//TODO: Deep clone for BakedBlueprint?
		final PlacedBlueprint clone = new PlacedBlueprint(this.world, this.def, this.baked.clone(), this.data.clone());

		clone.hasGeneratedAChunk = this.hasGeneratedAChunk;

		return clone;
	}

	public Region getRegion()
	{
		Region region = new Region(this.baked.getBakedRegion());
		region.add(this.getCreationData().getPos());

		return region;
	}

	public List<IBakedPosAction> getPendingPosActions()
	{
		return this.pendingPosActions;
	}
}
