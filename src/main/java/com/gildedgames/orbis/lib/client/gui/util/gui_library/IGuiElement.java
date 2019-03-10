package com.gildedgames.orbis.lib.client.gui.util.gui_library;

import com.gildedgames.orbis.lib.client.rect.RectHolder;

public interface IGuiElement extends RectHolder
{
	IGuiContext context();

	IGuiState state();

	void build(IGuiViewer viewer);
}
