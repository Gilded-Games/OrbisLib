package com.gildedgames.orbis.lib.util.mc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerGeneric extends Container
{

	public ContainerGeneric()
	{
	}

	@Override
	public boolean canInteractWith(final EntityPlayer player)
	{
		return true;
	}

}
