package com.gildedgames.orbis.lib.client.gui.util.decorators;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.client.gui.util.GuiTexture;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiLibHelper;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiEvent;
import com.gildedgames.orbis.lib.client.rect.Dim2D;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.gildedgames.orbis.lib.client.rect.RectModifier;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiScrollable extends GuiElement
{
	private static final ResourceLocation SCROLL_KNOB = OrbisLib.getResource("list/scroll_knob.png");

	private static final ResourceLocation SCROLL_KNOB_DISABLED = OrbisLib.getResource("list/scroll_knob_disabled.png");

	private static final ResourceLocation SCROLL_BAR = OrbisLib.getResource("list/scroll_bar.png");

	private IGuiElement window, pane;

	private float scroll;

	private IGuiElement decorated;

	private GuiTexture scrollKnob, scrollBar;

	private IGuiEvent scissorEvent = new IGuiEvent()
	{
		@Override
		public void onPreDraw(IGuiElement element)
		{
			MainWindow window = Minecraft.getInstance().mainWindow;

			double scaleW = (double) window.getWidth() / window.getScaledWidth();
			double scaleH = (double) window.getHeight() / window.getScaledHeight();

			GL11.glEnable(GL11.GL_SCISSOR_TEST);

			if (!(GuiScrollable.this.window.dim().width() < 0 || GuiScrollable.this.window.dim().height() < 0))
			{
				GL11.glScissor((int) ((GuiScrollable.this.window.dim().x()) * scaleW),
						(int) (window.getFramebufferHeight() - (
								(GuiScrollable.this.window.dim().y() + GuiScrollable.this.window.dim().height()) * scaleH)),
						(int) (GuiScrollable.this.window.dim().width() * scaleW), (int) (GuiScrollable.this.window.dim().height() * scaleH));
			}
		}

		@Override
		public void onPostDraw(IGuiElement element)
		{
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}

		@Override
		public void onMouseClicked(IGuiElement element, final double mouseX, final double mouseY, final int mouseButton)
		{

		}

		@Override
		public boolean isMouseClickedEnabled(IGuiElement element, double mouseX, double mouseY, int mouseButton)
		{
			boolean enabled = element == GuiScrollable.this.window || GuiScrollable.this.window.state().isHovered();

			if (!enabled)
			{
				for (IGuiEvent event : element.state().getEvents())
				{
					if (event instanceof IInputEnabledOutsideBounds)
					{
						IInputEnabledOutsideBounds input = (IInputEnabledOutsideBounds) event;

						input.onMouseClickedOutsideBounds(element, mouseX, mouseY, mouseButton);

						return false;
					}
				}
			}

			return enabled;
		}

		@Override
		public boolean isMouseReleasedEnabled(IGuiElement element, final double mouseX, final double mouseY, final int state)
		{
			boolean enabled = element == GuiScrollable.this.window || GuiScrollable.this.window.state().isHovered();

			if (!enabled)
			{
				for (IGuiEvent event : element.state().getEvents())
				{
					if (event instanceof IInputEnabledOutsideBounds)
					{
						IInputEnabledOutsideBounds input = (IInputEnabledOutsideBounds) event;

						input.onMouseReleasedOutsideBounds(element, mouseX, mouseY, state);

						return false;
					}
				}
			}

			return enabled;
		}

		@Override
		public boolean isMouseWheelEnabled(IGuiElement element, final double scroll)
		{
			boolean enabled = element == GuiScrollable.this.window || GuiScrollable.this.window.state().isHovered();

			if (!enabled)
			{
				for (IGuiEvent event : element.state().getEvents())
				{
					if (event instanceof IInputEnabledOutsideBounds)
					{
						IInputEnabledOutsideBounds input = (IInputEnabledOutsideBounds) event;

						input.onMouseWheelOutsideBounds(element, scroll);
					}
				}
			}

			return enabled;
		}

		@Override
		public boolean canBeHovered(IGuiElement element)
		{
			for (IGuiEvent event : element.state().getEvents())
			{
				if (event instanceof IInputEnabledOutsideBounds)
				{
					if (((IInputEnabledOutsideBounds) event).shouldHoverOutsideBounds(element))
					{
						return true;
					}
				}
			}

			return element == GuiScrollable.this.window || GuiScrollable.this.window.state().isHovered();
		}
	};

	public GuiScrollable(IGuiElement decorated, Rect pane)
	{
		super(pane, true);

		this.dim().mod().x(decorated.dim().x()).y(decorated.dim().y()).flush();

		this.decorated = decorated;

		this.window = new GuiElement(Dim2D.build().width(0).x(0).y(0).flush(), false);
		this.pane = new GuiElement(Dim2D.build().x(16).y(0).flush(), false);

		this.window.dim().add("scrollableArea", this, RectModifier.ModifierType.AREA);
		this.pane.dim().add("scrollableArea", this, RectModifier.ModifierType.AREA);
	}

	@Override
	public void build()
	{
		this.decorated.dim().mod().x(0).y(0).flush();

		this.pane.build(this.viewer());

		this.pane.context().addChildren(this.decorated);

		this.scrollKnob = new GuiTexture(Dim2D.build().width(12).height(15).x(1).y(1).flush(), SCROLL_KNOB);
		this.scrollBar = new GuiTexture(Dim2D.build().width(14).flush(), SCROLL_BAR);

		this.scrollBar.dim().add("scrollableHeight", this, RectModifier.ModifierType.HEIGHT);

		this.context().addChildren(this.window, this.pane, this.scrollBar, this.scrollKnob);

		this.window.state().setCanBeTopHoverElement(true);
	}

	@Override
	public void onGlobalContextChanged(IGuiElement element)
	{
		for (IGuiElement child : GuiLibHelper.getAllChildrenRecursivelyFor(this))
		{
			child.state().addEvent(this.scissorEvent);
		}
	}

	@Override
	public void onDraw(IGuiElement element, int mouseX, int mouseY, float partialTicks)
	{
		if (this.dim().height() >= this.decorated.dim().height())
		{
			this.scrollKnob.setResourceLocation(SCROLL_KNOB_DISABLED);
		}
		else
		{
			this.scrollKnob.setResourceLocation(SCROLL_KNOB);
		}
	}

	@Override
	public void onMouseWheel(IGuiElement element, final double state)
	{
		if (this.window.state().isHoveredAndTopElement())
		{
			float prevScroll = this.scroll;

			this.scroll -= (float) (state / 120) * 10.0F;

			this.scroll = Math.max(0.0F, Math.min(this.decorated.dim().height() - this.dim().height(), this.scroll));

			this.pane.dim().mod().addY(prevScroll - this.scroll).flush();
			float height = this.decorated.dim().height() - this.dim().height();

			float percent = this.scroll <= 0.0F ? 0.0F : this.scroll / height;

			float y = Math.max(0.0F, (percent * this.dim().height()) - this.scrollKnob.dim().height());

			this.scrollKnob.dim().mod().y(Math.min(this.dim().height() - this.scrollKnob.dim().height() - 1, y + 1)).flush();
		}
	}

	public void resetScroll()
	{
		this.pane.dim().mod().y(0).flush();

		this.scrollKnob.dim().mod().y(1).flush();

		this.scroll = 0.0F;
	}

	public interface IInputEnabledOutsideBounds<T extends IGuiElement>
	{
		default void onMouseClickedOutsideBounds(T element, final double mouseX, final double mouseY, final int mouseButton)
		{

		}

		default void onMouseClickMoveOutsideBounds(T element, final double mouseX, final double mouseY, final int clickedMouseButton)
		{

		}

		default void onMouseReleasedOutsideBounds(T element, final double mouseX, final double mouseY, final int state)
		{

		}

		default void onMouseWheelOutsideBounds(T element, final double scroll)
		{

		}

		default void onHandleMouseClickOutsideBounds(T element, final Slot slotIn, final int slotId, final int mouseButton, final ClickType type)
		{

		}

		default boolean shouldHoverOutsideBounds(T element)
		{
			return false;
		}
	}

	public static class InputEnabledOutsideBounds<T extends IGuiElement> implements IGuiEvent, IInputEnabledOutsideBounds<T>
	{

	}
}
