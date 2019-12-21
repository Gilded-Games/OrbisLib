package com.gildedgames.orbis.lib.data.blueprint;

import com.gildedgames.orbis.lib.data.management.IData;
import com.gildedgames.orbis.lib.data.management.IDataIdentifier;
import com.gildedgames.orbis.lib.data.management.IDataMetadata;
import com.gildedgames.orbis.lib.data.management.impl.DataMetadata;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.world.IWorldObject;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BlueprintNetworkData implements IData
{
	public static final String EXTENSION = "blueprintnetwork";

	private final List<IBlueprintNetworkDataListener> listeners = Lists.newArrayList();

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private IDataMetadata metadata;

	private List<IDataIdentifier> rooms, paths, start, finish;

	private int depth;

	private BlueprintNetworkData()
	{
		this.metadata = new DataMetadata();
	}

	public BlueprintNetworkData(int depth, List<IDataIdentifier> rooms, List<IDataIdentifier> paths, List<IDataIdentifier> start, List<IDataIdentifier> finish)
	{
		this();

		this.depth = depth;

		this.rooms = rooms;
		this.paths = paths;
		this.start = start;
		this.finish = finish;
	}

	public int getTargetDepth()
	{
		return this.depth;
	}

	public List<IDataIdentifier> getRooms()
	{
		return this.rooms;
	}

	public void listen(final IBlueprintNetworkDataListener listener)
	{
		if (!this.listeners.contains(listener))
		{
			this.listeners.add(listener);
		}
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
		else if (obj instanceof BlueprintNetworkData)
		{
			final BlueprintNetworkData o = (BlueprintNetworkData) obj;

			final EqualsBuilder builder = new EqualsBuilder();

			builder.append(this.metadata.getIdentifier(), o.metadata.getIdentifier());

			return builder.isEquals();
		}

		return false;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.setList("rooms", this.rooms);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.rooms = funnel.getList("rooms");
	}

	@Override
	public void preSaveToDisk(final IWorldObject object)
	{

	}

	@Override
	public String getFileExtension()
	{
		return BlueprintNetworkData.EXTENSION;
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
	public IData clone()
	{
		final BlueprintNetworkData data = new BlueprintNetworkData();

		final NBTTagCompound tag = new NBTTagCompound();

		this.write(tag);

		data.read(tag);

		return data;
	}

	public void markDirty()
	{
		this.listeners.forEach(IBlueprintNetworkDataListener::onDataChanged);
	}
}
