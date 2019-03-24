package com.gildedgames.orbis.lib.client.gui.util.gui_library;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;

import java.util.Collection;
import java.util.List;

public interface IGuiViewer
{
	void setHoveredDescription(List<ITextComponent> desc);

	void notifyGlobalContextChange();

	void notifyBuildingStarted(IGuiElement element);

	void notifyBuildingFinished(IGuiElement element);

	GuiScreen getActualScreen();

	IGuiViewer getPreviousViewer();

	IGuiElement getViewing();

	int getScreenWidth();

	int getScreenHeight();

	Minecraft mc();

	FontRenderer fontRenderer();

	void requestRecacheAndReorderAllVisibleElements();

	/**
	 * Ordered by context of the Z order of each element.
	 * @return
	 */
	List<IGuiElement> getAllVisibleElements();

	Collection<IGuiElement> getAllVisibleElementsBelow(IGuiElement child);

	Collection<IGuiElement> getAllVisibleElementsAbove(IGuiElement child);
}
