package com.gildedgames.orbis_api.preparation;

import net.minecraft.util.ResourceLocation;

import java.util.Collection;

public interface IPrepManagerPool
{

	IPrepManager get(ResourceLocation registryId);

	Collection<IPrepManager> getManagers();

}
