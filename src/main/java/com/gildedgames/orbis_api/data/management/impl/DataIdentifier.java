package com.gildedgames.orbis_api.data.management.impl;

import com.gildedgames.orbis_api.data.management.IDataIdentifier;
import com.gildedgames.orbis_api.data.management.IProjectIdentifier;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.google.gson.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.lang.reflect.Type;

public class DataIdentifier implements IDataIdentifier
{

	private int dataId;

	private IProjectIdentifier projectIdentifier;

	private DataIdentifier()
	{

	}

	private DataIdentifier(int dataId)
	{
		this.dataId = dataId;
	}

	public DataIdentifier(final IProjectIdentifier identifier, final int dataId)
	{
		this.projectIdentifier = identifier;
		this.dataId = dataId;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		tag.setInteger("dataId", this.dataId);
		funnel.set("projectIdentifier", this.projectIdentifier);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.dataId = tag.getInteger("dataId");
		this.projectIdentifier = funnel.get("projectIdentifier");
	}

	@Override
	public int getDataId()
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

	public static class Serializer implements JsonDeserializer<IDataIdentifier>, JsonSerializer<IDataIdentifier>
	{
		@Override
		public IDataIdentifier deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws
				JsonParseException
		{
			String[] values = JsonUtils.getString(element, "location").split(":");

			if (values.length <= 1)
			{
				int dataId = Integer.valueOf(values[0]);

				return new DataIdentifier(dataId);
			}

			if (values.length >= 3)
			{
				int dataId = Integer.valueOf(values[0]);

				String projectId = values[1];
				String creator = values[2];

				return new DataIdentifier(new ProjectIdentifier(projectId, creator), dataId);
			}

			return null;
		}

		@Override
		public JsonElement serialize(IDataIdentifier id, Type type, JsonSerializationContext context)
		{
			return new JsonPrimitive(id.toString());
		}
	}

}
