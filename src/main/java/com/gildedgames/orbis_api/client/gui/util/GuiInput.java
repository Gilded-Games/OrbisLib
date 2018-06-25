package com.gildedgames.orbis_api.client.gui.util;

import com.gildedgames.orbis_api.client.rect.Rect;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Set;

public class GuiInput extends GuiFrame
{
	private final GuiTextField field;

	private Set<IGuiInputListener> listeners = Sets.newHashSet();

	public GuiInput(final Rect rect)
	{
		super(rect);

		this.field = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, (int) rect.x(), (int) rect.y(), (int) rect.width(), (int) rect.height());
	}

	public void listen(IGuiInputListener listener)
	{
		this.listeners.add(listener);
	}

	public boolean unlisten(IGuiInputListener listener)
	{
		return this.listeners.remove(listener);
	}

	public GuiTextField getInner()
	{
		return this.field;
	}

	@Override
	public void init()
	{
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void mouseClickedOutsideBounds(int mouseX, int mouseY, int mouseButton)
	{
		super.mouseClickedOutsideBounds(mouseX, mouseY, mouseButton);

		if (this.field.getVisible())
		{
			this.field.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if (this.field.getVisible())
		{
			this.field.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void keyTyped(final char typedChar, final int keyCode) throws IOException
	{
		if (keyCode == Keyboard.KEY_ESCAPE)
		{
			this.getInner().setFocused(false);
		}

		super.keyTyped(typedChar, keyCode);

		if (this.field.getVisible())
		{
			this.field.textboxKeyTyped(typedChar, keyCode);

			if (this.field.isFocused() && keyCode == Keyboard.KEY_RETURN)
			{
				this.listeners.forEach(IGuiInputListener::onPressEnter);
			}
		}
	}

	@Override
	public void draw()
	{
		if (this.getInner().isFocused())
		{
			GuiFrame.preventInnerTyping = true;
		}
		else
		{
			GuiFrame.preventInnerTyping = false;
		}

		this.field.x = (int) this.dim().x();
		this.field.y = (int) this.dim().y();

		this.field.width = (int) this.dim().width();
		this.field.height = (int) this.dim().height();

		if (this.field.getVisible())
		{
			this.field.drawTextBox();
		}
	}

	@Override
	public void updateScreen()
	{
		this.field.updateCursorCounter();
	}
}
