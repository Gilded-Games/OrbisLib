package com.gildedgames.orbis.lib.core.variables;

import com.gildedgames.orbis.lib.client.gui.data.DropdownElementWithData;
import com.gildedgames.orbis.lib.client.gui.util.GuiInput;
import com.gildedgames.orbis.lib.client.gui.util.IGuiInputListener;
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

public class GuiVarInteger implements IGuiVar<Integer, GuiInput>, IGuiInputListener
{
	public static final List<DropdownElementWithData<Supplier<IGuiVarCompareExpression>>> COMPARE_EXPRESSIONS = Lists.newArrayList(
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.equals"), () -> new NumberEquals<>(new GuiVarInteger("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.doesnt_equal"),
					() -> new NumberDoesntEqual<>(new GuiVarInteger("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.greater_than"),
					() -> new NumberGreaterThan<>(new GuiVarInteger("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.less_than"),
					() -> new NumberLessThan<>(new GuiVarInteger("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.greater_than_or_equal"),
					() -> new NumberGreaterThanOrEqual<>(new GuiVarInteger("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.less_than_or_equal"),
					() -> new NumberLessThanOrEqual<>(new GuiVarInteger("orbis.gui.value")))
	);

	public static final List<DropdownElementWithData<Supplier<IGuiVarMutateExpression>>> MUTATE_EXPRESSIONS = Lists.newArrayList(
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.set"), () -> new NumberSet<>(new GuiVarInteger("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.increase"), () -> new NumberIncrease<>(new GuiVarInteger("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.decrease"), () -> new NumberDecrease<>(new GuiVarInteger("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.multiply"), () -> new NumberMultiply<>(new GuiVarInteger("orbis.gui.value"))),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.divide"), () -> new NumberDivide<>(new GuiVarInteger("orbis.gui.value")))
	);

	private int data;

	private String name = "";

	private GuiVarDisplay parentDisplay;

	private GuiVarInteger()
	{

	}

	public GuiVarInteger(String name)
	{
		this.name = name;
	}

	@Override
	public void setParentDisplay(GuiVarDisplay parentDisplay)
	{
		this.parentDisplay = parentDisplay;
	}

	@Override
	public String getVariableName()
	{
		return this.name;
	}

	@Override
	public String getDataName()
	{
		return "orbis.gui.integer";
	}

	@Override
	public Integer getData()
	{
		return this.data;
	}

	@Override
	public void setData(Integer data)
	{
		this.data = data;
	}

	@Override
	public GuiInput createDisplay(int maxWidth)
	{
		GuiInput input = new GuiInput(Dim2D.build().width(maxWidth).x(1).height(20).flush());

		input.listen(this);

		input.getInner().setText(String.valueOf(this.data));
		input.getInner().setMaxStringLength(15);
		input.getInner().setValidator((s) -> (StringUtils.isNumeric(s) || (s != null && s.isEmpty())) && !s.contains("-"));

		return input;
	}

	@Override
	public void updateDataFromDisplay(GuiInput guiInput)
	{
		if (guiInput.getInner().getText().isEmpty())
		{
			this.data = 0;
		}
		else
		{
			this.data = Integer.parseInt(guiInput.getInner().getText());
		}
	}

	@Override
	public void resetDisplayFromData(GuiInput guiInput)
	{
		guiInput.getInner().setText(String.valueOf(this.data));
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
		tag.setInteger("data", this.data);
		tag.setString("name", this.name);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		this.data = tag.getInteger("data");
		this.name = tag.getString("name");
	}

	@Override
	public void onPressEnter()
	{
		if (this.parentDisplay != null)
		{
			this.parentDisplay.updateVariableData();
		}
	}
}