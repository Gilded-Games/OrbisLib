package com.gildedgames.orbis_api.core.variables.conditions;

import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.variables.IGuiVar;
import com.gildedgames.orbis_api.util.mc.NBT;

import java.util.List;
import java.util.Random;

public interface IGuiCondition extends NBT
{

	String getName();

	List<IGuiVar> getVariables();

	boolean resolve(Random rand);

	Pos2D getGuiPos();

	void setGuiPos(Pos2D pos);

}
