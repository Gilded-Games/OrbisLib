package com.gildedgames.orbis.lib.client.gui.util.gui_library;

public interface IGuiEvent
{
	default boolean isMouseClickedEnabled(IGuiElement element, double mouseX, double mouseY, int mouseButton)
	{
		return element.state().isEnabled();
	}

	default boolean isMouseReleasedEnabled(IGuiElement element, final double mouseX, final double mouseY, final int state)
	{
		return element.state().isEnabled();
	}

	default boolean isMouseWheelEnabled(IGuiElement element, double scroll)
	{
		return element.state().isEnabled();
	}

	default boolean isKeyboardEnabled(IGuiElement element)
	{
		return element.state().isEnabled();
	}

	default boolean canBeHovered(IGuiElement element)
	{
		return true;
	}

	default void onGlobalContextChanged(IGuiElement element)
	{

	}

	default void onTick(IGuiElement element)
	{

	}

	default void onHovered(IGuiElement element)
	{

	}

	default void onHoverEnter(IGuiElement element)
	{

	}

	default void onHoverExit(IGuiElement element)
	{

	}

	default void onPreDraw(IGuiElement element)
	{

	}

	default void onPostDraw(IGuiElement element)
	{

	}

	default void onDraw(IGuiElement element, int mouseX, int mouseY, float partialTicks)
	{

	}

	default void onMouseWheel(IGuiElement element, final double state)
	{

	}

	default void onMouseClicked(IGuiElement element, final double mouseX, final double mouseY, final int mouseButton)
	{

	}

	default void onMouseReleased(IGuiElement element, final double mouseX, final double mouseY, final int state)
	{

	}

	default void onKeyTyped(IGuiElement element, final char typedChar, final int keyCode)
	{

	}

	default void onGuiClosed(IGuiElement element)
	{

	}

	default void onKeyPressed(IGuiElement element, int key, int scanCode, int modifiers)
	{

	}

}
