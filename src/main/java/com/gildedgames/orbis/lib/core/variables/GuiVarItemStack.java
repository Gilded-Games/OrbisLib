package com.gildedgames.orbis.lib.core.variables;

import com.gildedgames.orbis.lib.client.gui.data.DropdownElementWithData;
import com.gildedgames.orbis.lib.client.rect.Dim2D;
import com.gildedgames.orbis.lib.core.variables.displays.GuiItemStackChooser;
import com.gildedgames.orbis.lib.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class GuiVarItemStack implements IGuiVar<ItemStack, GuiItemStackChooser>
{
	public static final List<DropdownElementWithData<Supplier<IGuiVarCompareExpression>>> COMPARE_EXPRESSIONS = Lists.newArrayList();

	public static final List<DropdownElementWithData<Supplier<IGuiVarMutateExpression>>> MUTATE_EXPRESSIONS = Lists.newArrayList();

	private ItemStack data = ItemStack.EMPTY;

	private String name = "";

	private Function<ItemStack, Boolean> stackValidator;

	private GuiVarItemStack()
	{

	}

	public GuiVarItemStack(String name, Function<ItemStack, Boolean> stackValidator)
	{
		this.name = name;
		this.stackValidator = stackValidator;
	}

	public Function<ItemStack, Boolean> getStackValidator()
	{
		return this.stackValidator;
	}

	public void setStackValidator(Function<ItemStack, Boolean> stackValidator)
	{
		this.stackValidator = stackValidator;
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
		return "orbis.gui.itemstack";
	}

	@Override
	public ItemStack getData()
	{
		return this.data;
	}

	@Override
	public void setData(@Nonnull ItemStack data)
	{
		this.data = data;
	}

	@Override
	public GuiItemStackChooser createDisplay(int maxWidth)
	{
		return new GuiItemStackChooser(Dim2D.build().width(maxWidth).x(0).height(20).flush(), this);
	}

	@Override
	public void updateDataFromDisplay(GuiItemStackChooser guiFrame)
	{
		this.data = guiFrame.getChosenStack();
	}

	@Override
	public void resetDisplayFromData(GuiItemStackChooser guiFrame)
	{
		guiFrame.setChosenStack(this.data);
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
	public void write(CompoundNBT tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.setStack("data", this.data);
		tag.putString("name", this.name);
	}

	@Override
	public void read(CompoundNBT tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.data = funnel.getStack("data");
		this.name = tag.getString("name");
	}
}