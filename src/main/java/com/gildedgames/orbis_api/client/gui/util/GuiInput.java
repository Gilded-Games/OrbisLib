package com.gildedgames.orbis_api.client.gui.util;

import com.gildedgames.orbis_api.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis_api.client.gui.util.gui_library.GuiViewer;
import com.gildedgames.orbis_api.client.rect.Rect;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.util.Set;

public class GuiInput extends GuiElement
{
	private final GuiTextField field;

	private Set<IGuiInputListener> listeners = Sets.newHashSet();

	public GuiInput(final Rect rect)
	{
		super(rect, true);

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
	public void build()
	{
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onGuiClosed(GuiElement element)
	{
		Keyboard.enableRepeatEvents(false);
	}

	//TODO
	/*@Override
	public void mouseClickedOutsideBounds(int mouseX, int mouseY, int mouseButton)
	{
		super.mouseClickedOutsideBounds(mouseX, mouseY, mouseButton);

		if (this.field.getVisible())
		{
			this.field.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}*/

	@Override
	public void onMouseClicked(GuiElement element, final int mouseX, final int mouseY, final int mouseButton)
	{
		if (this.field.getVisible())
		{
			this.field.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void onKeyTyped(GuiElement element, final char typedChar, final int keyCode)
	{
		if (keyCode == Keyboard.KEY_ESCAPE)
		{
			this.getInner().setFocused(false);
		}

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
	public void onDraw(GuiElement element)
	{
		if (this.getInner().isFocused())
		{
			GuiViewer.preventInnerTyping();
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
	public void onUpdateScreen(GuiElement element)
	{
		this.field.updateCursorCounter();
	}
}
