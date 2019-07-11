package com.gildedgames.orbis.lib.core.variables;

import com.gildedgames.orbis.lib.client.gui.data.DropdownElementWithData;
import com.gildedgames.orbis.lib.client.gui.util.GuiInputSlider;
import com.gildedgames.orbis.lib.client.rect.Dim2D;
import com.gildedgames.orbis.lib.core.variables.displays.GuiVarDisplay;
import net.minecraft.nbt.CompoundNBT;

import java.util.List;
import java.util.function.Supplier;

public class GuiVarFloatRange implements IGuiVar<Float, GuiInputSlider>
{
	private float min, max, data;

	private String name = "";

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
	public String getVariableName()
	{
		return this.name;
	}

	@Override
	public String getDataName()
	{
		return "orbis.gui.float_range";
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
	public List<DropdownElementWithData<Supplier<IGuiVarCompareExpression>>> getCompareExpressions()
	{
		return null;
	}

	@Override
	public List<DropdownElementWithData<Supplier<IGuiVarMutateExpression>>> getMutateExpressions()
	{
		return null;
	}

	@Override
	public void write(CompoundNBT tag)
	{
		tag.putFloat("data", this.data);
		tag.putFloat("min", this.min);
		tag.putFloat("max", this.max);
		tag.putString("name", this.name);
	}

	@Override
	public void read(CompoundNBT tag)
	{
		this.data = tag.getFloat("data");
		this.min = tag.getFloat("min");
		this.max = tag.getFloat("max");
		this.name = tag.getString("name");
	}
}
