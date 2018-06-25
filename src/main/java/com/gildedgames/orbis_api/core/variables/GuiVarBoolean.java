package com.gildedgames.orbis_api.core.variables;

import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.variables.displays.GuiTickBox;
import com.gildedgames.orbis_api.core.variables.displays.GuiVarDisplay;
import net.minecraft.nbt.NBTTagCompound;

public class GuiVarBoolean implements IGuiVar<Boolean, GuiTickBox>
{
	private boolean data;

	private String name;

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
	public String getName()
	{
		return this.name;
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
}