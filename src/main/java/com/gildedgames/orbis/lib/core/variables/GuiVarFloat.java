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

public class GuiVarFloat implements IGuiVar<Float, GuiInput>
{
	public static final List<DropdownElementWithData<Supplier<IGuiVarCompareExpression>>> COMPARE_EXPRESSIONS = Lists.newArrayList(
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.equals"), () -> new NumberEquals<>(new GuiVarFloat("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.doesnt_equal"),
					() -> new NumberDoesntEqual<>(new GuiVarFloat("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.greater_than"),
					() -> new NumberGreaterThan<>(new GuiVarFloat("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.less_than"), () -> new NumberLessThan<>(new GuiVarFloat("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.greater_than_or_equal"),
					() -> new NumberGreaterThanOrEqual<>(new GuiVarFloat("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.less_than_or_equal"),
					() -> new NumberLessThanOrEqual<>(new GuiVarFloat("orbis.gui.value")))
	);

	public static final List<DropdownElementWithData<Supplier<IGuiVarMutateExpression>>> MUTATE_EXPRESSIONS = Lists.newArrayList(
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.set"), () -> new NumberSet<>(new GuiVarFloat("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.increase"), () -> new NumberIncrease<>(new GuiVarFloat("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.decrease"), () -> new NumberDecrease<>(new GuiVarFloat("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.multiply"), () -> new NumberMultiply<>(new GuiVarFloat("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.divide"), () -> new NumberDivide<>(new GuiVarFloat("orbis.gui.value")))
	);

	private float data;

	private String name = "";

	private GuiVarFloat()
	{

	}

	public GuiVarFloat(String name)
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
		return "orbis.gui.float";
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
	public GuiInput createDisplay(int maxWidth)
	{
		GuiInput input = new GuiInput(Dim2D.build().width(maxWidth).x(1).height(20).flush());

		input.getInner().setText(String.valueOf(this.data));
		input.getInner().setMaxStringLength(15);
		input.getInner().setValidator(
				(s) -> (StringUtils.isNumeric(s) || (s != null && s.chars().filter(num -> num == '.').count() <= 1) || (s != null && s.isEmpty())) && !s
						.contains("-"));

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
			this.data = Float.parseFloat(guiFrame.getInner().getText());
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
		tag.putFloat("data", this.data);
		tag.putString("name", this.name);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		this.data = tag.getFloat("data");
		this.name = tag.getString("name");
	}

}
