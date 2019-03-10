package com.gildedgames.orbis.lib.core.variables;

import com.gildedgames.orbis.lib.client.gui.data.DropdownElementWithData;
import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.core.variables.displays.GuiTickBox;
import com.gildedgames.orbis.lib.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class GuiVarBoolean implements IGuiVar<Boolean, GuiTickBox>
{
	public static final List<DropdownElementWithData<Supplier<IGuiVarCompareExpression>>> COMPARE_EXPRESSIONS = Lists.newArrayList(
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.equals_true"), EqualsTrue::new),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.equals_false"), EqualsFalse::new)
	);

	public static final List<DropdownElementWithData<Supplier<IGuiVarMutateExpression>>> MUTATE_EXPRESSIONS = Lists.newArrayList(
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.set"), Set::new)
	);

	private boolean data;

	private String name = "";

	private GuiVarBoolean()
	{

	}

	public GuiVarBoolean(String name)
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
		return "orbis.gui.boolean";
	}

	@Override
	public Boolean getData()
	{
		return this.data;
	}

	@Override
	public void setData(Boolean data)
	{
		this.data = data;
	}

	@Override
	public GuiTickBox createDisplay(int maxWidth)
	{
		return new GuiTickBox(Pos2D.flush(1, 0), this.data);
	}

	@Override
	public void updateDataFromDisplay(GuiTickBox guiFrame)
	{
		this.data = guiFrame.isTicked();
	}

	@Override
	public void resetDisplayFromData(GuiTickBox guiFrame)
	{
		guiFrame.setTicked(this.data);
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
		tag.setBoolean("data", this.data);
		tag.setString("name", this.name);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		this.data = tag.getBoolean("data");
		this.name = tag.getString("name");
	}

	public static class EqualsTrue implements IGuiVarCompareExpression<Boolean>
	{
		public EqualsTrue()
		{

		}

		@Override
		public boolean compare(Object input)
		{
			if (input instanceof Boolean)
			{
				return (boolean) input;
			}

			return false;
		}

		@Override
		public String getDisplayString()
		{
			return "orbis.gui.equals";
		}

		@Override
		public List<IGuiVar<Boolean, ?>> getInputs()
		{
			return Collections.emptyList();
		}

		@Override
		public void transferData(List<IGuiVar<Boolean, ?>> prevInputs)
		{

		}

		@Override
		public void write(NBTTagCompound tag)
		{

		}

		@Override
		public void read(NBTTagCompound tag)
		{

		}
	}

	public static class EqualsFalse implements IGuiVarCompareExpression<Boolean>
	{
		public EqualsFalse()
		{

		}

		@Override
		public boolean compare(Object input)
		{
			if (input instanceof Boolean)
			{
				return !(boolean) input;
			}

			return false;
		}

		@Override
		public String getDisplayString()
		{
			return "orbis.gui.equals";
		}

		@Override
		public List<IGuiVar<Boolean, ?>> getInputs()
		{
			return Collections.emptyList();
		}

		@Override
		public void transferData(List<IGuiVar<Boolean, ?>> prevInputs)
		{

		}

		@Override
		public void write(NBTTagCompound tag)
		{

		}

		@Override
		public void read(NBTTagCompound tag)
		{

		}
	}

	public static class Set implements IGuiVarMutateExpression<Boolean>
	{
		private GuiVarBoolean value;

		private List<IGuiVar<Boolean, ?>> inputs = Lists.newArrayList();

		public Set()
		{
			this.value = new GuiVarBoolean("orbis.gui.value");
			this.inputs.add(this.value);
		}

		@Override
		public Boolean mutate(Boolean input)
		{
			return this.value.getData();
		}

		@Override
		public String getDisplayString()
		{
			return "orbis.gui.set";
		}

		@Override
		public List<IGuiVar<Boolean, ?>> getInputs()
		{
			return this.inputs;
		}

		@Override
		public void transferData(List<IGuiVar<Boolean, ?>> prevInputs)
		{
			if (prevInputs.size() >= 1)
			{
				this.value.setData(prevInputs.get(0).getData());
			}
		}

		@Override
		public void write(NBTTagCompound tag)
		{
			NBTFunnel funnel = new NBTFunnel(tag);

			funnel.set("value", this.value);
		}

		@Override
		public void read(NBTTagCompound tag)
		{
			NBTFunnel funnel = new NBTFunnel(tag);

			this.value = funnel.get("value");

			this.inputs.clear();

			this.inputs.add(this.value);
		}
	}
}