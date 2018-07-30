package com.gildedgames.orbis_api.client.gui.util.gui_library;

public interface IGuiStateListener
{
	default void onSetVisible(IGuiState state, boolean oldValue, boolean newValue)
	{

	}

	default void onSetZOrder(IGuiState state, int oldValue, int newValue)
	{

	}
}
