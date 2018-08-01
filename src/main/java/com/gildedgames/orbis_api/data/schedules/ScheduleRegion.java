package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.region.IColored;
import com.gildedgames.orbis_api.data.region.IMutableRegion;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class ScheduleRegion implements NBT, IColored, ISchedule
{
	private List<IScheduleProcessor> processors;

	private String triggerId;

	private IMutableRegion bounds;

	private BlueprintData dataParent;

	private IScheduleRecord parent;

	private int color = 0xd19044;

	private ScheduleRegion()
	{

	}

	public ScheduleRegion(String uniqueName, IMutableRegion bounds)
	{
		this.triggerId = uniqueName;
		this.bounds = bounds;
	}

	@Override
	public String getTriggerId()
	{
		return this.triggerId;
	}

	@Override
	public void setTriggerId(String triggerId)
	{
		this.triggerId = triggerId;
	}

	@Override
	public IScheduleRecord getParent()
	{
		return this.parent;
	}

	@Override
	public void setParent(IScheduleRecord parent)
	{
		this.parent = parent;
	}

	@Override
	public IMutableRegion getBounds()
	{
		return this.bounds;
	}

	@Override
	public List<IScheduleProcessor> getProcessors()
	{
		return this.processors;
	}

	@Override
	public void addProcessor(IScheduleProcessor processor)
	{
		if (!this.processors.contains(processor))
		{
			this.processors.add(processor);
		}
	}

	@Override
	public boolean removeProcessor(IScheduleProcessor processor)
	{
		return this.processors.removeAll(this.processors);
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		tag.setString("triggerId", this.triggerId);
		funnel.set("bounds", this.bounds);
		funnel.setList("processors", this.processors);
		tag.setInteger("color", this.color);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.triggerId = tag.getString("triggerId");
		this.bounds = funnel.get("bounds");
		this.processors = funnel.getList("processors");
		this.color = tag.getInteger("color");
	}

	@Override
	public int getColor()
	{
		return this.color;
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

		this.processors.forEach((processor) -> processor.setDataParent(this.dataParent));
	}
}
