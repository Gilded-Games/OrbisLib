package com.gildedgames.orbis_api.client.gui.util.gui_library;

import com.gildedgames.orbis_api.client.rect.ModDim2D;
import com.gildedgames.orbis_api.client.rect.Rect;

public class GuiElement implements IGuiElement, IGuiEvent<GuiElement>
{
	private IGuiContext context;

	private IGuiViewer viewer;

	private GuiState state = new GuiState(this);

	private boolean eventsEnabled;

	public GuiElement(Rect rect, boolean enableLocalEvents)
	{
		this.state().dim().set(rect);

		if (enableLocalEvents)
		{
			this.enableLocalEvents();
		}
	}

	public IGuiViewer viewer()
	{
		return this.viewer;
	}

	public void enableLocalEvents()
	{
		if (!this.eventsEnabled)
		{
			this.state().addEvent(this);

			this.eventsEnabled = true;
		}
	}

	@Override
	public IGuiContext context()
	{
		if (this.context == null)
		{
			throw new RuntimeException("Tried to access a GuiElement's context before it was being built! Make sure to call build() first.");
		}

		return this.context;
	}

	@Override
	public IGuiState state()
	{
		return this.state;
	}

	@Override
	public final void build(IGuiViewer viewer)
	{
		this.state.setViewer(viewer);

		this.state.setIsBuilding(true);

		this.viewer = viewer;
		this.context = new GuiContext(viewer, this);

		this.build();

		this.state.setIsBuilding(false);
		this.state.setHasBuilt(true);
	}

	public void build()
	{

	}

	@Override
	public ModDim2D dim()
	{
		return this.state.dim();
	}

	public void rebuild()
	{
		this.context().clearChildren();

		this.state.setIsBuilding(true);

		this.build(this.viewer);

		this.state.setIsBuilding(false);

		this.viewer.requestRecacheAndReorderAllVisibleElements();
	}
}