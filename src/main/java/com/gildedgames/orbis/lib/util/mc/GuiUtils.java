package com.gildedgames.orbis.lib.util.mc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public class GuiUtils
{
	public static void drawHoveringText(List<ITextComponent> textLines, int x, int y, FontRenderer font)
	{
		Screen gui = Minecraft.getInstance().currentScreen;

		if (gui == null)
		{
			return;
		}

		// TODO: Avoid allocation here
		List<String> strings = new ArrayList<>(textLines.size());

		for (ITextComponent component : textLines)
		{
			strings.add(component.toString());
		}

		// TODO: Don't use Forge's internal classes!!
		net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(strings, x, y, gui.width, gui.height, -1, font);
	}
}
