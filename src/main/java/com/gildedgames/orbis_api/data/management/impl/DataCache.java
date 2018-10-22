package com.gildedgames.orbis_api.data.management.impl;

import com.gildedgames.orbis_api.data.management.IData;
import com.gildedgames.orbis_api.data.management.IDataCache;
import com.gildedgames.orbis_api.data.management.IDataMetadata;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DataCache implements IDataCache
{
	private String cacheId;

	private Map<UUID, IData> idToData = Maps.newHashMap();

	private Map<UUID, IDataMetadata> idToMetadata = Maps.newHashMap();

	private int nextId;

	private DataCache()
	{

	}

	public DataCache(final String cacheId)
	{
		this.cacheId = cacheId;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		tag.setString("cacheId", this.cacheId);
		tag.setInteger("nextId", this.nextId);

		funnel.setMap("idToData", this.idToData, NBTFunnel.UUID_SETTER, NBTFunnel.setter());
		funnel.setMap("idToMetadata", this.idToMetadata, NBTFunnel.UUID_SETTER, NBTFunnel.setter());
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.cacheId = tag.getString("cacheId");
		this.nextId = tag.getInteger("nextId");

		this.idToData = funnel.getMap("idToData", NBTFunnel.UUID_GETTER, NBTFunnel.getter());
		this.idToMetadata = funnel.getMap("idToMetadata", NBTFunnel.UUID_GETTER, NBTFunnel.getter());

		this.idToMetadata.forEach((uuid, metadata) ->
		{
			IData data = this.idToData.get(uuid);

			if (data != null)
			{
				data.setMetadata(metadata);
			}
		});
	}

	@Override
	public boolean hasData(final UUID dataId)
	{
		return this.idToData.containsKey(dataId);
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
	}

	@Nullable
	@Override
	public <T extends IData> Optional<T> getData(final UUID dataId)
	{
		T data = (T) this.idToData.get(dataId);

		return Optional.ofNullable(data);
	}

	@Override
	public void removeData(final UUID dataId)
	{
		this.idToData.remove(dataId);
	}

	@Override
	public UUID addData(final IData data)
	{
		UUID id = UUID.randomUUID();

		while (this.idToData.containsKey(id))
		{
			id = UUID.randomUUID();
		}

		if (data.getMetadata().getIdentifier() == null)
		{
			data.getMetadata().setIdentifier(new DataIdentifier(null, id));
		}

		this.setData(id, data);

		return id;
	}

	@Override
	public void setData(final UUID dataId, final IData data)
	{
		this.idToData.put(dataId, data);
	}

	@Override
	public String getCacheId()
	{
		return this.cacheId;
	}
}
