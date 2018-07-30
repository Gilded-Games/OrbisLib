package com.gildedgames.orbis_api.client.gui.util.gui_library;

import com.gildedgames.orbis_api.client.rect.RectHolder;

import java.util.List;

public interface IGuiState extends RectHolder
{
	void setCanBeTopHoverElement(boolean flag);

	boolean canBeTopHoverElement();

	void updateState();

	void listen(IGuiStateListener listener);

	boolean unlisten(IGuiStateListener listener);

	boolean hasBuilt();

	boolean isBuilding();

	List<IGuiEvent> getEvents();

	void addEvent(IGuiEvent event);

	boolean removeEvent(IGuiEvent event);

	float getAlpha();

	void setAlpha(float alpha);

	int getZOrder();

	void setZOrder(int zOrder);

	boolean isHoveredAndTopElement();

	void setHoveredAndTopElement(boolean flag);

	boolean isHovered();

	void setHovered(boolean flag);

	boolean isVisible();

	void setVisible(boolean flag);

	boolean isEnabled();

	void setEnabled(boolean flag);

	boolean isInputEnabled();

	boolean getShouldScaleRender();

	void setShouldScaleRender(boolean flag);

	boolean isInputDisabledWhenNotHovered();

	void setInputDisabledWhenNotHovered(boolean flag);
}
