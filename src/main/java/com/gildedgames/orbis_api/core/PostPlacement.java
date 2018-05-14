package com.gildedgames.orbis_api.core;

import com.gildedgames.orbis_api.block.BlockDataContainer;
import net.minecraft.world.World;

import java.util.Random;

public interface PostPlacement
{

	void postGenerate(World world, Random rand, ICreationData<?> data, BlockDataContainer container);

}
