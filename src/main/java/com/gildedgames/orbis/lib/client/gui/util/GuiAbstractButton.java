package com.gildedgames.orbis.lib.client.gui.util;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.google.common.collect.Lists;

import java.util.List;

public class GuiAbstractButton extends GuiElement
{

	protected final GuiTexture defaultState, hoveredState, clickedState, disabledState;

	private final List<Runnable> onClickEvents = Lists.newArrayList();

	private boolean selected;

	public GuiAbstractButton(final Rect dim, final GuiTexture texture)
	{
		this(dim, texture, texture.clone(), texture.clone());
	}

	public GuiAbstractButton(final Rect dim, final GuiTexture defaultState, final GuiTexture hoveredState, final GuiTexture clickedState)
	{
		this(dim, defaultState, hoveredState, clickedState, null);
	}

	public GuiAbstractButton(final Rect dim, final GuiTexture defaultState, final GuiTexture hoveredState, final GuiTexture clickedState,
			final GuiTexture disabledState)
	{
		super(dim, true);

		this.defaultState = defaultState;
		this.hoveredState = hoveredState;
		this.clickedState = clickedState;

		this.disabledState = disabledState;
	}

	public void setSelected(boolean selected)
	{
		this.selected = selected;

		if (selected)
		{
			this.defaultState.state().setVisible(false);
			this.hoveredState.state().setVisible(true);
		}
	}

	public void addClickEvent(final Runnable event)
	{
		this.onClickEvents.add(event);
	}

	@Override
	public void onMouseClicked(GuiElement element, final int mouseX, final int mouseY, final int mouseButton)
	{
		if (this.state().isEnabled() && this.clickedState.state().isHoveredAndTopElement() && mouseButton == 0)
		{
			this.clickedState.state().setVisible(true);

			this.onClickEvents.forEach(Runnable::run);
		}
	}

	@Override
	public void onMouseReleased(GuiElement element, final int mouseX, final int mouseY, final int state)
	{
		this.clickedState.state().setVisible(false);
	}

	@Override
	public void build()
	{
		this.defaultState.state().setVisible(false);
		this.hoveredState.state().setVisible(false);
		this.clickedState.state().setVisible(false);

		this.defaultState.dim().mod().center(false).resetPos().flush();
		this.hoveredState.dim().mod().center(false).resetPos().flush();
		this.clickedState.dim().mod().center(false).resetPos().flush();

		this.context().addChildren(this.clickedState, this.defaultState, this.hoveredState);

		this.clickedState.state().setCanBeTopHoverElement(true);

		if (this.disabledState != null)
		{
			this.disabledState.dim().mod().center(false).resetPos().flush();

			if (!this.state().isEnabled())
			{
				this.disabledState.state().setVisible(true);
			}
			else
			{
				this.disabledState.state().setVisible(false);
			}

			this.context().addChildren(this.disabledState);
		}

		this.defaultState.state().setVisible(true);

		this.state().setCanBeTopHoverElement(true);
	}

	@Override
	public void onHoverEnter(GuiElement element)
	{
		this.defaultState.state().setVisible(false);
		this.hoveredState.state().setVisible(true);
	}

	@Override
	public void onHoverExit(GuiElement element)
	{
		if (!this.selected)
		{
			this.defaultState.state().setVisible(true);
			this.hoveredState.state().setVisible(false);
		}
	}

	@Override
	public void onDraw(GuiElement element)
	{
		if (this.disabledState != null)
		{
			if (!this.state().isEnabled())
			{
				this.disabledState.state().setVisible(true);
			}
			else
			{
				this.disabledState.state().setVisible(false);
			}
		}
	}

}