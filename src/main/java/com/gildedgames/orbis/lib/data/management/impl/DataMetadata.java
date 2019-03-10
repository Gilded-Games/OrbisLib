package com.gildedgames.orbis.lib.data.management.impl;

import com.gildedgames.orbis.lib.data.management.IDataIdentifier;
import com.gildedgames.orbis.lib.data.management.IDataMetadata;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.util.mc.IText;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * A basic concrete implementation of IDataMetadata.
 * Can be decorated with ProjectMetadataDecorator.
 */
public class DataMetadata implements IDataMetadata
{
	@SerializedName("name")
	public String name = "";

	@SerializedName("display")
	public List<IText> display;

	@SerializedName("identifier")
	public IDataIdentifier identifier;

	@SerializedName("dependencies")
	private List<IDataIdentifier> dependencies;

	public DataMetadata()
	{
		this.display = Lists.newArrayList();
		this.dependencies = Lists.newArrayList();
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		tag.setString("name", this.name);
		funnel.set("identifier", this.identifier);

		funnel.setList("dependencies", this.dependencies);
		funnel.setList("display", this.display);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.name = tag.getString("name");
		this.identifier = funnel.get("identifier");

		this.dependencies = funnel.getList("dependencies");
		this.display = funnel.getList("display");
	}

	@Override
	public List<IText> getMetadataDisplay()
	{
		return this.display;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public void setName(final String name)
	{
		this.name = name;
	}

	@Override
	public List<IDataIdentifier> getDependencies()
	{
		return this.dependencies;
	}

	@Override
	public IDataIdentifier getIdentifier()
	{
		return this.identifier;
	}

	@Override
	public void setIdentifier(final IDataIdentifier identifier)
	{
		this.identifier = identifier;
	}

	@Override
	public String toString()
	{
		return this.name + (this.identifier != null ? " - " + this.identifier.toString() : "");
	}
}
