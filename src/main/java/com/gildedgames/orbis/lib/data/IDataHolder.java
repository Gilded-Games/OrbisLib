package com.gildedgames.orbis.lib.data;

import com.gildedgames.orbis.lib.data.management.IData;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.world.World;

import java.util.Random;

public interface IDataHolder<DATA extends IData> extends NBT
{
	DATA get(World world, Random random);

	int getLargestHeight();

	int getLargestWidth();

	int getLargestLength();
}
