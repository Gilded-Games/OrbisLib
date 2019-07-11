package com.gildedgames.orbis.lib.core.variables.conditions;

import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.core.variables.GuiVarFloatRange;
import com.gildedgames.orbis.lib.core.variables.IGuiVar;
import com.gildedgames.orbis.lib.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundNBT;

import java.util.List;
import java.util.Random;

public class GuiConditionPercentage implements IGuiCondition
{
	private List<IGuiVar> variables = Lists.newArrayList();

	private GuiVarFloatRange percent;

	private Pos2D guiPos = Pos2D.ORIGIN;

	private GuiVarDisplay parentDisplay;

	public GuiConditionPercentage()
	{
		this.percent = new GuiVarFloatRange("orbis.gui.value", 0.0F, 100.0F);

		this.percent.setData(50.0F);

		this.variables.add(this.percent);
	}

	@Override
	public String getName()
	{
		return "orbis.gui.percentage";
	}

	@Override
	public List<IGuiVar> getVariables()
	{
		return this.variables;
	}

	@Override
	public boolean resolve(Random rand)
	{
		return this.percent.getData() >= rand.nextFloat() * 100.0F;
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
	public void write(CompoundNBT tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("percent", this.percent);
		funnel.set("guiPos", this.guiPos, NBTFunnel.POS2D_SETTER);
	}

	@Override
	public void read(CompoundNBT tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.percent = funnel.get("percent");
		this.guiPos = funnel.getWithDefault("guiPos", NBTFunnel.POS2D_GETTER, () -> this.guiPos);

		this.variables.clear();

		this.variables.add(this.percent);
	}

	@Override
	public void setParentDisplay(GuiVarDisplay parentDisplay)
	{
		this.parentDisplay = parentDisplay;
	}
}
