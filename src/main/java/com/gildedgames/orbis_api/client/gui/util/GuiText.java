package com.gildedgames.orbis_api.client.gui.util;

import com.gildedgames.orbis_api.client.rect.Rect;
import com.gildedgames.orbis_api.util.mc.IText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class GuiText extends GuiFrame
{
	private IText text;

	public GuiText(final Rect rect, final IText text)
	{
		super(rect);
		this.setText(text);
	}

	public void setText(final IText component)
	{
		this.text = component;

		if (component == null)
		{
			this.dim().mod().width(0).flush();
		}
		else
		{
			FontRenderer r = Minecraft.getMinecraft().fontRenderer;

			this.dim().mod().scale(this.text.scale()).width(r.getStringWidth(this.text.component().getFormattedText())).height(r.FONT_HEIGHT)
					.flush();
		}
	}

	@Override
	public void init()
	{

	}

	@Override
	public void draw()
	{
		if (this.text != null)
		{
			int color = GuiFrameUtils.changeAlpha(16777215,
					(int) (this.getAlpha() * 255));

			this.drawString(this.fontRenderer, this.text.component().getUnformattedText(), (int) this.dim().x(), (int) this.dim().y(), color);
		}
	}
}
