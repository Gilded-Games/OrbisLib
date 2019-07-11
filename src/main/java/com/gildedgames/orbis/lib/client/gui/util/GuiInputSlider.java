package com.gildedgames.orbis.lib.client.gui.util;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;

public class GuiInputSlider extends GuiElement
{
	private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");

	private final float minValue, maxValue;

	private boolean dragging, hovered;

	private String displayString;

	private float sliderValue;

	private DecimalFormat df;

	public GuiInputSlider(final Rect rect, float minValue, float maxValue, float sliderValue)
	{
		super(rect, true);

		this.minValue = minValue;
		this.maxValue = maxValue;

		this.sliderValue = sliderValue;

		this.df = new DecimalFormat();
		this.df.setMaximumFractionDigits(1);

		this.displayString = String.valueOf(this.df.format(this.sliderValue * this.maxValue));
	}

	public float getSliderValue()
	{
		return this.sliderValue;
	}

	public void setSliderValue(float sliderValue)
	{
		this.sliderValue = sliderValue;

		this.displayString = String.valueOf(this.df.format(this.sliderValue * this.maxValue));
	}

	@Override
	public void build()
	{
		this.state().setCanBeTopHoverElement(true);
	}

	@Override
	public void onMouseClicked(IGuiElement element, final double mouseX, final double mouseY, final int mouseButton)
	{
		if (mouseButton == 0 && this.state().isHoveredAndTopElement())
		{
			this.sliderValue = ((int) mouseX - (this.dim().x() + 4)) / (this.dim().width() - 8);
			this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);

			this.dragging = true;
		}
	}

	@Override
	public void onDraw(IGuiElement element, int mouseX, int mouseY, float partialTicks)
	{
		FontRenderer fontrenderer = this.viewer().mc().fontRenderer;
		this.viewer().mc().getTextureManager().bindTexture(BUTTON_TEXTURES);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.hovered = mouseX >= this.dim().x() && mouseX < this.dim().x() + this.dim().width()
				&& mouseY >= this.dim().y() && mouseY < this.dim().y() + this.dim().height();

		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		this.viewer().getActualScreen().blit((int) this.dim().x(), (int) this.dim().y(), 0, 46, (int) (this.dim().width() / 2), (int) this.dim().height());
		this.viewer().getActualScreen().blit((int) (this.dim().x() + this.dim().width() / 2), (int) this.dim().y(), (int) (200 - this.dim().width() / 2), 46,
				(int) (this.dim().width() / 2), (int) this.dim().height());

		int j = 14737632;

		if (!this.state().isEnabled())
		{
			j = 10526880;
		}
		else if (this.hovered)
		{
			j = 16777120;
		}

		this.viewer().getActualScreen().drawCenteredString(fontrenderer, this.displayString, (int) (this.dim().x() + this.dim().width() / 2),
				(int) (this.dim().y() + (this.dim().height() - 8) / 2), j);

		if (this.dragging)
		{
			this.sliderValue = (mouseX - (this.dim().x() + 4)) / (this.dim().width() - 8);
			this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);

			this.displayString = String.valueOf(this.df.format(this.sliderValue * this.maxValue));
		}

		this.viewer().mc().getTextureManager().bindTexture(BUTTON_TEXTURES);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.viewer().getActualScreen()
				.blit((int) this.dim().x() + (int) (this.sliderValue * (this.dim().width() - 8)), (int) this.dim().y(), 0, 66, 4, 20);
		this.viewer().getActualScreen()
				.blit((int) this.dim().x() + (int) (this.sliderValue * (this.dim().width() - 8)) + 4, (int) this.dim().y(), 196, 66, 4, 20);
	}

	@Override
	public void onMouseReleased(IGuiElement element, final double mouseX, final double mouseY, final int state)
	{
		this.dragging = false;
	}
}
