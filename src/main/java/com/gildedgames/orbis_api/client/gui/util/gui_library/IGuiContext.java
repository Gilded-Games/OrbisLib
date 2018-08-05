package com.gildedgames.orbis_api.client.gui.util.gui_library;

import java.util.List;

public interface IGuiContext
{
	IGuiElement getOwner();

	void addChildren(IGuiElement... children);

	void addChildren(IGuiElement child);

	void addChildNoMods(IGuiElement child);

	void removeChild(IGuiElement child);

	/**
	 * Should also remove all RectModifiers off children
	 */
	void clearChildren();

	List<IGuiElement> getChildren();

	void addParent(IGuiElement parent);

	boolean removeParent(IGuiElement parent);

	List<IGuiElement> getParents();
}
