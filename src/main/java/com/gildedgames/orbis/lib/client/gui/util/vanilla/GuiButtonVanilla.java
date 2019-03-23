package com.gildedgames.orbis.lib.client.gui.util.vanilla;

import com.gildedgames.orbis.lib.client.PartialTicks;
import com.gildedgames.orbis.lib.client.gui.util.GuiFrameUtils;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.gildedgames.orbis.lib.util.InputHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonVanilla extends GuiElement
{
	private final GuiButton button;

	public GuiButtonVanilla(final Rect rect)
	{
		super(rect, true);

		this.button = new GuiButton(0, (int) rect.x(), (int) rect.y(), (int) rect.width(), (int) rect.height(), "") {
			@Override
			public void onClick(double mouseX, double mouseY)
			{
				GuiButtonVanilla.this.viewer().pushActionPerformed(this);
			}
		};
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
	public void onDraw(GuiElement element)
	{
		GlStateManager.pushMatrix();

		GuiFrameUtils.applyAlpha(this.state());

		this.button.x = (int) this.dim().x();
		this.button.y = (int) this.dim().y();

		this.button.width = (int) this.dim().width();
		this.button.height = (int) this.dim().height();

		this.button.enabled = this.state().isEnabled();
		this.button.visible = this.state().isVisible();

		this.button.render(InputHelper.getMouseX(), InputHelper.getMouseY(), PartialTicks.get());

		GlStateManager.popMatrix();
	}
}
