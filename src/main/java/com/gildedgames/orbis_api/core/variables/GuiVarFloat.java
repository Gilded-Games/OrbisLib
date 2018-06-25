package com.gildedgames.orbis_api.core.variables;

import com.gildedgames.orbis_api.client.gui.util.GuiFrame;
import com.gildedgames.orbis_api.core.variables.displays.GuiVarDisplay;
import net.minecraft.nbt.NBTTagCompound;

public class GuiVarFloat implements IGuiVar<Float, GuiFrame>
{
	private float data;

	private GuiVarFloat()
	{

	}

	@Override
	public void setParentDisplay(GuiVarDisplay parentDisplay)
	{

	}

	@Override
	public String getName()
	{
		return "Float";
	}

	@Override
	public Float getData()
	{
		return this.data;
	}

	@Override
	public void setData(Float data)
	{
		this.data = data;
	}

	@Override
	public GuiFrame createDisplay(int maxWidth)
	{
		return null;
	}

	@Override
	public void updateDataFromDisplay(GuiFrame guiFrame)
	{

	}

	@Override
	public void resetDisplayFromData(GuiFrame guiFrame)
	{

	}

	@Override
	public void write(NBTTagCompound tag)
	{
		tag.setFloat("data", this.data);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		this.data = tag.getFloat("data");
	}
}
