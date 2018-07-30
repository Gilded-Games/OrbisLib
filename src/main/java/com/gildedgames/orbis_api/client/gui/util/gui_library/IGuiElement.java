package com.gildedgames.orbis_api.client.gui.util.gui_library;

import com.gildedgames.orbis_api.client.rect.RectHolder;

public interface IGuiElement extends RectHolder
{
	IGuiContext context();

	IGuiState state();

	void build(IGuiViewer viewer);
}
