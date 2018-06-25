package com.gildedgames.orbis_api.core.variables.conditions;

import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.variables.GuiVarInteger;
import com.gildedgames.orbis_api.core.variables.IGuiVar;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Random;

public class GuiConditionRatio implements IGuiCondition
{
	private List<IGuiVar> variables = Lists.newArrayList();

	private GuiVarInteger numerator, denominator;

	private Pos2D guiPos = Pos2D.ORIGIN;

	public GuiConditionRatio()
	{
		this.numerator = new GuiVarInteger("Numerator");
		this.denominator = new GuiVarInteger("Denominator");

		this.variables.add(this.numerator);
		this.variables.add(this.denominator);
	}

	@Override
	public String getName()
	{
		return "Ratio";
	}

	@Override
	public List<IGuiVar> getVariables()
	{
		return this.variables;
	}

	@Override
	public boolean resolve(Random rand)
	{
		if (this.denominator.getData() <= 0 || this.numerator.getData() <= 0 || this.denominator.getData() < 2)
		{
			return true;
		}

		return rand.nextInt(this.denominator.getData()) >= this.numerator.getData();
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

		funnel.set("numerator", this.numerator);
		funnel.set("denominator", this.denominator);
		funnel.set("guiPos", this.guiPos, NBTFunnel.POS2D_SETTER);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.numerator = funnel.get("numerator");
		this.denominator = funnel.get("denominator");
		this.guiPos = funnel.getWithDefault("guiPos", NBTFunnel.POS2D_GETTER, () -> this.guiPos);

		this.variables.clear();

		this.variables.add(this.numerator);
		this.variables.add(this.denominator);
	}
}
