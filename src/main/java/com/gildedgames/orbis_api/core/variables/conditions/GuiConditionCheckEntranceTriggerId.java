package com.gildedgames.orbis_api.core.variables.conditions;

import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.variables.GuiVarString;
import com.gildedgames.orbis_api.core.variables.IGuiVar;
import com.gildedgames.orbis_api.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis_api.data.pathway.IEntrance;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Random;

public class GuiConditionCheckEntranceTriggerId implements IGuiConditionEntrance
{
	private List<IGuiVar> variables = Lists.newArrayList();

	private GuiVarString guiVarTriggerId;

	private Pos2D guiPos = Pos2D.ORIGIN;

	public GuiConditionCheckEntranceTriggerId()
	{
		this.guiVarTriggerId = new GuiVarString("orbis.gui.trigger_id");

		this.variables.add(this.guiVarTriggerId);
	}

	@Override
	public String getName()
	{
		return "orbis.gui.check_entrance_trigger_id";
	}

	@Override
	public List<IGuiVar> getVariables()
	{
		return this.variables;
	}

	@Override
	public boolean resolve(Random rand)
	{
		return true;
	}

	@Override
	public Pos2D getGuiPos()
	{
		return this.guiPos;
	}

	@Override
	public void setGuiPos(Pos2D pos)
	{
		this.guiPos = pos;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("guiVarTriggerId", this.guiVarTriggerId);
		funnel.set("guiPos", this.guiPos, NBTFunnel.POS2D_SETTER);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.guiVarTriggerId = funnel.get("guiVarTriggerId");
		this.guiPos = funnel.getWithDefault("guiPos", NBTFunnel.POS2D_GETTER, () -> this.guiPos);

		this.variables.clear();

		this.variables.add(this.guiVarTriggerId);
	}

	@Override
	public void setParentDisplay(GuiVarDisplay parentDisplay)
	{

	}

	@Override
	public boolean canConnectTo(IEntrance entrance)
	{
		return this.guiVarTriggerId.getData().equals(entrance.getTriggerId());
	}
}
