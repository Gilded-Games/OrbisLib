package com.gildedgames.orbis.lib.client.gui.util.gui_library;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;

public interface IGuiEvent<T extends IGuiElement>
{
	default boolean isMouseClickedEnabled(IGuiElement element, double mouseX, double mouseY, int mouseButton)
	{
		return element.state().isEnabled();
	}

	default boolean isMouseClickMoveEnabled(T element, final double mouseX, final double mouseY, final int clickedMouseButton)
	{
		return element.state().isEnabled();
	}

	default boolean isMouseReleasedEnabled(IGuiElement element, final double mouseX, final double mouseY, final int state)
	{
		return element.state().isEnabled();
	}

	default boolean isMouseWheelEnabled(IGuiElement element, double state)
	{
		return element.state().isEnabled();
	}

	default boolean isHandleMouseClickEnabled(T element, final Slot slotIn, final int slotId, final int mouseButton, final ClickType type)
	{
		return element.state().isEnabled();
	}

	default boolean isKeyboardEnabled(T element)
	{
		return element.state().isEnabled();
	}

	default boolean canBeHovered(T element)
	{
		return true;
	}

	default void onGlobalContextChanged(T element)
	{

	}

	default void onTick(T element)
	{

	}

	default void onHovered(T element)
	{

	}

	default void onHoverEnter(T element)
	{

	}

	default void onHoverExit(T element)
	{

	}

	default void onPreDraw(T element)
	{

	}

	default void onPostDraw(T element)
	{

	}

	default void onDraw(T element)
	{

	}

	default void onMouseWheel(IGuiElement element, final double state)
	{

	}

	default void onMouseClicked(IGuiElement element, final double mouseX, final double mouseY, final int mouseButton)
	{

	}

	default void onMouseClickMove(T element, final double mouseX, final double mouseY, final int clickedMouseButton)
	{

	}

	default void onMouseReleased(IGuiElement element, final double mouseX, final double mouseY, final int state)
	{

	}

	default void onKeyTyped(T element, final char typedChar, final int keyCode)
	{

	}

	default void onActionPerformed(T element, final GuiButton button)
	{

	}

	default void onDrawGuiContainerBackgroundLayer(T element, final float partialTicks, final int mouseX, final int mouseY)
	{

	}

	default void onHandleMouseClick(T element, final Slot slotIn, final int slotId, final int mouseButton, final ClickType type)
	{

	}

	default void onGuiClosed(T element)
	{

	}
}
