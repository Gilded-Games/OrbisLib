package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.core.ICreationData;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.region.IColored;
import com.gildedgames.orbis_api.data.region.IMutableRegion;
import com.gildedgames.orbis_api.inventory.InventorySpawnEggs;
import com.gildedgames.orbis_api.processing.DataPrimer;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import net.minecraft.nbt.NBTTagCompound;

public class ScheduleRegion implements NBT, IColored, ISchedule
{
	private InventorySpawnEggs spawnEggsInv;

	private String triggerId;

	private IMutableRegion bounds;

	private BlueprintData dataParent;

	private IScheduleRecord parent;

	private ScheduleRegion()
	{

	}

	public ScheduleRegion(String uniqueName, IMutableRegion bounds)
	{
		this.triggerId = uniqueName;
		this.bounds = bounds;
		this.spawnEggsInv = new InventorySpawnEggs();
	}

	public InventorySpawnEggs getSpawnEggsInventory()
	{
		return this.spawnEggsInv;
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
	public void onGenerateLayer(DataPrimer primer, ICreationData<?> data)
	{

	}

	@Override
	public void write(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		tag.setString("triggerId", this.triggerId);
		funnel.set("bounds", this.bounds);
		funnel.set("spawnEggsInv", this.spawnEggsInv);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.triggerId = tag.getString("triggerId");
		this.bounds = funnel.get("bounds");
		this.spawnEggsInv = funnel.get("spawnEggsInv");
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

		this.spawnEggsInv.setDataParent(this.dataParent);
	}
}
