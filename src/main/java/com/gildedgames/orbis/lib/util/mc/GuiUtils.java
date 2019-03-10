package com.gildedgames.orbis.lib.util.mc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public class GuiUtils
{
	public static void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font)
	{
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;

		if (gui == null)
		{
			return;
		}

		net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(textLines, x, y, gui.width, gui.height, -1, font);
	}
}
