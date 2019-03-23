package com.gildedgames.orbis.lib.client.gui.util;

import net.minecraft.client.Minecraft;

public interface IDropdownHolder
{
	static IDropdownHolder get()
	{
		Minecraft mc = Minecraft.getInstance();

		if (mc.currentScreen instanceof IDropdownHolder)
		{
			return (IDropdownHolder) mc.currentScreen;
		}

		return null;
	}

	GuiDropdownList getDropdown();
}
