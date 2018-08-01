package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.blueprint.BlueprintDataPalette;
import com.gildedgames.orbis_api.data.region.IColored;
import com.gildedgames.orbis_api.data.region.IMutableRegion;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

//TODO: Convert into schedule processor
public class ScheduleBlueprint implements NBT, IColored, ISchedule
{
	private String triggerId;

	private IMutableRegion bounds;

	private BlueprintData dataParent;

	private IScheduleRecord parent;

	private BlueprintDataPalette palette;

	private ScheduleBlueprint()
	{

	}

	public ScheduleBlueprint(String uniqueName, BlueprintDataPalette palette, IMutableRegion bounds)
	{
		this.triggerId = uniqueName;
		this.palette = palette;
		this.bounds = bounds;
	}

	public BlueprintDataPalette getPalette()
	{
		return this.palette;
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
		return null;
	}

	@Override
	public void addProcessor(IScheduleProcessor processor)
	{

	}

	@Override
	public boolean removeProcessor(IScheduleProcessor processor)
	{
		return false;
	}

	/*@Override
	public void onGenerateSchedule(DataPrimer primer, ICreationData<?> data)
	{
		primer.create(this.palette, data.clone().pos(data.getPos().add(this.bounds.getMin().add(this.bounds.getWidth() / 2, 0, this.bounds.getLength() / 2))));
	}*/

	@Override
	public void write(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		tag.setString("triggerId", this.triggerId);
		funnel.set("bounds", this.bounds);
		funnel.set("palette", this.palette);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.triggerId = tag.getString("triggerId");
		this.bounds = funnel.get("bounds");
		this.palette = funnel.get("palette");
	}

	@Override
	public int getColor()
	{
		return 0xd19044;
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
	}
}
