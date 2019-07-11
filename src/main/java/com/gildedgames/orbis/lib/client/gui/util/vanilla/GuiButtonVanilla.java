package com.gildedgames.orbis.lib.client.gui.util.vanilla;

import com.gildedgames.orbis.lib.client.gui.util.GuiFrameUtils;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.rect.Rect;
import net.minecraft.client.gui.widget.button.Button;
import com.mojang.blaze3d.platform.GlStateManager;

public class GuiButtonVanilla extends GuiElement
{
	private final Button button;

	public GuiButtonVanilla(final Rect rect)
	{
		this(rect, button -> { });
	}

	public GuiButtonVanilla(final Rect rect, Button.IPressable handler)
	{
		super(rect, true);

		this.button = new Button((int) rect.width(), (int) rect.height(), (int) rect.x(), (int) rect.y(), "Label", handler);
	}

	public Button getInner()
	{
		return this.button;
	}

	@Override
	public void build()
	{
		this.state().setCanBeTopHoverElement(true);
	}

	@Override
	public void onDraw(IGuiElement element, int mouseX, int mouseY, float partialTicks)
	{
		GlStateManager.pushMatrix();

		GuiFrameUtils.applyAlpha(this.state());

		this.button.x = (int) this.dim().x();
		this.button.y = (int) this.dim().y();

		this.button.setWidth((int) this.dim().width());
		this.button.setHeight((int) this.dim().height());

		this.button.active = this.state().isEnabled();
		this.button.visible = this.state().isVisible();

		this.button.render(mouseX, mouseY, partialTicks);

		GlStateManager.popMatrix();
	}
}
