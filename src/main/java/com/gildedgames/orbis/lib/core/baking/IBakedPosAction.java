package com.gildedgames.orbis.lib.core.baking;

import com.gildedgames.orbis.lib.processing.DataPrimer;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.util.math.BlockPos;

public interface IBakedPosAction extends NBT
{
	BlockPos getPos();

	void setPos(BlockPos pos);

	void call(DataPrimer primer);

	IBakedPosAction copy();
}
