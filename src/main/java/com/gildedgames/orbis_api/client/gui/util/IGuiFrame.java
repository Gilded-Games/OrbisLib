package com.gildedgames.orbis_api.client.gui.util;

import com.gildedgames.orbis_api.client.rect.RectHolder;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;
import java.util.List;

public interface IGuiFrame extends RectHolder
{

	int getZOrder();

	float getAlpha();

	void setAlpha(float alpha);

	void clearChildren();

	boolean hasInit();

	void onHovered();

	void onHoverEnter();

	void onHoverExit();

	void init();

	List<IGuiFrame> getChildren();

	boolean isVisible();

	void setVisible(boolean flag);

	boolean isEnabled();

	void setEnabled(boolean flag);

	void addChildren(IGuiFrame... children);

	void addChildren(IGuiFrame child);

	void addChildNoMods(IGuiFrame child);

	void removeChild(IGuiFrame child);

	void preDraw();

	void draw();

	void preDrawChildren();

	void onMouseWheel(final int state);

	void publicMouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException;

	void publicMouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick);

	void publicMouseReleased(final int mouseX, final int mouseY, final int state);

	void publicKeyTyped(final char typedChar, final int keyCode) throws IOException;

	void publicActionPerformed(final GuiButton button) throws IOException;

	void publicDrawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY);

	void mouseReleasedOutsideBounds(final int mouseX, final int mouseY, final int state);

	void mouseClickMoveOutsideBounds(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick);

	void mouseClickedOutsideBounds(final int mouseX, final int mouseY, final int mouseButton);
}
