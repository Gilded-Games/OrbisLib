package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.core.variables.GuiVarBoolean;
import com.gildedgames.orbis.lib.core.variables.GuiVarString;
import com.gildedgames.orbis.lib.core.variables.IGuiVar;
import com.gildedgames.orbis.lib.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundNBT;

import java.util.List;

public class ScheduleLayerOptions implements IScheduleLayerOptions
{
	private List<IGuiVar> variables = Lists.newArrayList();

	private GuiVarString displayName;

	private GuiVarBoolean replacesSolidBlocks;

	public ScheduleLayerOptions()
	{
		this.displayName = new GuiVarString("orbis.gui.display_name");
		this.replacesSolidBlocks = new GuiVarBoolean("orbis.gui.replaces_solid_blocks");

		this.variables.add(this.displayName);
		this.variables.add(this.replacesSolidBlocks);
	}

	@Override
	public GuiVarString getDisplayNameVar()
	{
		return this.displayName;
	}

	@Override
	public GuiVarBoolean getReplacesSolidBlocksVar()
	{
		return this.replacesSolidBlocks;
	}

	@Override
	public void copyFrom(IScheduleLayerOptions other)
	{
		this.displayName.setData(other.getDisplayNameVar().getData());
		this.replacesSolidBlocks.setData(other.getReplacesSolidBlocksVar().getData());
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

	@Override
	public void write(CompoundNBT tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("displayName", this.displayName);
		funnel.set("replacesSolidBlocks", this.replacesSolidBlocks);

	}

	@Override
	public void read(CompoundNBT tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.displayName = funnel.getWithDefault("displayName", () -> this.displayName);
		this.replacesSolidBlocks = funnel.getWithDefault("replacesSolidBlocks", () -> this.replacesSolidBlocks);

		this.variables.clear();

		this.variables.add(this.displayName);
		this.variables.add(this.replacesSolidBlocks);
	}
}
