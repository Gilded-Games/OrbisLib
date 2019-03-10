package com.gildedgames.orbis.lib.client.gui.util;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.gildedgames.orbis.lib.util.InputHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;

public class GuiInputSlider extends GuiElement
{
	protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");

	private final float minValue, maxValue;

	public boolean dragging, hovered;

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
	public void onMouseClicked(GuiElement element, final int mouseX, final int mouseY, final int mouseButton)
	{
		if (mouseButton == 0 && this.state().isHoveredAndTopElement())
		{
			this.sliderValue = (InputHelper.getMouseX() - (this.dim().x() + 4)) / (this.dim().width() - 8);
			this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);

			this.dragging = true;
		}
	}

	protected int getHoverState(boolean mouseOver)
	{
		int i = 1;

		if (this.state().isEnabled())
		{
			i = 0;
		}
		else if (mouseOver)
		{
			i = 2;
		}

		return i;
	}

	@Override
	public void onDraw(GuiElement element)
	{
		FontRenderer fontrenderer = this.viewer().mc().fontRenderer;
		this.viewer().mc().getTextureManager().bindTexture(BUTTON_TEXTURES);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.hovered =
				InputHelper.getMouseX() >= this.dim().x() && InputHelper.getMouseY() >= this.dim().y() && InputHelper.getMouseX() < this.dim().x() + this.dim()
						.width()
						&& InputHelper.getMouseY() < this.dim().y() + this.dim().height();

		GlStateManager.enableBlend();
		GlStateManager
				.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
						GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		this.viewer().getActualScreen().drawTexturedModalRect(this.dim().x(), this.dim().y(), 0, 46, (int) (this.dim().width() / 2), (int) this.dim().height());
		this.viewer().getActualScreen().drawTexturedModalRect(this.dim().x() + this.dim().width() / 2, this.dim().y(), (int) (200 - this.dim().width() / 2), 46,
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
			this.sliderValue = (InputHelper.getMouseX() - (this.dim().x() + 4)) / (this.dim().width() - 8);
			this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);

			this.displayString = String.valueOf(this.df.format(this.sliderValue * this.maxValue));
		}

		this.viewer().mc().getTextureManager().bindTexture(BUTTON_TEXTURES);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.viewer().getActualScreen()
				.drawTexturedModalRect(this.dim().x() + (int) (this.sliderValue * (this.dim().width() - 8)), this.dim().y(), 0, 66, 4, 20);
		this.viewer().getActualScreen()
				.drawTexturedModalRect(this.dim().x() + (int) (this.sliderValue * (this.dim().width() - 8)) + 4, this.dim().y(), 196, 66, 4, 20);
	}

	@Override
	public void onMouseReleased(GuiElement element, final int mouseX, final int mouseY, final int state)
	{
		this.dragging = false;
	}
}
