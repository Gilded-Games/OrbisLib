package com.gildedgames.orbis_api.data.management.impl;

import com.gildedgames.orbis_api.data.management.IProjectIdentifier;
import com.google.gson.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * A basic concrete implementation of IProjectIdentifier.
 */
public class ProjectIdentifier implements IProjectIdentifier
{
	private UUID projectId;

	private String originalCreator;

	private ProjectIdentifier()
	{

	}

	public ProjectIdentifier(final UUID projectId, final String originalCreator)
	{
		this.projectId = projectId;
		this.originalCreator = originalCreator;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		tag.setUniqueId("id", this.projectId);
		tag.setString("originalCreator", this.originalCreator);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		this.projectId = tag.getUniqueId("id");
		this.originalCreator = tag.getString("originalCreator");
	}

	@Override
	public UUID getProjectId()
	{
		return this.projectId;
	}

	@Override
	public String getOriginalCreator()
	{
		return this.originalCreator;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj == this)
		{
			return true;
		}

		if (obj instanceof IProjectIdentifier)
		{
			final IProjectIdentifier id = (IProjectIdentifier) obj;
			final EqualsBuilder builder = new EqualsBuilder();

			builder.append(this.getProjectId(), id.getProjectId());
			builder.append(this.getOriginalCreator(), id.getOriginalCreator());

			return builder.isEquals();
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		final HashCodeBuilder builder = new HashCodeBuilder(17, 37);

		builder.append(this.getProjectId());
		builder.append(this.getOriginalCreator());

		return builder.toHashCode();
	}

	@Override
	public String toString()
	{
		return this.projectId + ":" + this.originalCreator;
	}

	public class Serializer implements JsonDeserializer<IProjectIdentifier>, JsonSerializer<IProjectIdentifier>
	{
		@Override
		public IProjectIdentifier deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws
				JsonParseException
		{
			String[] values = JsonUtils.getString(element, "location").split(":");

			UUID dataId = UUID.fromString(values[0]);
			String originalCreator = values[1];

			return new ProjectIdentifier(dataId, originalCreator);
		}

		@Override
		public JsonElement serialize(IProjectIdentifier id, Type type, JsonSerializationContext context)
		{
			return new JsonPrimitive(id.toString());
		}
	}

}
