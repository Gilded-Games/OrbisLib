package com.gildedgames.orbis_api.client.gui.util.vanilla;

import com.gildedgames.orbis_api.client.PartialTicks;
import com.gildedgames.orbis_api.client.gui.util.GuiFrameUtils;
import com.gildedgames.orbis_api.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis_api.client.rect.Rect;
import com.gildedgames.orbis_api.util.InputHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonVanillaToggled extends GuiElement
{
	public static int TOGGLED_BUTTON_ID = -1;

	private final GuiButton button;

	private int id;

	public GuiButtonVanillaToggled(final Rect rect, final int id)
	{
		super(rect, true);

		this.id = id;

		this.button = new GuiButton(0, (int) rect.x(), (int) rect.y(), (int) rect.width(), (int) rect.height(), "");
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(final int id)
	{
		this.id = id;
	}

	public GuiButton getInner()
	{
		return this.button;
	}

	@Override
	public void build()
	{
		this.state().setCanBeTopHoverElement(true);
	}

	@Override
	public void onMouseClicked(GuiElement element, final int mouseX, final int mouseY, final int mouseButton)
	{
		if (this.button.mousePressed(this.viewer().mc(), mouseX, mouseY))
		{
			this.button.playPressSound(this.viewer().mc().getSoundHandler());
			this.viewer().pushActionPerformed(this.button);

			TOGGLED_BUTTON_ID = this.id;
		}
	}

	@Override
	public void onDraw(GuiElement element)
	{
		GlStateManager.pushMatrix();

		GuiFrameUtils.applyAlpha(this.state());

		this.button.x = (int) this.dim().x();
		this.button.y = (int) this.dim().y();

		this.button.width = (int) this.dim().width();
		this.button.height = (int) this.dim().height();

		this.button.drawButton(Minecraft.getMinecraft(), InputHelper.getMouseX(), InputHelper.getMouseY(), PartialTicks.get());

		if (this.id == TOGGLED_BUTTON_ID)
		{
			Gui.drawRect((int) this.dim().x(), (int) this.dim().y(), (int) this.dim().maxX(), (int) this.dim().maxY(), Integer.MIN_VALUE);
		}

		GlStateManager.popMatrix();
	}
}
