package com.gildedgames.orbis_api.client.gui.util;

import com.gildedgames.orbis_api.client.gui.util.gui_library.*;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.util.InputHelper;
import com.gildedgames.orbis_api.util.ObjectFilter;
import com.gildedgames.orbis_api.util.mc.ContainerGeneric;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class GuiFrame extends GuiContainer implements IGuiViewer
{

	private static boolean preventInnerTyping = false;

	private final IGuiViewer prevFrame;

	private IGuiState state = new GuiState();

	private IGuiRelationships relationships = new GuiRelationships(this);

	private boolean drawDefaultBackground;

	private List<IGuiElement> drawn = Lists.newArrayList();

	private List<IGuiElement> hasBeenPostDrawn = Lists.newArrayList();

	/**
	 * Used to recursively fetch all children from a frame.
	 */
	private List<IGuiElement> childrenCache = Lists.newArrayList();

	public GuiFrame()
	{
		super(new ContainerGeneric());

		this.prevFrame = null;
	}

	public GuiFrame(IGuiViewer prevFrame)
	{
		this(prevFrame, new ContainerGeneric());
	}

	public GuiFrame(IGuiViewer prevFrame, final Container inventorySlotsIn)
	{
		super(inventorySlotsIn);

		this.prevFrame = prevFrame;

		// Set dimensions to that of the screen width and height
		this.getState().dim().set(Dim2D.build().width(this.width).height(this.height).flush());
	}

	public static void preventInnerTyping()
	{
		preventInnerTyping = true;
	}

	/*@Override
	public boolean isInputEnabled()
	{
		return !(!this.isEnabled() || (this.inputDisabledWhenNotHovered && !this.isHoveredOnTop));
	}

	@Override
	public void setShouldScaleRender(boolean shouldScaleRender)
	{
		this.shouldScaleRender = shouldScaleRender;
	}*/

	@Override
	public IGuiViewer getPreviousViewer()
	{
		return this.prevFrame;
	}

	@Override
	public GuiScreen getActualScreen()
	{
		return this;
	}

	public void setDrawDefaultBackground(final boolean flag)
	{
		this.drawDefaultBackground = flag;
	}

	@Override
	public void drawDefaultBackground()
	{

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
	{
		for (final IGuiElement frame : this.children)
		{
			frame.publicDrawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		}
	}

	@Override
	public void initGui()
	{
		if (Minecraft.getMinecraft().currentScreen == this)
		{
			super.initGui();
		}

		if (!this.hasInit)
		{
			this.init();

			this.initContainerSize();

			this.hasInit = true;
		}
	}

	public void initContainerSize()
	{
		//TODO: Dimensions are currnetly width and height of screen
		this.guiLeft = (int) this.getState().dim().x();
		this.guiTop = (int) this.getState().dim().y();

		this.xSize = (int) this.getState().dim().width();
		this.ySize = (int) this.getState().dim().height();
	}

	private void drawContext(IGuiContext context, boolean debugDimRendering)
	{
		IGuiState state = context.getState();

		if (debugDimRendering)
		{
			Gui.drawRect((int) state.dim().x(), (int) state.dim().y(), (int) state.dim().maxX(), (int) state.dim().maxY(), Integer.MAX_VALUE);
		}

		final float x = state.dim().x();
		final float y = state.dim().y();

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();

		GlStateManager.disableLighting();

		GlStateManager.translate(x + state.dim().originX(), y + state.dim().originY(), 0);

		GlStateManager.translate(state.dim().isCenteredX() ? (state.dim().width() / 2) : 0, state.dim().isCenteredY() ? (state.dim().height() / 2) : 0, 0);

		if (state.shouldScaleRender)
		{
			GlStateManager.scale(state.dim().scale(), state.dim().scale(), 0);
		}

		GlStateManager.rotate(state.dim().degrees(), 0.0F, 0.0F, 1.0F);

		GlStateManager.translate(state.dim().isCenteredX() ? -(state.dim().width() / 2) : 0, state.dim().isCenteredY() ? -(state.dim().height() / 2) : 0, 0);

		GlStateManager.translate(-x - state.dim().originX(), -y - state.dim().originY(), 0);

		GuiFrameUtils.applyAlpha(state);

		for (int i = 0; i < state.getEvents().size(); i++)
		{
			IGuiEvent event = state.getEvents().get(i);

			event.onPreDraw(context);
			event.onDraw(context);
		}

		if (state.allChildren != null)
		{
			state.allChildren.clear();

			GuiFrame.fetchAllChildren(state.allChildren, state);

			state.allChildren.sort(Comparator.comparingInt(IGuiElement::getZOrder));
		}
	}

	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float partialTicks)
	{
		// Init allChildren
		this.getAllChildrenSortedByZOrder();

		preventInnerTyping = false;

		if (!this.getState().isVisible())
		{
			return;
		}

		if (this.isHoveredOnTop)
		{
			if (!this.hoverEntered)
			{
				this.onHoverEnter();

				this.hoverEntered = true;
			}

			this.onHovered();
		}
		else if (this.hoverEntered)
		{
			this.onHoverExit();

			this.hoverEntered = false;
		}

		if (this.drawDefaultBackground)
		{
			super.drawDefaultBackground();
		}

		InputHelper.markHoveredAndTopElements(false);

		this.drawn.clear();
		this.hasBeenPostDrawn.clear();

		this.allChildren.forEach((frame) ->
		{
			this.drawContext(frame, false);

			this.drawn.add(frame);

			for (IGuiElement drawnFrame : this.drawn)
			{
				if (this.hasBeenPostDrawn.contains(drawnFrame))
				{
					continue;
				}

				GuiFrame.fetchAllChildren(this.childrenCache, drawnFrame);

				if (this.drawn.containsAll(this.childrenCache))
				{
					drawnFrame.postDrawAllChildren();
					this.hasBeenPostDrawn.add(drawnFrame);
				}

				this.childrenCache.clear();
			}
		});

		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GlStateManager.disableAlpha();

		GlStateManager.popMatrix();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException
	{
		if (!this.isInputEnabled())
		{
			return;
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);

		if (Minecraft.getMinecraft().currentScreen == this)
		{
			for (final IGuiElement frame : this.getAllChildrenSortedByZOrder())
			{
				frame.publicMouseClicked(mouseX, mouseY, mouseButton);
			}
		}
	}

	@Override
	protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick)
	{
		if (!this.isInputEnabled())
		{
			return;
		}

		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

		if (Minecraft.getMinecraft().currentScreen == this)
		{
			for (final IGuiElement frame : this.getAllChildrenSortedByZOrder())
			{
				frame.publicMouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
			}
		}
	}

	@Override
	protected void mouseReleased(final int mouseX, final int mouseY, final int state)
	{
		if (!this.isInputEnabled())
		{
			return;
		}

		super.mouseReleased(mouseX, mouseY, state);

		if (Minecraft.getMinecraft().currentScreen == this)
		{
			for (final IGuiElement frame : this.getAllChildrenSortedByZOrder())
			{
				frame.publicMouseReleased(mouseX, mouseY, state);
			}
		}
	}

	@Override
	public void mouseReleasedOutsideBounds(final int mouseX, final int mouseY, final int state)
	{
		if (Minecraft.getMinecraft().currentScreen == this)
		{
			for (final IGuiElement frame : this.getAllChildrenSortedByZOrder())
			{
				frame.mouseReleasedOutsideBounds(mouseX, mouseY, state);
			}
		}
	}

	@Override
	public void mouseClickMoveOutsideBounds(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick)
	{
		if (Minecraft.getMinecraft().currentScreen == this)
		{
			for (final IGuiElement frame : this.getAllChildrenSortedByZOrder())
			{
				frame.mouseClickMoveOutsideBounds(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
			}
		}
	}

	@Override
	public void mouseClickedOutsideBounds(final int mouseX, final int mouseY, final int mouseButton)
	{
		if (Minecraft.getMinecraft().currentScreen == this)
		{
			for (final IGuiElement frame : this.getAllChildrenSortedByZOrder())
			{
				frame.mouseClickedOutsideBounds(mouseX, mouseY, mouseButton);
			}
		}
	}

	@Override
	protected void handleMouseClick(final Slot slotIn, final int slotId, final int mouseButton, final ClickType type)
	{
		if (!this.isInputEnabled())
		{
			return;
		}

		if (Minecraft.getMinecraft().currentScreen == this)
		{
			super.handleMouseClick(slotIn, slotId, mouseButton, type);
		}
	}

	protected void keyTypedInner(final char typedChar, final int keyCode) throws IOException
	{
		if (!preventInnerTyping)
		{
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		if (!this.isInputEnabled())
		{
			return;
		}

		super.handleMouseInput();

		final int i = Mouse.getEventDWheel();

		if (i != 0)
		{
			this.onMouseWheel(i);
		}
	}

	@Override
	public void onMouseWheel(final int state)
	{
		if (!this.isInputEnabled())
		{
			return;
		}

		if (Minecraft.getMinecraft().currentScreen == this)
		{
			for (final IGuiElement frame : this.getAllChildrenSortedByZOrder())
			{
				frame.onMouseWheel(state);
			}
		}
	}

	@Override
	protected void keyTyped(final char typedChar, final int keyCode) throws IOException
	{
		if (!this.isInputEnabled())
		{
			return;
		}

		if (Minecraft.getMinecraft().currentScreen == this)
		{
			this.keyTypedInner(typedChar, keyCode);
		}

		if (Minecraft.getMinecraft().currentScreen == this)
		{
			for (final IGuiElement frame : this.getAllChildrenSortedByZOrder())
			{
				frame.publicKeyTyped(typedChar, keyCode);
			}
		}
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		for (final IGuiElement frame : this.children)
		{
			final GuiScreen gui = ObjectFilter.cast(frame, GuiScreen.class);

			if (gui != null)
			{
				gui.onGuiClosed();
			}
		}
	}

	@Override
	public void updateScreen()
	{
		if (!this.isEnabled())
		{
			return;
		}

		super.updateScreen();

		for (final IGuiElement frame : this.children)
		{
			final GuiScreen gui = ObjectFilter.cast(frame, GuiScreen.class);

			if (gui != null)
			{
				gui.updateScreen();
			}
		}
	}

	@Override
	public void setWorldAndResolution(final Minecraft mc, final int width, final int height)
	{
		super.setWorldAndResolution(mc, width, height);

		for (final IGuiElement frame : this.children)
		{
			final GuiScreen gui = ObjectFilter.cast(frame, GuiScreen.class);

			if (gui != null)
			{
				gui.setWorldAndResolution(mc, width, height);
			}
		}
	}

	@Override
	protected void actionPerformed(final GuiButton button) throws IOException
	{
		if (!this.isInputEnabled())
		{
			return;
		}

		super.actionPerformed(button);

		for (final IGuiElement frame : this.children)
		{
			frame.publicActionPerformed(button);
		}
	}

	@Override
	public void onResize(final Minecraft mcIn, final int w, final int h)
	{
		super.onResize(mcIn, w, h);

		for (final IGuiElement frame : this.children)
		{
			final GuiScreen gui = ObjectFilter.cast(frame, GuiScreen.class);

			if (gui != null)
			{
				gui.onResize(mcIn, w, h);
			}
		}
	}
}