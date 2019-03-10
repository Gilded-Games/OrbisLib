package com.gildedgames.orbis.lib.core;

import com.gildedgames.orbis.lib.block.BlockDataContainer;
import net.minecraft.world.World;

import java.util.Random;

public interface PostPlacement
{

	void postGenerate(World world, Random rand, ICreationData<?> data, BlockDataContainer container);

}
