package com.gildedgames.orbis_api.core.variables;

import com.gildedgames.orbis_api.client.gui.util.GuiInput;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.core.variables.displays.GuiVarDisplay;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.StringUtils;

public class GuiVarInteger implements IGuiVar<Integer, GuiInput>
{
	private int data;

	private String name;

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

	}

	@Override
	public String getName()
	{
		return this.name;
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
}