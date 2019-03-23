package com.gildedgames.orbis.lib.core.variables;

import com.gildedgames.orbis.lib.client.gui.data.DropdownElementWithData;
import com.gildedgames.orbis.lib.client.gui.util.GuiInput;
import com.gildedgames.orbis.lib.client.gui.util.IGuiInputListener;
import com.gildedgames.orbis.lib.client.rect.Dim2D;
import com.gildedgames.orbis.lib.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class GuiVarString implements IGuiVar<String, GuiInput>, IGuiInputListener
{
	public static final List<DropdownElementWithData<Supplier<IGuiVarCompareExpression>>> COMPARE_EXPRESSIONS = Lists.newArrayList(
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.equals"), Equals::new),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.doesnt_equal"), DoesntEqual::new),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.contains"), Contains::new)
	);

	public static final List<DropdownElementWithData<Supplier<IGuiVarMutateExpression>>> MUTATE_EXPRESSIONS = Lists.newArrayList(
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.set"), Set::new),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.concatenate"), Concatenate::new),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.replace"), Replace::new),
			new DropdownElementWithData<>(new TextComponentTranslation("orbis.gui.clear"), Clear::new)
	);

	private String data = "";

	private String name = "";

	private int maxStringLength;

	private GuiVarDisplay parentDisplay;

	private GuiVarString()
	{

	}

	public GuiVarString(String name, int maxStringLength)
	{
		this.name = name;
		this.maxStringLength = maxStringLength;
	}

	public GuiVarString(String name)
	{
		this.name = name;
		this.maxStringLength = -1;
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
		return "orbis.gui.string";
	}

	@Override
	public String getData()
	{
		return this.data;
	}

	@Override
	public void setData(String data)
	{
		this.data = data;
	}

	@Override
	public GuiInput createDisplay(int maxWidth)
	{
		GuiInput input = new GuiInput(Dim2D.build().width(maxWidth).x(1).height(20).flush());

		input.getInner().setText(this.data);

		input.listen(this);

		if (this.maxStringLength >= 0)
		{
			input.getInner().setMaxStringLength(this.maxStringLength);
		}

		return input;
	}

	@Override
	public void updateDataFromDisplay(GuiInput guiInput)
	{
		this.data = guiInput.getInner().getText();
	}

	@Override
	public void resetDisplayFromData(GuiInput guiInput)
	{
		guiInput.getInner().setText(this.data);
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
		tag.putString("data", this.data);
		tag.putString("name", this.name);
		tag.putInt("maxStringLength", this.maxStringLength);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		this.data = tag.getString("data");
		this.name = tag.getString("name");
		this.maxStringLength = tag.getInt("maxStringLength");
	}

	@Override
	public void onPressEnter()
	{
		if (this.parentDisplay != null)
		{
			this.parentDisplay.updateVariableData();
		}
	}

	public static class Equals implements IGuiVarCompareExpression<String>
	{
		private GuiVarString value;

		private List<IGuiVar<String, ?>> inputs = Lists.newArrayList();

		public Equals()
		{
			this.value = new GuiVarString("orbis.gui.value");
			this.inputs.add(this.value);
		}

		@Override
		public boolean compare(Object input)
		{
			return input.equals(this.value.getData());
		}

		@Override
		public String getDisplayString()
		{
			return "orbis.gui.equals";
		}

		@Override
		public List<IGuiVar<String, ?>> getInputs()
		{
			return this.inputs;
		}

		@Override
		public void transferData(List<IGuiVar<String, ?>> prevInputs)
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

	public static class DoesntEqual implements IGuiVarCompareExpression<String>
	{
		private GuiVarString value;

		private List<IGuiVar<String, ?>> inputs = Lists.newArrayList();

		public DoesntEqual()
		{
			this.value = new GuiVarString("orbis.gui.value");
			this.inputs.add(this.value);
		}

		@Override
		public boolean compare(Object input)
		{
			return !input.equals(this.value.getData());
		}

		@Override
		public String getDisplayString()
		{
			return "orbis.gui.doesnt_equal";
		}

		@Override
		public List<IGuiVar<String, ?>> getInputs()
		{
			return this.inputs;
		}

		@Override
		public void transferData(List<IGuiVar<String, ?>> prevInputs)
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

	public static class Contains implements IGuiVarCompareExpression<String>
	{
		private GuiVarString value;

		private List<IGuiVar<String, ?>> inputs = Lists.newArrayList();

		public Contains()
		{
			this.value = new GuiVarString("orbis.gui.value");
			this.inputs.add(this.value);
		}

		@Override
		public boolean compare(Object input)
		{
			if (input instanceof String)
			{
				return ((String) input).contains(this.value.getData());
			}

			return false;
		}

		@Override
		public String getDisplayString()
		{
			return "orbis.gui.contains";
		}

		@Override
		public List<IGuiVar<String, ?>> getInputs()
		{
			return this.inputs;
		}

		@Override
		public void transferData(List<IGuiVar<String, ?>> prevInputs)
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

	public static class Set implements IGuiVarMutateExpression<String>
	{
		private GuiVarString value;

		private List<IGuiVar<String, ?>> inputs = Lists.newArrayList();

		public Set()
		{
			this.value = new GuiVarString("orbis.gui.value");
			this.inputs.add(this.value);
		}

		@Override
		public String mutate(String input)
		{
			return this.value.getData();
		}

		@Override
		public String getDisplayString()
		{
			return "orbis.gui.set";
		}

		@Override
		public List<IGuiVar<String, ?>> getInputs()
		{
			return this.inputs;
		}

		@Override
		public void transferData(List<IGuiVar<String, ?>> prevInputs)
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

	public static class Concatenate implements IGuiVarMutateExpression<String>
	{
		private GuiVarString value;

		private List<IGuiVar<String, ?>> inputs = Lists.newArrayList();

		public Concatenate()
		{
			this.value = new GuiVarString("orbis.gui.value");
			this.inputs.add(this.value);
		}

		@Override
		public String mutate(String input)
		{
			return input + this.value.getData();
		}

		@Override
		public String getDisplayString()
		{
			return "orbis.gui.concatenate";
		}

		@Override
		public List<IGuiVar<String, ?>> getInputs()
		{
			return this.inputs;
		}

		@Override
		public void transferData(List<IGuiVar<String, ?>> prevInputs)
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

	public static class Replace implements IGuiVarMutateExpression<String>
	{
		private GuiVarString replace, with;

		private List<IGuiVar<String, ?>> inputs = Lists.newArrayList();

		public Replace()
		{
			this.replace = new GuiVarString("orbis.gui.replace");
			this.with = new GuiVarString("orbis.gui.with");

			this.inputs.add(this.replace);
			this.inputs.add(this.with);
		}

		@Override
		public String mutate(String input)
		{
			return input.replace(this.replace.getData(), this.with.getData());
		}

		@Override
		public String getDisplayString()
		{
			return "orbis.gui.replace";
		}

		@Override
		public List<IGuiVar<String, ?>> getInputs()
		{
			return this.inputs;
		}

		@Override
		public void transferData(List<IGuiVar<String, ?>> prevInputs)
		{
			if (prevInputs.size() >= 1)
			{
				this.replace.setData(prevInputs.get(0).getData());
			}

			if (prevInputs.size() >= 2)
			{
				this.with.setData(prevInputs.get(1).getData());
			}
		}

		@Override
		public void write(NBTTagCompound tag)
		{
			NBTFunnel funnel = new NBTFunnel(tag);

			funnel.set("replace", this.replace);
			funnel.set("with", this.with);
		}

		@Override
		public void read(NBTTagCompound tag)
		{
			NBTFunnel funnel = new NBTFunnel(tag);

			this.replace = funnel.get("replace");
			this.with = funnel.get("with");

			this.inputs.clear();

			this.inputs.add(this.replace);
			this.inputs.add(this.with);
		}
	}

	public static class Clear implements IGuiVarMutateExpression<String>
	{
		public Clear()
		{

		}

		@Override
		public String mutate(String input)
		{
			return "";
		}

		@Override
		public String getDisplayString()
		{
			return "orbis.gui.clear";
		}

		@Override
		public List<IGuiVar<String, ?>> getInputs()
		{
			return Collections.emptyList();
		}

		@Override
		public void transferData(List<IGuiVar<String, ?>> prevInputs)
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
}