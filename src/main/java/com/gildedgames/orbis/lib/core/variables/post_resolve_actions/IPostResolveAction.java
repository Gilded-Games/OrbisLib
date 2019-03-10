package com.gildedgames.orbis.lib.core.variables.post_resolve_actions;

import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.core.variables.IGuiVarDisplayChild;
import com.gildedgames.orbis.lib.core.variables.IGuiVarDisplayContents;
import com.gildedgames.orbis.lib.util.mc.NBT;

import java.util.Random;

public interface IPostResolveAction extends NBT, IGuiVarDisplayContents, IGuiVarDisplayChild
{
	String getName();

	void resolve(Random rand);

	Pos2D getGuiPos();

	void setGuiPos(Pos2D pos);
}
