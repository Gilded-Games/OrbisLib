package com.gildedgames.orbis.lib.client.gui.util;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiViewer;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.google.common.collect.Lists;
import net.minecraft.client.KeyboardListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class GuiInput extends GuiElement
{
	private final KeyboardListener keyboardListener;

	private final TextFieldWidget field;

	private List<IGuiInputListener> listeners = Lists.newArrayList();

	public GuiInput(final Rect rect)
	{
		super(rect, true);

		this.field = new TextFieldWidget(Minecraft.getInstance().fontRenderer, (int) rect.x(), (int) rect.y(), (int) rect.width(), (int) rect.height(), "Label");
		this.keyboardListener = Minecraft.getInstance().keyboardListener;
	}

	public void listen(IGuiInputListener listener)
	{
		if (!this.listeners.contains(listener))
		{
			this.listeners.add(listener);
		}
	}

	public boolean unlisten(IGuiInputListener listener)
	{
		return this.listeners.remove(listener);
	}

	public TextFieldWidget getInner()
	{
		return this.field;
	}

	@Override
	public void build()
	{
		this.keyboardListener.enableRepeatEvents(true);
	}

	@Override
	public void onGuiClosed(IGuiElement element)
	{
		this.keyboardListener.enableRepeatEvents(false);
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
	public void onMouseClicked(IGuiElement element, final double mouseX, final double mouseY, final int mouseButton)
	{
		if (this.field.getVisible())
		{
			this.field.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void onKeyTyped(IGuiElement element, final char typedChar, final int modifiers)
	{
		if (this.field.isFocused() && this.field.getVisible())
		{
			this.field.charTyped(typedChar, modifiers);
		}
	}

	@Override
	public void onKeyPressed(IGuiElement element, int key, int scanCode, int modifiers)
	{
		if (key == GLFW.GLFW_KEY_ESCAPE)
		{
			this.getInner().setFocused2(false);
		}

		if (this.field.getVisible())
		{
			if (this.field.isFocused() && key == GLFW.GLFW_KEY_ENTER)
			{
				this.listeners.forEach(IGuiInputListener::onPressEnter);
			}
		}
	}

	@Override
	public void onDraw(IGuiElement element, int mouseX, int mouseY, float partialTicks)
	{
		if (this.getInner().isFocused())
		{
			GuiViewer.preventInnerTyping();
		}

		this.field.x = (int) this.dim().x();
		this.field.y = (int) this.dim().y();

		this.field.setWidth((int) this.dim().width());
		this.field.setHeight((int) this.dim().height());

		if (this.field.getVisible())
		{
			this.field.render(mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public void onTick(IGuiElement element)
	{
		this.field.tick();
	}
}
