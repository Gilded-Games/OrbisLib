package com.gildedgames.orbis_api.client.gui.util.vanilla;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.client.gui.util.GuiFrameUtils;
import com.gildedgames.orbis_api.client.gui.util.gui_library.*;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.util.InputHelper;
import com.gildedgames.orbis_api.util.mc.GuiUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;

public abstract class GuiFrameCreative extends GuiContainerCreativePublic implements IGuiViewer
{
	private static boolean preventInnerTyping = false;

	private final List<IGuiElement> allParentsRecursive = Lists.newArrayList();

	private final IGuiViewer previousViewer;

	private List<IGuiElement> allVisibleElements = Lists.newArrayList();

	private boolean drawDefaultBackground;

	private IGuiElement viewing;

	private List<IGuiElement> drawn = Lists.newArrayList();

	private List<IGuiElement> hasBeenPostDrawn = Lists.newArrayList();

	/**
	 * Used to recursively fetch all children from a frame.
	 */
	private List<IGuiElement> childrenCache = Lists.newArrayList();

	private List<IGuiElement> elementsCurrentlyBuilding = Lists.newArrayList();

	private boolean recacheRequested, contextChanged;

	private Map<IGuiElement, List<IGuiElement>> subListCache = Maps.newHashMap();

	private List<String> hoverDescription;

	public GuiFrameCreative(final EntityPlayer player)
	{
		super(player);

		this.viewing = new GuiElement(Dim2D.flush(), false);
		this.previousViewer = null;
	}

	public GuiFrameCreative(IGuiElement viewing, final EntityPlayer player)
	{
		this(viewing, null, player);
	}

	public GuiFrameCreative(IGuiElement viewing, IGuiViewer previousViewer, final EntityPlayer player)
	{
		super(player);

		this.previousViewer = previousViewer;
		this.viewing = viewing;
	}

	public static void preventInnerTyping()
	{
		preventInnerTyping = true;
	}

	public static void fetchAllVisibleChildren(List<IGuiElement> allChildren, IGuiContext top)
	{
		if (top.getOwner().state().isVisible())
		{
			allChildren.addAll(top.getChildren());

			top.getChildren().forEach((element) -> fetchAllVisibleChildren(allChildren, element.context()));
		}
	}

	@Override
	public void setHoveredDescription(List<String> desc)
	{
		this.hoverDescription = desc;
	}

	@Override
	public void notifyGlobalContextChange()
	{
		this.contextChanged = true;
	}

	@Override
	public void notifyBuildingStarted(IGuiElement element)
	{
		if (!this.elementsCurrentlyBuilding.contains(element))
		{
			this.elementsCurrentlyBuilding.add(element);
		}
	}

	@Override
	public void notifyBuildingFinished(IGuiElement element)
	{
		this.elementsCurrentlyBuilding.remove(element);
	}

	@Override
	public IGuiViewer getPreviousViewer()
	{
		return this.previousViewer;
	}

	@Override
	public IGuiElement getViewing()
	{
		return this.viewing;
	}

	@Override
	public int getScreenWidth()
	{
		return this.width;
	}

	@Override
	public int getScreenHeight()
	{
		return this.height;
	}

	@Override
	public Minecraft mc()
	{
		return this.mc;
	}

	@Override
	public FontRenderer fontRenderer()
	{
		return this.fontRenderer;
	}

	@Override
	public void requestRecacheAndReorderAllVisibleElements()
	{
		this.recacheRequested = true;
	}

	@Override
	public List<IGuiElement> getAllVisibleElements()
	{
		return this.allVisibleElements;
	}

	@Override
	public Collection<IGuiElement> getAllVisibleElementsBelow(IGuiElement child)
	{
		if (!this.allVisibleElements.contains(child))
		{
			return Collections.emptyList();
		}

		if (!this.subListCache.containsKey(child))
		{
			int startIndex = this.allVisibleElements.indexOf(child);

			this.subListCache.put(child, this.allVisibleElements.subList(startIndex, this.allVisibleElements.size() - 1));
		}

		return this.subListCache.get(child);
	}

	@Override
	public Collection<IGuiElement> getAllVisibleElementsAbove(IGuiElement child)
	{
		if (!this.allParentsRecursive.contains(child))
		{
			return Collections.emptyList();
		}

		int startIndex = this.allParentsRecursive.indexOf(child);

		return this.allParentsRecursive.subList(startIndex, this.allParentsRecursive.size() - 1);
	}

	@Override
	public void pushActionPerformed(GuiButton button)
	{
		try
		{
			this.actionPerformed(button);
		}
		catch (IOException e)
		{
			OrbisAPI.LOGGER.info(e);
		}
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
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}

	@Override
	public void initGui()
	{
		if (Minecraft.getMinecraft().currentScreen == this)
		{
			super.initGui();
		}

		if (!this.viewing.state().hasBuilt())
		{
			this.viewing.build(this);

			this.build(this.viewing.context());

			this.initContainerSize();

			this.requestRecacheAndReorderAllVisibleElements();
		}
	}

	public abstract void build(IGuiContext context);

	public void initContainerSize()
	{

	}

	private void drawElement(IGuiElement element, boolean debugDimRendering)
	{
		IGuiState state = element.state();

		if (!state.isVisible())
		{
			return;
		}

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

		if (state.getShouldScaleRender())
		{
			GlStateManager.scale(state.dim().scale(), state.dim().scale(), 0);
		}

		GlStateManager.rotate(state.dim().degrees(), 0.0F, 0.0F, 1.0F);

		GlStateManager.translate(state.dim().isCenteredX() ? -(state.dim().width() / 2) : 0, state.dim().isCenteredY() ? -(state.dim().height() / 2) : 0, 0);

		GlStateManager.translate(-x - state.dim().originX(), -y - state.dim().originY(), 0);

		GuiFrameUtils.applyAlpha(state);

		for (IGuiEvent event : state.getEvents())
		{
			event.onPreDraw(element);
		}

		for (IGuiEvent event : state.getEvents())
		{
			event.onDraw(element);
		}

		for (IGuiEvent event : state.getEvents())
		{
			event.onPostDraw(element);
		}

		GlStateManager.popMatrix();
	}

	protected void drawElements()
	{
		for (IGuiElement element : this.allVisibleElements)
		{
			element.state().updateState();

			this.drawElement(element, false);
		}
	}

	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float partialTicks)
	{
		if (this.elementsCurrentlyBuilding.isEmpty() && this.recacheRequested)
		{
			this.subListCache.clear();
			this.allVisibleElements.clear();

			fetchAllVisibleChildren(this.allVisibleElements, this.viewing.context());

			this.allVisibleElements.sort(Comparator.comparingInt((element) -> element.state().getZOrder()));

			this.recacheRequested = false;
		}

		if (this.contextChanged)
		{
			this.allVisibleElements.forEach((element) -> element.state().getEvents().forEach((event) -> event.onGlobalContextChanged(element)));

			this.contextChanged = false;
		}

		preventInnerTyping = false;

		super.drawDefaultBackground();

		GlStateManager.pushMatrix();

		InputHelper.markHoveredAndTopElements(this, false);

		this.drawElements();

		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GlStateManager.disableAlpha();

		GlStateManager.popMatrix();

		super.drawScreen(mouseX, mouseY, partialTicks);

		if (this.hoverDescription != null && this.hoverDescription.size() > 0)
		{
			GuiUtils.drawHoveringText(this.hoverDescription, mouseX, mouseY, Minecraft.getMinecraft().fontRenderer);
		}

		this.hoverDescription = null;
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);

		this.allVisibleElements.forEach((element) ->
		{
			for (IGuiEvent event : element.state().getEvents())
			{
				if (!event.isMouseClickedEnabled(element, mouseX, mouseY, mouseButton))
				{
					return;
				}
			}

			element.state().getEvents().forEach((event) -> event.onMouseClicked(element, mouseX, mouseY, mouseButton));
		});
	}

	@Override
	protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick)
	{
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

		this.allVisibleElements.forEach((element) ->
		{
			for (IGuiEvent event : element.state().getEvents())
			{
				if (!event.isMouseClickMoveEnabled(element, mouseX, mouseY, clickedMouseButton, timeSinceLastClick))
				{
					return;
				}
			}

			element.state().getEvents().forEach((event) -> event.onMouseClickMove(element, mouseX, mouseY, clickedMouseButton, timeSinceLastClick));
		});
	}

	@Override
	protected void mouseReleased(final int mouseX, final int mouseY, final int state)
	{
		super.mouseReleased(mouseX, mouseY, state);

		this.allVisibleElements.forEach((element) ->
		{
			for (IGuiEvent event : element.state().getEvents())
			{
				if (!event.isMouseReleasedEnabled(element, mouseX, mouseY, state))
				{
					return;
				}
			}

			element.state().getEvents().forEach((event) -> event.onMouseReleased(element, mouseX, mouseY, state));
		});
	}

	@Override
	protected void handleMouseClick(final Slot slotIn, final int slotId, final int mouseButton, final ClickType type)
	{
		super.handleMouseClick(slotIn, slotId, mouseButton, type);

		this.allVisibleElements.forEach((element) ->
		{
			for (IGuiEvent event : element.state().getEvents())
			{
				if (!event.isHandleMouseClickEnabled(element, slotIn, slotId, mouseButton, type))
				{
					return;
				}
			}

			element.state().getEvents().forEach((event) -> event.onHandleMouseClick(element, slotIn, slotId, mouseButton, type));
		});
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		final int state = Mouse.getEventDWheel();

		if (state != 0)
		{
			this.onMouseWheel(state);
		}
	}

	protected void onMouseWheel(int state)
	{
		this.allVisibleElements.forEach((element) ->
		{
			for (IGuiEvent event : element.state().getEvents())
			{
				if (!event.isMouseWheelEnabled(element, state))
				{
					return;
				}
			}

			element.state().getEvents().forEach((event) -> event.onMouseWheel(element, state));
		});
	}

	protected void keyTypedInner(final char typedChar, final int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void keyTyped(final char typedChar, final int keyCode) throws IOException
	{
		if (!preventInnerTyping)
		{
			this.keyTypedInner(typedChar, keyCode);
		}

		this.allVisibleElements.forEach((element) ->
		{
			for (IGuiEvent event : element.state().getEvents())
			{
				if (!event.isKeyboardEnabled(element))
				{
					return;
				}
			}

			element.state().getEvents().forEach((event) -> event.onKeyTyped(element, typedChar, keyCode));
		});
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		this.allVisibleElements.forEach((element) ->
		{
			element.state().getEvents().forEach((event) -> event.onGuiClosed(element));
		});
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		this.allVisibleElements.forEach((element) -> element.state().getEvents().forEach((event) -> event.onUpdateScreen(element)));
	}

	@Override
	public void setWorldAndResolution(final Minecraft mc, final int width, final int height)
	{
		super.setWorldAndResolution(mc, width, height);
	}

	@Override
	protected void actionPerformed(final GuiButton button) throws IOException
	{
		super.actionPerformed(button);

		this.allVisibleElements.forEach((element) ->
		{
			element.state().getEvents().forEach((event) -> event.onActionPerformed(element, button));
		});
	}

	@Override
	public void onResize(final Minecraft mcIn, final int w, final int h)
	{
		super.onResize(mcIn, w, h);

		this.allVisibleElements.forEach((element) -> element.state().dim().refreshModifiedState());
	}
}