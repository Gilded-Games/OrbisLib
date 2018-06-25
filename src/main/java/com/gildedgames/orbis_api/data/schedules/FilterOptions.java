package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.core.variables.GuiVarBoolean;
import com.gildedgames.orbis_api.core.variables.GuiVarFloatRange;
import com.gildedgames.orbis_api.core.variables.GuiVarString;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import net.minecraft.nbt.NBTTagCompound;

public class FilterOptions implements IFilterOptions
{

	private GuiVarBoolean choosesPerBlock;

	private GuiVarFloatRange edgeNoise;

	private GuiVarString displayName;

	public FilterOptions()
	{
		this.choosesPerBlock = new GuiVarBoolean("Chooses Per Block");
		this.edgeNoise = new GuiVarFloatRange("Edge Noise", 0.0F, 100.0F);
		this.displayName = new GuiVarString("Display Name");
	}

	@Override
	public GuiVarString getDisplayNameVar()
	{
		return this.displayName;
	}

	@Override
	public GuiVarBoolean getChoosesPerBlockVar()
	{
		return this.choosesPerBlock;
	}

	@Override
	public GuiVarFloatRange getEdgeNoiseVar()
	{
		return this.edgeNoise;
	}

	@Override
	public void copyFrom(IFilterOptions options)
	{
		this.choosesPerBlock.setData(options.getChoosesPerBlockVar().getData());
		this.edgeNoise.setData(options.getEdgeNoiseVar().getData());
		this.displayName.setData(options.getDisplayNameVar().getData());
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("choosesPerBlock", this.choosesPerBlock);
		funnel.set("edgeNoise", this.edgeNoise);
		funnel.set("displayName", this.displayName);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.choosesPerBlock = funnel.get("choosesPerBlock");
		this.edgeNoise = funnel.get("edgeNoise");
		this.displayName = funnel.get("displayName");
	}
}
