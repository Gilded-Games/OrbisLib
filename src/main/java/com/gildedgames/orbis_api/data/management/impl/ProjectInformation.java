package com.gildedgames.orbis_api.data.management.impl;

import com.gildedgames.orbis_api.data.management.IProjectIdentifier;
import com.gildedgames.orbis_api.data.management.IProjectMetadata;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.google.gson.annotations.SerializedName;
import net.minecraft.nbt.NBTTagCompound;

public class ProjectInformation implements NBT
{
	@SerializedName("metadata")
	private IProjectMetadata metadata;

	@SerializedName("identifier")
	private IProjectIdentifier identifier;

	private ProjectInformation()
	{

	}

	public ProjectInformation(IProjectIdentifier identifier, IProjectMetadata metadata)
	{
		this.identifier = identifier;
		this.metadata = metadata;
	}

	/**
	 * This unique identifier to distinguish it between other projects.
	 * Includes a name and authors.
	 * @return
	 */
	public IProjectIdentifier getIdentifier()
	{
		return this.identifier;
	}

	/**
	 * Used for displaying information about this project to the user.
	 * @return The project's metadata.
	 */
	public IProjectMetadata getMetadata()
	{
		return this.metadata;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("identifier", this.identifier);
		funnel.set("metadata", this.metadata);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.identifier = funnel.get("identifier");
		this.metadata = funnel.get("metadata");
	}
}
