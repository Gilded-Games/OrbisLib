package com.gildedgames.orbis_api.data.json;

import com.gildedgames.orbis_api.data.management.IData;
import com.gildedgames.orbis_api.data.management.IDataManual;
import com.gildedgames.orbis_api.data.management.IDataMetadata;
import com.gildedgames.orbis_api.data.management.impl.DataMetadata;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.world.IWorldObject;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class JsonData implements IDataManual
{

	public static final String EXTENSION = "json";

	private IDataMetadata metadata;

	public JsonData()
	{
		this.metadata = new DataMetadata();
	}

	@Override
	public void preSaveToDisk(IWorldObject object)
	{

	}

	@Override
	public IData clone()
	{
		final JsonData data = new JsonData();

		final NBTTagCompound tag = new NBTTagCompound();

		this.write(tag);

		data.read(tag);

		return data;
	}

	@Override
	public int hashCode()
	{
		final HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(this.metadata.getIdentifier());

		return builder.toHashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj instanceof JsonData)
		{
			final JsonData o = (JsonData) obj;

			final EqualsBuilder builder = new EqualsBuilder();

			builder.append(this.metadata.getIdentifier(), o.metadata.getIdentifier());

			return builder.isEquals();
		}

		return false;
	}

	@Override
	public String getFileExtension()
	{
		return EXTENSION;
	}

	@Override
	public IDataMetadata getMetadata()
	{
		return this.metadata;
	}

	@Override
	public void setMetadata(IDataMetadata metadata)
	{
		this.metadata = metadata;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("metadata", this.metadata);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.metadata = funnel.get("metadata");
	}

	@Override
	public void readMetadataOnly(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.metadata = funnel.get("metadata");
	}
}
