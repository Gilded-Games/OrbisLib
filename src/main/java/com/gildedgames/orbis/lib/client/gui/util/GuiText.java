package com.gildedgames.orbis.lib.client.gui.util;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.gildedgames.orbis.lib.util.mc.IText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiText extends GuiElement
{
	private IText text;

	public GuiText(final Rect rect, final IText text)
	{
		super(rect, true);

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
			FontRenderer r = Minecraft.getInstance().fontRenderer;

			if (this.text.component() instanceof TranslationTextComponent)
			{
				TranslationTextComponent trans = (TranslationTextComponent) this.text.component();

				if (trans.getKey() == null)
				{
					return;
				}
			}

			String text = this.text.component().getFormattedText();

			this.dim().mod().scale(this.text.scale()).width(r.getStringWidth(text) * this.text.scale()).height(r.FONT_HEIGHT)
					.flush();
		}
	}

	@Override
	public void build()
	{

	}

	@Override
	public void onDraw(IGuiElement element, int mouseX, int mouseY, float partialTicks)
	{
		if (this.text != null)
		{
			int color = GuiFrameUtils.changeAlpha(16777215,
					(int) (this.state().getAlpha() * 255));

			this.viewer().getActualScreen()
					.drawString(this.viewer().fontRenderer(), this.text.component().getUnformattedComponentText(), (int) this.dim().x(), (int) this.dim().y(), color);
		}
	}
}
