package com.gildedgames.orbis.lib.util.mc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class ContainerGeneric extends Container
{

	public ContainerGeneric()
	{
		super(null, -1);
	}

	@Override
	public boolean canInteractWith(final PlayerEntity player)
	{
		return true;
	}

}
