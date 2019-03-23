package com.gildedgames.orbis.lib.core.variables;

import com.gildedgames.orbis.lib.client.gui.data.DropdownElementWithData;
import com.gildedgames.orbis.lib.client.gui.util.GuiInput;
import com.gildedgames.orbis.lib.client.rect.Dim2D;
import com.gildedgames.orbis.lib.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis.lib.core.variables.var_comparators.*;
import com.gildedgames.orbis.lib.core.variables.var_mutators.*;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Supplier;

public class GuiVarDouble implements IGuiVar<Double, GuiInput>
{
	public static final List<DropdownElementWithData<Supplier<IGuiVarCompareExpression>>> COMPARE_EXPRESSIONS = Lists.newArrayList(
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.equals"), () -> new NumberEquals<>(new GuiVarDouble("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.doesnt_equal"),
					() -> new NumberDoesntEqual<>(new GuiVarDouble("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.greater_than"),
					() -> new NumberGreaterThan<>(new GuiVarDouble("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.less_than"), () -> new NumberLessThan<>(new GuiVarDouble("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.greater_than_or_equal"),
					() -> new NumberGreaterThanOrEqual<>(new GuiVarDouble("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.less_than_or_equal"),
					() -> new NumberLessThanOrEqual<>(new GuiVarDouble("orbis.gui.value")))
	);

	public static final List<DropdownElementWithData<Supplier<IGuiVarMutateExpression>>> MUTATE_EXPRESSIONS = Lists.newArrayList(
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.set"), () -> new NumberSet<>(new GuiVarDouble("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.increase"), () -> new NumberIncrease<>(new GuiVarDouble("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.decrease"), () -> new NumberDecrease<>(new GuiVarDouble("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.multiply"), () -> new NumberMultiply<>(new GuiVarDouble("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.divide"), () -> new NumberDivide<>(new GuiVarDouble("orbis.gui.value")))
	);

	private double data;

	private String name = "";

	private GuiVarDouble()
	{

	}

	public GuiVarDouble(String name)
	{
		this.name = name;
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
		return "orbis.gui.double";
	}

	@Override
	public Double getData()
	{
		return this.data;
	}

	@Override
	public void setData(Double data)
	{
		this.data = data;
	}

	@Override
	public GuiInput createDisplay(int maxWidth)
	{
		GuiInput input = new GuiInput(Dim2D.build().width(maxWidth).x(1).height(20).flush());

		input.getInner().setText(String.valueOf(this.data));
		input.getInner().setMaxStringLength(15);
		input.getInner().setValidator((s) -> (StringUtils.isNumeric(s) || (s != null && s.isEmpty())) && !s.contains("-"));

		return input;
	}

	@Override
	public void updateDataFromDisplay(GuiInput guiFrame)
	{
		if (guiFrame.getInner().getText().isEmpty())
		{
			this.data = 0.0F;
		}
		else
		{
			this.data = Double.parseDouble(guiFrame.getInner().getText());
		}
	}

	@Override
	public void resetDisplayFromData(GuiInput guiFrame)
	{
		guiFrame.getInner().setText(String.valueOf(this.data));
	}

	@Override
	public List<DropdownElementWithData<Supplier<IGuiVarCompareExpression>>> getCompareExpressions()
	{
		return COMPARE_EXPRESSIONS;
	}

	@Override
	public List<DropdownElementWithData<Supplier<IGuiVarMutateExpression>>> getMutateExpressions()
	{
		return MUTATE_EXPRESSIONS;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		tag.putDouble("data", this.data);
		tag.putString("name", this.name);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		this.data = tag.getDouble("data");
		this.name = tag.getString("name");
	}
}
