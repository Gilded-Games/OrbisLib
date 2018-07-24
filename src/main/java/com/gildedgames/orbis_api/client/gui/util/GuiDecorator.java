package com.gildedgames.orbis_api.client.gui.util;

import com.gildedgames.orbis_api.client.rect.ModDim2D;
import com.gildedgames.orbis_api.util.Decorator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;

import java.io.IOException;
import java.util.List;

public abstract class GuiDecorator<T extends GuiFrame> extends GuiFrame implements Decorator<T>
{

	private final T element;

	public GuiDecorator(final T element)
	{
		this.element = element;
	}

	@Override
	public T getDecoratedElement()
	{
		return this.element;
	}

	@Override
	public void setShouldScaleRender(boolean shouldScaleRender)
	{
		this.element.setShouldScaleRender(shouldScaleRender);
	}

	@Override
	public IGuiFrame getPrevFrame()
	{
		return this.element.getPrevFrame();
	}

	@Override
	public void preDrawChild(IGuiFrame child)
	{
		this.element.preDrawChild(child);
	}

	@Override
	public List<IGuiFrame> getChildren()
	{
		return this.element.getChildren();
	}

	@Override
	public void initContainerSize()
	{
		this.element.initContainerSize();
	}

	@Override
	public void setInputDisabledWhenNotHovered(boolean flag)
	{
		this.element.setInputDisabledWhenNotHovered(flag);
	}

	@Override
	public boolean isInputEnabled()
	{
		return this.element.isInputEnabled();
	}

	@Override
	public boolean isEnabled()
	{
		return this.element.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		this.element.setEnabled(enabled);
	}

	@Override
	public boolean isVisible()
	{
		return this.element.isVisible();
	}

	@Override
	public void setVisible(boolean visible)
	{
		this.element.setVisible(visible);
	}

	@Override
	public void addChildren(IGuiFrame... children)
	{
		this.element.addChildren(children);
	}

	@Override
	public void addChildNoMods(IGuiFrame child)
	{
		this.element.addChildNoMods(child);
	}

	public <D> D findDecoratedElement(final Class<? extends D> clazz)
	{
		Object element = this.getDecoratedElement();

		while (element != null)
		{
			if (element.getClass().isAssignableFrom(clazz))
			{
				return (D) element;
			}

			if (element instanceof Decorator)
			{
				final Decorator decorator = (Decorator) element;

				element = decorator.getDecoratedElement();

				if (element == null)
				{
					break;
				}
			}
			else
			{
				break;
			}
		}

		return null;
	}

	@Override
	public int getZOrder()
	{
		return this.element.getZOrder();
	}

	@Override
	public float getAlpha()
	{
		return this.element.getAlpha();
	}

	@Override
	public void setAlpha(float alpha)
	{
		this.element.setAlpha(alpha);
	}

	@Override
	public void clearChildren()
	{
		this.element.clearChildren();
	}

	@Override
	public boolean hasInit()
	{
		return this.element.hasInit();
	}

	@Override
	public void onHovered()
	{
		this.element.onHovered();
	}

	@Override
	public void onHoverEnter()
	{
		this.element.onHoverEnter();
	}

	@Override
	public void onHoverExit()
	{
		this.element.onHoverExit();
	}

	@Override
	public void setDrawDefaultBackground(final boolean flag)
	{
		this.element.setDrawDefaultBackground(flag);
	}

	@Override
	public void init()
	{
		this.element.init();
	}

	@Override
	public ModDim2D dim()
	{
		return this.element.dim();
	}

	@Override
	public void drawDefaultBackground()
	{
		this.element.drawDefaultBackground();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
	{
		this.element.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}

	@Override
	public void addChildren(final IGuiFrame element)
	{
		this.element.addChildren(element);
	}

	@Override
	public void removeChild(final IGuiFrame gui)
	{
		this.element.removeChild(gui);
	}

	@Override
	public void initGui()
	{
		super.initGui();

		this.element.initGui();
	}

	@Override
	public void draw()
	{
		this.element.draw();
	}

	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float partialTicks)
	{
		this.preDraw();

		this.element.drawScreen(mouseX, mouseY, partialTicks);

		this.postDraw();
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException
	{
		this.element.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick)
	{
		this.element.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}

	@Override
	protected void mouseReleased(final int mouseX, final int mouseY, final int state)
	{
		this.element.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void handleMouseClick(final Slot slotIn, final int slotId, final int mouseButton, final ClickType type)
	{
		this.element.handleMouseClick(slotIn, slotId, mouseButton, type);
	}

	@Override
	protected void keyTyped(final char typedChar, final int keyCode) throws IOException
	{
		this.element.keyTyped(typedChar, keyCode);
	}

	@Override
	public void onGuiClosed()
	{
		this.element.onGuiClosed();
	}

	@Override
	public void updateScreen()
	{
		this.element.updateScreen();
	}

	@Override
	public void setWorldAndResolution(final Minecraft mc, final int width, final int height)
	{
		super.setWorldAndResolution(mc, width, height);

		this.element.setWorldAndResolution(mc, width, height);
		this.mc = mc;
	}

	@Override
	protected void actionPerformed(final GuiButton button) throws IOException
	{
		this.element.actionPerformed(button);
	}

	@Override
	public void onResize(final Minecraft mcIn, final int w, final int h)
	{
		this.element.onResize(mcIn, w, h);
	}

	protected void postDraw()
	{

	}

}
