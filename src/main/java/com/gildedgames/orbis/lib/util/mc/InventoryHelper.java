package com.gildedgames.orbis.lib.util.mc;

import com.google.common.collect.Lists;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.List;

public class InventoryHelper
{

	public static List<ItemStack> getItemStacks(Slot[] slots)
	{
		final List<ItemStack> stacks = Lists.newArrayList();

		for (final Slot slot : slots)
		{
			if (slot.getStack() != null && !slot.getStack().isEmpty())
			{
				stacks.add(slot.getStack());
			}
		}

		return stacks;
	}

}
