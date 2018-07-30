package com.gildedgames.orbis_api.client.gui.util.gui_library;

import com.gildedgames.orbis_api.client.rect.ModDim2D;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class GuiState implements IGuiState
{
	private boolean hasBuilt, building, canBeTopHoverElement = false;

	private boolean shouldScaleRender = true;

	private boolean hoveredAndTopElement = false;

	private boolean enabled = true, visible = true, hovered = false, hoverEntered = false, inputDisabledWhenNotHovered = false;

	private float alpha = 1.0F;

	private int zOrder;

	private List<IGuiEvent> events = Collections.emptyList();

	private List<IGuiStateListener> listeners = Collections.emptyList();

	private ModDim2D dim = new ModDim2D();

	private IGuiElement owner;

	private IGuiViewer viewer;

	public GuiState(IGuiElement ownerContext)
	{
		this.owner = ownerContext;
	}

	public void setViewer(IGuiViewer viewer)
	{
		this.viewer = viewer;
	}

	@Override
	public boolean isInputEnabled()
	{
		return this.isEnabled() && (!this.inputDisabledWhenNotHovered || this.hoveredAndTopElement);
	}

	@Override
	public boolean getShouldScaleRender()
	{
		return this.shouldScaleRender;
	}

	@Override
	public void setShouldScaleRender(boolean flag)
	{
		this.shouldScaleRender = flag;
	}

	@Override
	public boolean isInputDisabledWhenNotHovered()
	{
		return this.inputDisabledWhenNotHovered;
	}

	@Override
	public void setInputDisabledWhenNotHovered(boolean flag)
	{
		this.inputDisabledWhenNotHovered = flag;
	}

	@Override
	public ModDim2D dim()
	{
		return this.dim;
	}

	@Override
	public boolean isVisible()
	{
		return this.visible;
	}

	@Override
	public void setVisible(boolean flag)
	{
		boolean oldValue = this.visible;

		this.visible = flag;

		this.listeners.forEach((listener) -> listener.onSetVisible(this, oldValue, this.visible));
	}

	@Override
	public boolean isEnabled()
	{
		return this.enabled;
	}

	@Override
	public void setEnabled(boolean flag)
	{
		this.enabled = flag;
	}

	@Override
	public boolean isHoveredAndTopElement()
	{
		return this.hoveredAndTopElement;
	}

	@Override
	public void setHoveredAndTopElement(boolean flag)
	{
		this.hoveredAndTopElement = flag;
	}

	@Override
	public boolean isHovered()
	{
		return this.hovered;
	}

	@Override
	public void setHovered(boolean flag)
	{
		this.hovered = flag;
	}

	@Override
	public void setCanBeTopHoverElement(boolean flag)
	{
		this.canBeTopHoverElement = flag;
	}

	@Override
	public boolean canBeTopHoverElement()
	{
		return this.canBeTopHoverElement;
	}

	@Override
	public void updateState()
	{
		if (this.hovered)
		{
			if (!this.hoverEntered)
			{
				this.getEvents().forEach((event) -> event.onHoverEnter(this.owner));

				this.hoverEntered = true;
			}

			this.getEvents().forEach((event) -> event.onHovered(this.owner));
		}
		else if (this.hoverEntered)
		{
			this.getEvents().forEach((event) -> event.onHoverExit(this.owner));

			this.hoverEntered = false;
		}
	}

	@Override
	public void listen(IGuiStateListener listener)
	{
		if (this.listeners.isEmpty())
		{
			this.listeners = Lists.newArrayList();
		}

		if (!this.listeners.contains(listener))
		{
			this.listeners.add(listener);
		}
	}

	@Override
	public boolean unlisten(IGuiStateListener listener)
	{
		return this.listeners.remove(listener);
	}

	@Override
	public boolean hasBuilt()
	{
		return this.hasBuilt;
	}

	@Override
	public boolean isBuilding()
	{
		return this.building;
	}

	public void setHasBuilt(boolean flag)
	{
		this.hasBuilt = flag;
	}

	public void setIsBuilding(boolean flag)
	{
		this.building = flag;

		if (this.building)
		{
			this.viewer.notifyBuildingStarted(this.owner);
		}
		else
		{
			this.viewer.notifyBuildingFinished(this.owner);
		}
	}

	@Override
	public List<IGuiEvent> getEvents()
	{
		return this.events;
	}

	@Override
	public void addEvent(IGuiEvent event)
	{
		if (this.events.isEmpty())
		{
			this.events = Lists.newArrayList();
		}

		if (!this.events.contains(event))
		{
			this.events.add(event);
		}
	}

	@Override
	public boolean removeEvent(IGuiEvent event)
	{
		return this.events.remove(event);
	}

	@Override
	public float getAlpha()
	{
		return this.alpha;
	}

	@Override
	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
	}

	@Override
	public int getZOrder()
	{
		return this.zOrder;
	}

	@Override
	public void setZOrder(int zOrder)
	{
		int oldValue = this.zOrder;

		int zOrderDif = this.owner.context().getParents().stream().mapToInt((element) -> element.state().getZOrder()).sum();

		this.zOrder = zOrder + zOrderDif;

		this.listeners.forEach((listener) -> listener.onSetZOrder(this, oldValue, this.zOrder));
	}
}
