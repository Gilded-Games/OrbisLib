package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.util.ObjectFilter;
import com.gildedgames.orbis_api.util.RegionHelp;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.world.IWorldObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

public class ScheduleRecord implements IScheduleRecord
{

	private final List<IScheduleRecordListener> listeners = Lists.newArrayList();

	private Map<Integer, ISchedule> schedules = Maps.newHashMap();

	private BlueprintData dataParent;

	private IScheduleLayer parent;

	private int nextId;

	public ScheduleRecord()
	{

	}

	@Override
	public void listen(IScheduleRecordListener listener)
	{
		if (!this.listeners.contains(listener))
		{
			this.listeners.add(listener);
		}
	}

	@Override
	public boolean unlisten(IScheduleRecordListener listener)
	{
		return this.listeners.remove(listener);
	}

	@Override
	public void removeSchedule(int id)
	{
		ISchedule schedule = this.schedules.remove(id);

		this.listeners.forEach(o -> o.onRemoveSchedule(schedule));
	}

	@Override
	public int addSchedule(ISchedule schedule, IWorldObject parentWorldObject)
	{
		int id = this.nextId++;

		boolean success = this.setSchedule(id, schedule, parentWorldObject);

		if (!success)
		{
			return -1;
		}

		return id;
	}

	@Override
	public boolean setSchedule(int id, ISchedule schedule, IWorldObject parentWorldObject)
	{
		IRegion bb = parentWorldObject.getShape().getBoundingBox();

		BlockPos min = schedule.getBounds().getMin().add(bb.getMin());
		BlockPos max = schedule.getBounds().getMax().add(bb.getMin());

		if (max.getX() > bb.getMax().getX() || max.getY() > bb.getMax().getY() || max.getZ() > bb.getMax().getZ()
				|| min.getX() < bb.getMin().getX() || min.getY() < bb.getMin().getY() || min.getZ() < bb.getMin().getZ())
		{
			return false;
		}

		for (ISchedule s : this.getSchedules(ISchedule.class))
		{
			if (RegionHelp.intersects(schedule.getBounds(), s.getBounds()))
			{
				return false;
			}
		}

		schedule.setDataParent(this.dataParent);
		schedule.setParent(this);

		this.schedules.put(id, schedule);

		this.listeners.forEach(o -> o.onAddSchedule(schedule));

		return true;
	}

	@Override
	public int getScheduleId(ISchedule schedule)
	{
		for (Map.Entry<Integer, ISchedule> entry : this.schedules.entrySet())
		{
			int i = entry.getKey();
			final ISchedule s = entry.getValue();

			if (schedule.equals(s))
			{
				return i;
			}
		}

		return -1;
	}

	@Override
	public <T extends ISchedule> T getSchedule(int id)
	{
		return (T) this.schedules.get(id);
	}

	@Override
	public <T extends ISchedule> List<T> getSchedules(Class<T> clazz)
	{
		return ObjectFilter.getTypesFrom(this.schedules.values(), clazz);
	}

	@Override
	public List<ISchedule> getSchedulesFromTriggerID(String triggerId)
	{
		List<ISchedule> schedules = Lists.newArrayList();

		for (ScheduleRegion s : this.getSchedules(ScheduleRegion.class))
		{
			if (s.getTriggerId().equals(triggerId))
			{
				schedules.add(s);
			}
		}

		return schedules;
	}

	@Override
	public IScheduleLayer getParent()
	{
		return this.parent;
	}

	@Override
	public void setParent(IScheduleLayer parent)
	{
		this.parent = parent;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		tag.setInteger("nextId", this.nextId);

		funnel.setIntMap("schedules", this.schedules);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.nextId = tag.getInteger("nextId");

		this.schedules = funnel.getIntMap("schedules");

		this.schedules.values().forEach(s -> s.setParent(this));
	}

	@Override
	public Class<? extends BlueprintData> getDataClass()
	{
		return BlueprintData.class;
	}

	@Override
	public BlueprintData getDataParent()
	{
		return this.dataParent;
	}

	@Override
	public void setDataParent(BlueprintData blueprintData)
	{
		this.dataParent = blueprintData;

		this.schedules.values().forEach(s -> s.setDataParent(this.dataParent));
	}
}
