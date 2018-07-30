package com.gildedgames.orbis_api.client.gui.util.decorators;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.client.gui.util.GuiTexture;
import com.gildedgames.orbis_api.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis_api.client.gui.util.gui_library.GuiLibHelper;
import com.gildedgames.orbis_api.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis_api.client.gui.util.gui_library.IGuiEvent;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.client.rect.Rect;
import com.gildedgames.orbis_api.client.rect.RectModifier;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiScrollable extends GuiElement
{
	private static final ResourceLocation SCROLL_KNOB = OrbisAPI.getResource("list/scroll_knob.png");

	private static final ResourceLocation SCROLL_KNOB_DISABLED = OrbisAPI.getResource("list/scroll_knob_disabled.png");

	private static final ResourceLocation SCROLL_BAR = OrbisAPI.getResource("list/scroll_bar.png");

	private IGuiElement window, pane;

	private float scroll;

	private IGuiElement decorated;

	private GuiTexture scrollKnob, scrollBar;

	private IGuiEvent<IGuiElement> scissorEvent = new IGuiEvent<IGuiElement>()
	{
		@Override
		public void onPreDraw(IGuiElement element)
		{
			ScaledResolution res = new ScaledResolution(GuiScrollable.this.viewer().mc());

			double scaleW = GuiScrollable.this.viewer().mc().displayWidth / res.getScaledWidth_double();
			double scaleH = GuiScrollable.this.viewer().mc().displayHeight / res.getScaledHeight_double();

			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			GL11.glScissor((int) ((GuiScrollable.this.window.dim().x()) * scaleW),
					(int) (GuiScrollable.this.viewer().mc().displayHeight - (
							(GuiScrollable.this.window.dim().y() + GuiScrollable.this.window.dim().height()) * scaleH)),
					(int) (GuiScrollable.this.window.dim().width() * scaleW), (int) (GuiScrollable.this.window.dim().height() * scaleH));
		}

		@Override
		public void onPostDraw(IGuiElement element)
		{
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}

		@Override
		public void onMouseClicked(IGuiElement element, final int mouseX, final int mouseY, final int mouseButton)
		{

		}

		@Override
		public boolean isMouseClickedEnabled(IGuiElement element, int mouseX, int mouseY, int mouseButton)
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
		public boolean isMouseClickMoveEnabled(IGuiElement element, final int mouseX, final int mouseY, final int clickedMouseButton,
				final long timeSinceLastClick)
		{
			boolean enabled = element == GuiScrollable.this.window || GuiScrollable.this.window.state().isHovered();

			if (!enabled)
			{
				for (IGuiEvent event : element.state().getEvents())
				{
					if (event instanceof IInputEnabledOutsideBounds)
					{
						IInputEnabledOutsideBounds input = (IInputEnabledOutsideBounds) event;

						input.onMouseClickMoveOutsideBounds(element, mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

						return false;
					}
				}
			}

			return enabled;
		}

		@Override
		public boolean isMouseReleasedEnabled(IGuiElement element, final int mouseX, final int mouseY, final int state)
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
		public boolean isMouseWheelEnabled(IGuiElement element, final int state)
		{
			boolean enabled = element == GuiScrollable.this.window || GuiScrollable.this.window.state().isHovered();

			if (!enabled)
			{
				for (IGuiEvent event : element.state().getEvents())
				{
					if (event instanceof IInputEnabledOutsideBounds)
					{
						IInputEnabledOutsideBounds input = (IInputEnabledOutsideBounds) event;

						input.onMouseWheelOutsideBounds(element, state);
					}
				}
			}

			return enabled;
		}

		@Override
		public boolean isHandleMouseClickEnabled(IGuiElement element, final Slot slotIn, final int slotId, final int mouseButton, final ClickType type)
		{
			boolean enabled = element == GuiScrollable.this.window || GuiScrollable.this.window.state().isHovered();

			if (!enabled)
			{
				for (IGuiEvent event : element.state().getEvents())
				{
					if (event instanceof IInputEnabledOutsideBounds)
					{
						IInputEnabledOutsideBounds input = (IInputEnabledOutsideBounds) event;

						input.onHandleMouseClickOutsideBounds(element, slotIn, slotId, mouseButton, type);

						return false;
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
	public void onGlobalContextChanged(GuiElement element)
	{
		for (IGuiElement child : GuiLibHelper.getAllChildrenRecursivelyFor(this))
		{
			child.state().addEvent(this.scissorEvent);
		}
	}

	@Override
	public void onDraw(GuiElement element)
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
	public void onMouseWheel(GuiElement element, final int state)
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
		default void onMouseClickedOutsideBounds(T element, final int mouseX, final int mouseY, final int mouseButton)
		{

		}

		default void onMouseClickMoveOutsideBounds(T element, final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick)
		{

		}

		default void onMouseReleasedOutsideBounds(T element, final int mouseX, final int mouseY, final int state)
		{

		}

		default void onMouseWheelOutsideBounds(T element, final int state)
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

	public static class InputEnabledOutsideBounds<T extends IGuiElement> implements IGuiEvent<T>, IInputEnabledOutsideBounds<T>
	{

	}
}
