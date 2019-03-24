package com.gildedgames.orbis.lib.world.instances;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ModDimension;

public interface IInstanceFactory<T extends IInstance>
{
	T createInstance();

	ResourceLocation getUniqueName();

	ModDimension getDimensionType();
}
