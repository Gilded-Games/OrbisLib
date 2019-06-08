package com.gildedgames.orbis.lib.client.gui.util;

import com.gildedgames.orbis.lib.client.gui.data.Text;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.rect.Dim2D;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.gildedgames.orbis.lib.util.mc.IText;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class GuiTextBox extends GuiElement
{

	private final boolean centerFormat;

	private IText[] text;

	private Function<String, String> textMutator;

	public GuiTextBox(final Rect dim, final boolean centerFormat, final IText... text)
	{
		super(dim, true);

		this.text = text;
		this.centerFormat = centerFormat;
	}

	public void setTextMutator(final Function<String, String> textMutator)
	{
		this.textMutator = textMutator;
	}

	public void setText(final IText... text)
	{
		this.text = text;
	}

	@Override
	public void build()
	{
		int textHeight = 0;

		if (this.dim().width() == 0)
		{
			throw new IllegalArgumentException("A GuiTextBox is attempting to build with 0 width. This cannot work. Please fix: " + this);
		}

		final float halfWidth = this.dim().width() / 2;

		for (final IText t : this.text)
		{
			if (t.component().getFormattedText().isEmpty())
			{
				continue;
			}

			String formatted = t.component().getFormattedText();

			if (this.textMutator != null)
			{
				formatted = this.textMutator.apply(formatted);
			}

			final String[] strings = formatted.split(System.lineSeparator() + "|\\\\n");

			final List<String> stringList = new ArrayList<>(strings.length);
			Collections.addAll(stringList, strings);

			for (final String string : stringList)
			{
				final List<String> newStrings = this.viewer().fontRenderer().listFormattedStringToWidth(string, (int) (this.dim().width() / t.scale()));

				for (final String s : newStrings)
				{
					final GuiText textElement;

					if (this.centerFormat)
					{
						textElement = new GuiText(Dim2D.build().pos(halfWidth, textHeight).centerX(true).flush(),
								new Text(new TextComponentString(s), t.scale()));
					}
					else
					{
						textElement = new GuiText(Dim2D.build().pos(0, textHeight).flush(), new Text(new TextComponentString(s), t.scale()));
					}

					textElement.state().setAlpha(this.state().getAlpha());

					this.context().addChildren(textElement);

					textHeight += 1.2f * t.scaledHeight();
				}
			}
		}

		this.dim().mod().height(textHeight).flush();
	}

	@Override
	public void onDraw(final GuiElement element)
	{
		for (final IGuiElement c : this.context().getChildren())
		{
			if (c instanceof GuiText)
			{
				final GuiText text = (GuiText) c;

				text.state().setAlpha(this.state().getAlpha());
			}
		}
	}

}