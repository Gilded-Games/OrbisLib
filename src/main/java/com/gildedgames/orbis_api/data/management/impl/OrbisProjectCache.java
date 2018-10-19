package com.gildedgames.orbis_api.data.management.impl;

import com.gildedgames.orbis_api.data.management.*;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

public class OrbisProjectCache implements IProjectCache
{

	private final Map<UUID, IDataMetadata> idToMetadata = Maps.newHashMap();

	private IProject project;

	private BiMap<UUID, IData> idToData = HashBiMap.create();

	private BiMap<UUID, String> idToLocation = HashBiMap.create();

	private OrbisProjectCache()
	{

	}

	public OrbisProjectCache(final IProject project)
	{
		this.project = project;
	}

	@Override
	public boolean hasData(final UUID dataId)
	{
		return this.idToData.containsKey(dataId);
	}

	@Override
	public void setProject(final IProject project)
	{
		this.project = project;
	}

	@Override
	public Collection<IData> getAllData()
	{
		return this.idToData.values();
	}

	@Override
	public void clear()
	{
		this.idToData.clear();
		this.idToLocation.clear();
		this.idToMetadata.clear();
	}

	@Override
	public <T extends IData> Optional<T> getData(final UUID dataId)
	{
		if (!this.idToData.containsKey(dataId))
		{
			return Optional.empty();
		}

		return Optional.of((T) this.idToData.get(dataId));
	}

	@Override
	public Optional<IDataMetadata> getMetadata(final UUID dataId)
	{
		if (!this.idToMetadata.containsKey(dataId))
		{
			return Optional.empty();
		}

		return Optional.of(this.idToMetadata.get(dataId));
	}

	@Override
	public void removeData(final UUID dataId)
	{
		this.idToData.remove(dataId);
		this.idToLocation.remove(dataId);
	}

	@Override
	public void setData(final IData data, String location)
	{
		location = location.replace("/", "\\");

		if (data.getMetadata().getIdentifier() == null || data.getMetadata().getIdentifier().getDataId() == null)
		{
			data.getMetadata().setIdentifier(this.createNextIdentifier());
		}

		UUID id = data.getMetadata().getIdentifier().getDataId();

		final boolean fromOtherProject =
				data.getMetadata().getIdentifier() != null && !this.project.getProjectIdentifier()
						.equals(data.getMetadata().getIdentifier().getProjectIdentifier());

		/* If the data file seems to be moved from another project, it'll reassign a new data id for it **/
		if (fromOtherProject)
		{
			data.getMetadata().setIdentifier(this.createNextIdentifier());

			id = data.getMetadata().getIdentifier().getDataId();
		}

		/*if (this.idToData.containsKey(id))
		{
			data.getMetadata().setIdentifier(this.createNextIdentifier());

			id = data.getMetadata().getIdentifier().getDataId();
		}*/

		this.idToData.put(id, data);

		if (!this.idToMetadata.containsKey(id) || !Objects.equals(this.idToMetadata.get(id), data.getMetadata()))
		{
			this.idToMetadata.put(id, data.getMetadata());
		}

		this.setDataLocation(id, location);

		final int index = location.contains(String.valueOf(File.separatorChar)) ? location.lastIndexOf(File.separatorChar) + 1 : 0;

		data.getMetadata().setName(location.substring(index).replace("." + data.getFileExtension(), ""));

		this.project.getMetadata().setLastChanged(LocalDateTime.now());
	}

	@Override
	public void setDataLocation(final UUID dataId, final String location)
	{
		if (!this.idToLocation.containsKey(dataId) || !Objects.equals(this.idToLocation.get(dataId), location))
		{
			if (this.idToLocation.containsValue(location))
			{
				this.idToLocation.forcePut(dataId, location);
			}
			else
			{
				this.idToLocation.put(dataId, location);
			}
		}
	}

	@Override
	public Optional<String> getDataLocation(final UUID dataId)
	{
		if (!this.idToLocation.containsKey(dataId))
		{
			return Optional.empty();
		}

		return Optional.of(this.idToLocation.get(dataId));
	}

	@Override
	public Optional<UUID> getDataId(String location)
	{
		location = location.replace("/", "\\");

		if (this.idToLocation.inverse().containsKey(location))
		{
			return Optional.of(this.idToLocation.inverse().get(location));
		}

		return Optional.empty();
	}

	@Override
	public IDataIdentifier createNextIdentifier()
	{
		UUID dataId = UUID.randomUUID();

		while (this.idToData.containsKey(dataId))
		{
			dataId = UUID.randomUUID();
		}

		return new DataIdentifier(this.project.getProjectIdentifier(), dataId);
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.setMap("idToData", this.idToData, NBTFunnel.UUID_SETTER, NBTFunnel.setter());
		funnel.setMap("idToLocation", this.idToLocation, NBTFunnel.UUID_SETTER, NBTFunnel.STRING_SETTER);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.idToData = HashBiMap.create(funnel.getMap("idToData", NBTFunnel.UUID_GETTER, NBTFunnel.getter()));
		this.idToLocation = HashBiMap.create(funnel.getMap("idToLocation", NBTFunnel.UUID_GETTER, NBTFunnel.STRING_GETTER));

		for (final Map.Entry<UUID, IData> entry : this.idToData.entrySet())
		{
			final UUID id = entry.getKey();
			final IData data = entry.getValue();

			this.idToMetadata.put(id, data.getMetadata());
		}
	}
}
