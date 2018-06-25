package com.gildedgames.orbis_api.core.variables;

import com.gildedgames.orbis_api.client.gui.util.GuiInputSlider;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.core.variables.displays.GuiVarDisplay;
import net.minecraft.nbt.NBTTagCompound;

public class GuiVarFloatRange implements IGuiVar<Float, GuiInputSlider>
{
	private float min, max, data;

	private String name;

	private GuiVarFloatRange()
	{

	}

	public GuiVarFloatRange(String name, float min, float max)
	{
		this.name = name;

		this.min = min;
		this.max = max;
	}

	@Override
	public void setParentDisplay(GuiVarDisplay parentDisplay)
	{

	}

	@Override
	public String getName()
	{
		return this.name;
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
	public GuiInputSlider createDisplay(int maxWidth)
	{
		return new GuiInputSlider(Dim2D.build().width(maxWidth).x(1).height(20).flush(), this.min, this.max, this.data <= 0.0F ? 0.0F : this.data / this.max);
	}

	@Override
	public void updateDataFromDisplay(GuiInputSlider guiFrame)
	{
		this.data = guiFrame.getSliderValue() * this.max;
	}

	@Override
	public void resetDisplayFromData(GuiInputSlider guiFrame)
	{
		guiFrame.setSliderValue(this.data / this.max);
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		tag.setFloat("data", this.data);
		tag.setFloat("min", this.min);
		tag.setFloat("max", this.max);
		tag.setString("name", this.name);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		this.data = tag.getFloat("data");
		this.min = tag.getFloat("min");
		this.max = tag.getFloat("max");
		this.name = tag.getString("name");
	}
}
