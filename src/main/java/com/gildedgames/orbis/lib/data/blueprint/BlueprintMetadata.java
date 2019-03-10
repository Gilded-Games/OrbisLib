package com.gildedgames.orbis.lib.data.blueprint;

import com.gildedgames.orbis.lib.core.variables.GuiVarBoolean;
import com.gildedgames.orbis.lib.core.variables.IGuiVar;
import com.gildedgames.orbis.lib.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class BlueprintMetadata implements IBlueprintMetadata
{
	private GuiVarBoolean layerTransparency;

	private GuiVarBoolean choosePerBlockOnPostGen;

	private List<IGuiVar> variables = Lists.newArrayList();

	public BlueprintMetadata()
	{
		this.layerTransparency = new GuiVarBoolean("orbis.gui.layer_transparency");
		this.choosePerBlockOnPostGen = new GuiVarBoolean("orbis.gui.choose_per_block_on_post_gen");

		this.layerTransparency.setData(true);

		this.variables.clear();

		this.variables.add(this.layerTransparency);
		this.variables.add(this.choosePerBlockOnPostGen);
	}

	@Override
	public GuiVarBoolean getLayerTransparencyVar()
	{
		return this.layerTransparency;
	}

	@Override
	public GuiVarBoolean getChoosePerBlockOnPostGenVar()
	{
		return this.choosePerBlockOnPostGen;
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
	public void write(final NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("layerTransparency", this.layerTransparency);
		funnel.set("choosePerBlockOnPostGen", this.choosePerBlockOnPostGen);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.layerTransparency = funnel.getWithDefault("layerTransparency", () -> this.layerTransparency);
		this.choosePerBlockOnPostGen = funnel.getWithDefault("choosePerBlockOnPostGen", () -> this.choosePerBlockOnPostGen);

		this.variables.clear();

		this.variables.add(this.layerTransparency);
		this.variables.add(this.choosePerBlockOnPostGen);
	}
}
