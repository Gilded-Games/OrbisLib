package com.gildedgames.orbis.lib.client.gui.util;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.gildedgames.orbis.lib.util.mc.IText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.function.Function;

public class GuiText extends GuiElement
{
	private IText text;

	private Function<String, String> textMutator;

	private String raw;

	public GuiText(final Rect rect, final IText text)
	{
		super(rect, true);

		this.setText(text);
	}

	public void setTextMutator(final Function<String, String> textMutator)
	{
		this.textMutator = textMutator;

		this.setText(this.text);
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
			final FontRenderer r = Minecraft.getMinecraft().fontRenderer;

			if (this.text.component() instanceof TextComponentTranslation)
			{
				final TextComponentTranslation trans = (TextComponentTranslation) this.text.component();

				if (trans.getKey() == null)
				{
					return;
				}
			}

			String text = this.text.component().getFormattedText();

			if (this.textMutator != null)
			{
				text = this.textMutator.apply(text);
			}

			this.dim().mod().scale(this.text.scale()).width(r.getStringWidth(text) * this.text.scale()).height(r.FONT_HEIGHT)
					.flush();

			this.raw = text;
		}
	}

	@Override
	public void build()
	{

	}

	@Override
	public void onDraw(final GuiElement element)
	{
		if (this.text != null)
		{
			final int color = GuiFrameUtils.changeAlpha(16777215,
					(int) (this.state().getAlpha() * 255));

			this.viewer().getActualScreen()
					.drawString(this.viewer().fontRenderer(), this.raw, (int) this.dim().x(), (int) this.dim().y(), color);
		}
	}
}
