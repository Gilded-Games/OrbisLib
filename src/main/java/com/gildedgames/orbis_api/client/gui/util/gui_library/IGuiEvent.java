package com.gildedgames.orbis_api.client.gui.util.gui_library;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;

public interface IGuiEvent<T extends IGuiElement>
{
	default void onGlobalContextChanged(T element)
	{

	}

	default void onUpdateScreen(T element)
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

	default void onMouseWheel(T element, final int state)
	{

	}

	default void onMouseReleasedOutsideBounds(T element, final int mouseX, final int mouseY, final int state)
	{

	}

	default void onMouseClickMoveOutsideBounds(T element, final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick)
	{

	}

	default void onMouseClickedOutsideBounds(T element, final int mouseX, final int mouseY, final int mouseButton)
	{

	}

	default void onMouseClicked(T element, final int mouseX, final int mouseY, final int mouseButton)
	{

	}

	default void onMouseClickMove(T element, final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick)
	{

	}

	default void onMouseReleased(T element, final int mouseX, final int mouseY, final int state)
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
