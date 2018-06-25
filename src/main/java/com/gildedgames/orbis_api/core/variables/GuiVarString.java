package com.gildedgames.orbis_api.core.variables;

import com.gildedgames.orbis_api.client.gui.util.GuiInput;
import com.gildedgames.orbis_api.client.gui.util.IGuiInputListener;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.core.variables.displays.GuiVarDisplay;
import net.minecraft.nbt.NBTTagCompound;

public class GuiVarString implements IGuiVar<String, GuiInput>, IGuiInputListener
{
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
	public String getName()
	{
		return this.name;
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
	public void write(NBTTagCompound tag)
	{
		tag.setString("data", this.data);
		tag.setString("name", this.name);
		tag.setInteger("maxStringLength", this.maxStringLength);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		this.data = tag.getString("data");
		this.name = tag.getString("name");
		this.maxStringLength = tag.getInteger("maxStringLength");
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