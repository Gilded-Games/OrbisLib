package com.gildedgames.orbis.lib.client.gui.util.events;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiEvent;

/**
 * Except "mouse released", which is still enabled.
 */
public class MouseInputDisabledWhenNotHovered implements IGuiEvent
{
	@Override
	public boolean isMouseClickedEnabled(IGuiElement element, double mouseX, double mouseY, int mouseButton)
	{
		return element.state().isEnabled() && element.state().isHoveredAndTopElement();
	}

	@Override
	public boolean isMouseReleasedEnabled(IGuiElement element, final double mouseX, final double mouseY, final int state)
	{
		return true;
	}

	@Override
	public boolean isMouseWheelEnabled(IGuiElement element, final double scroll)
	{
		return element.state().isEnabled() && element.state().isHoveredAndTopElement();
	}

}
