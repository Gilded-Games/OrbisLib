package com.gildedgames.orbis.lib.data.framework;

import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.framework.interfaces.IFrameworkNode;
import com.gildedgames.orbis.lib.data.pathway.PathwayData;
import com.gildedgames.orbis.lib.data.region.IMutableRegion;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;

public class FrameworkNode implements IFrameworkNode
{
	// Not sure what this is about tbh lol.
	private static boolean isNullAllowed = false;

	private IFrameworkNode schedule;

	//	private final boolean isNullAllowed = false;

	private FrameworkData dataParent;

	private FrameworkNode()
	{

	}

	public FrameworkNode(IFrameworkNode schedule)
	{
		this.schedule = schedule;
	}

	public IFrameworkNode schedule()
	{
		return this.schedule;
	}

	@Override
	public BlueprintData getBlueprintData()
	{
		return null;
	}

	@Override
	public int getMaxEdges()
	{
		return this.schedule.getMaxEdges();
	}

	@Override
	public IMutableRegion getBounds()
	{
		return this.schedule.getBounds();
	}

	@Override
	public Collection<PathwayData> pathways()
	{
		return this.schedule.pathways();
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);
		funnel.set("schedule", this.schedule);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);
		this.schedule = funnel.get("schedule");
	}

	@Override
	public Class<? extends FrameworkData> getDataClass()
	{
		return FrameworkData.class;
	}

	@Override
	public FrameworkData getDataParent()
	{
		return this.dataParent;
	}

	@Override
	public void setDataParent(FrameworkData frameworkData)
	{
		this.dataParent = frameworkData;

		if (this.schedule != null)
		{
			this.schedule.setDataParent(frameworkData);
		}
	}
}
