package com.gildedgames.orbis_api.core.baking;

import com.gildedgames.orbis_api.processing.DataPrimer;
import com.gildedgames.orbis_api.util.mc.NBT;
import net.minecraft.util.math.BlockPos;

public interface IBakedPosAction extends NBT
{
	BlockPos getPos();

	void setPos(BlockPos pos);

	void call(DataPrimer primer);
}
