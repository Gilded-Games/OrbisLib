package com.gildedgames.orbis.lib.client.gui.util.gui_library;

import com.gildedgames.orbis.lib.client.gui.util.GuiFrameUtils;
import com.gildedgames.orbis.lib.util.InputHelper;
import com.gildedgames.orbis.lib.util.mc.GuiUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

import java.util.*;

public abstract class GuiViewer<T extends Container> extends ContainerScreen<T> implements IGuiViewer
{
	private static boolean preventInnerTyping = false;

	private final List<IGuiElement> allParentsRecursive = Lists.newArrayList();

	private final IGuiViewer previousViewer;

	private List<IGuiElement> allVisibleElements = Lists.newArrayList();

	private boolean drawDefaultBackground = true;

	private IGuiElement viewing;

	private List<IGuiElement> elementsCurrentlyBuilding = Lists.newArrayList();

	private boolean recacheRequested, contextChanged;

	private Map<IGuiElement, List<IGuiElement>> subListCache = Maps.newHashMap();

	private List<ITextComponent> hoverDescription;

	public GuiViewer(IGuiElement viewing, IGuiViewer prevViewer, final T container, final PlayerEntity entity, final ITextComponent title)
	{
		super(container, entity.inventory, title);

		this.previousViewer = prevViewer;
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
	public void setHoveredDescription(List<ITextComponent> desc)
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
		return this.minecraft;
	}

	@Override
	public FontRenderer fontRenderer()
	{
		return this.font;
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
	public Screen getActualScreen()
	{
		return this;
	}

	public void setDrawDefaultBackground(final boolean flag)
	{
		this.drawDefaultBackground = flag;
	}

	@Override
	public void renderBackground()
	{

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
	{

	}

	@Override
	public void init()
	{
		if (Minecraft.getInstance().currentScreen == this)
		{
			super.init();
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
		//TODO: Dimensions are currnetly width and height of screen
		this.guiLeft = 0;
		this.guiTop = 0;

		this.xSize = this.width;
		this.ySize = this.height;
	}

	private void drawElement(IGuiElement element, int mouseX, int mouseY, float partialTicks)
	{
		IGuiState state = element.state();

		if (!state.isVisible())
		{
			return;
		}

		if (false)
		{
			AbstractGui.fill((int) state.dim().x(), (int) state.dim().y(), (int) state.dim().maxX(), (int) state.dim().maxY(), Integer.MAX_VALUE);
		}

		final float x = state.dim().x();
		final float y = state.dim().y();

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();

		GlStateManager.disableLighting();

		GlStateManager.translatef(x + state.dim().originX(), y + state.dim().originY(), 0);

		GlStateManager.translatef(state.dim().isCenteredX() ? (state.dim().width() / 2) : 0, state.dim().isCenteredY() ? (state.dim().height() / 2) : 0, 0);

		if (state.getShouldScaleRender())
		{
			GlStateManager.scalef(state.dim().scale(), state.dim().scale(), 0);
		}

		GlStateManager.rotatef(state.dim().degrees(), 0.0F, 0.0F, 1.0F);

		GlStateManager.translatef(state.dim().isCenteredX() ? -(state.dim().width() / 2) : 0, state.dim().isCenteredY() ? -(state.dim().height() / 2) : 0, 0);

		GlStateManager.translatef(-x - state.dim().originX(), -y - state.dim().originY(), 0);

		GuiFrameUtils.applyAlpha(state);

		for (IGuiEvent event : state.getEvents())
		{
			event.onPreDraw(element);
		}

		for (IGuiEvent event : state.getEvents())
		{
			event.onDraw(element, mouseX, mouseY, partialTicks);
		}

		for (IGuiEvent event : state.getEvents())
		{
			event.onPostDraw(element);
		}

		GlStateManager.popMatrix();
	}

	protected void drawElements(int mouseX, int mouseY, float partialTicks)
	{
		for (IGuiElement element : this.allVisibleElements)
		{
			element.state().updateState();

			this.drawElement(element, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public void render(final int mouseX, final int mouseY, final float partialTicks)
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

		if (this.drawDefaultBackground)
		{
			super.renderBackground();
		}

		GlStateManager.pushMatrix();

		InputHelper.markHoveredAndTopElements(this, false);

		this.drawElements(mouseX, mouseY, partialTicks);

		GlStateManager.popMatrix();

		super.render(mouseX, mouseY, partialTicks);

		if (this.hoverDescription != null && this.hoverDescription.size() > 0)
		{
			GuiUtils.drawHoveringText(this.hoverDescription, mouseX, mouseY, Minecraft.getInstance().fontRenderer);
		}

		this.hoverDescription = null;
	}

	@Override
	public boolean mouseClicked(final double mouseX, final double mouseY, final int mouseButton)
	{
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

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseReleased(final double mouseX, final double mouseY, final int state)
	{
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

		return super.mouseReleased(mouseX, mouseY, state);
	}

	// TODO: It's not clear what parameter is what. Research!
	@Override
	public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double amount)
	{
		this.allVisibleElements.forEach((element) ->
		{
			for (IGuiEvent event : element.state().getEvents())
			{
				if (!event.isMouseWheelEnabled(element, amount))
				{
					return;
				}
			}

			element.state().getEvents().forEach((event) -> event.onMouseWheel(element, amount));
		});

		return super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, amount);
	}

	@Override
	public boolean charTyped(final char typedChar, final int keyCode)
	{
		if (!preventInnerTyping)
		{
			super.charTyped(typedChar, keyCode);
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

		return super.charTyped(typedChar, keyCode);
	}

	@Override
	public void onClose()
	{
		super.onClose();

		this.allVisibleElements.forEach((element) ->
		{
			element.state().getEvents().forEach((event) -> event.onGuiClosed(element));
		});
	}

	@Override
	public void tick()
	{
		super.tick();

		this.allVisibleElements.forEach((element) -> element.state().getEvents().forEach((event) -> event.onTick(element)));
	}

	@Override
	public void resize(final Minecraft mcIn, final int w, final int h)
	{
		super.resize(mcIn, w, h);

		this.allVisibleElements.forEach((element) -> element.state().dim().refreshModifiedState());
	}
}