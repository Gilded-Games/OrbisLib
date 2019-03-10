package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.core.variables.GuiVarBoolean;
import com.gildedgames.orbis.lib.core.variables.GuiVarFloatRange;
import com.gildedgames.orbis.lib.core.variables.GuiVarString;
import com.gildedgames.orbis.lib.core.variables.IGuiVar;
import com.gildedgames.orbis.lib.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class FilterOptions implements IFilterOptions
{

	private GuiVarBoolean choosesPerBlock;

	private GuiVarFloatRange edgeNoise;

	private GuiVarString displayName;

	private List<IGuiVar> variables = Lists.newArrayList();

	public FilterOptions()
	{
		this.choosesPerBlock = new GuiVarBoolean("orbis.gui.chooses_per_block");
		this.edgeNoise = new GuiVarFloatRange("orbis.gui.edge_noise", 0.0F, 100.0F);
		this.displayName = new GuiVarString("orbis.gui.display_name");

		this.variables.add(this.displayName);
		this.variables.add(this.choosesPerBlock);
		this.variables.add(this.edgeNoise);
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

		this.choosesPerBlock = funnel.getWithDefault("choosesPerBlock", () -> this.choosesPerBlock);
		this.edgeNoise = funnel.getWithDefault("edgeNoise", () -> this.edgeNoise);
		this.displayName = funnel.getWithDefault("displayName", () -> this.displayName);

		this.variables.clear();

		this.variables.add(this.displayName);
		this.variables.add(this.choosesPerBlock);
		this.variables.add(this.edgeNoise);
	}

	@Override
	public List<IGuiVar> getVariables()
	{
		return this.variables;
	}

	@Override
	public void setParentDisplay(GuiVarDisplay parentDisplay)
	{

	}
}
