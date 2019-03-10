package com.gildedgames.orbis.lib.data.management.impl;

import com.gildedgames.orbis.lib.data.management.IDataIdentifier;
import com.gildedgames.orbis.lib.data.management.IProjectIdentifier;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.google.gson.annotations.SerializedName;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class DataIdentifier implements IDataIdentifier
{

	@SerializedName("dataId")
	private UUID dataId;

	@SerializedName("projectIdentifier")
	private IProjectIdentifier projectIdentifier;

	private DataIdentifier()
	{

	}

	private DataIdentifier(UUID dataId)
	{
		this.dataId = dataId;
	}

	public DataIdentifier(final IProjectIdentifier identifier, final UUID dataId)
	{
		this.projectIdentifier = identifier;
		this.dataId = dataId;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		if (this.dataId != null)
		{
			tag.setUniqueId("dataId", this.dataId);
		}

		funnel.set("projectIdentifier", this.projectIdentifier);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.dataId = tag.hasUniqueId("dataId") ? tag.getUniqueId("dataId") : null;
		this.projectIdentifier = funnel.get("projectIdentifier");
	}

	@Override
	public UUID getDataId()
	{
		return this.dataId;
	}

	@Override
	public IProjectIdentifier getProjectIdentifier()
	{
		return this.projectIdentifier;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj == this)
		{
			return true;
		}

		if (obj instanceof IDataIdentifier)
		{
			final IDataIdentifier id = (IDataIdentifier) obj;
			final EqualsBuilder builder = new EqualsBuilder();

			builder.append(this.getDataId(), id.getDataId());
			builder.append(this.getProjectIdentifier(), id.getProjectIdentifier());

			return builder.isEquals();
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		final HashCodeBuilder builder = new HashCodeBuilder(3, 7);

		builder.append(this.getDataId());
		builder.append(this.getProjectIdentifier());

		return builder.toHashCode();
	}

	@Override
	public String toString()
	{
		return this.dataId + (this.projectIdentifier != null ? ":" + this.projectIdentifier.toString() : "");
	}

}
