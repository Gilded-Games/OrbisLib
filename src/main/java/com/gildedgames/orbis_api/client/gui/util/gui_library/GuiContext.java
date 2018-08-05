package com.gildedgames.orbis_api.client.gui.util.gui_library;

import com.gildedgames.orbis_api.client.rect.RectHolder;
import com.gildedgames.orbis_api.client.rect.RectModifier;
import com.google.common.collect.Lists;

import java.util.List;

public class GuiContext implements IGuiContext, IGuiStateListener
{
	private final List<IGuiElement> children = Lists.newArrayList();

	private final List<IGuiElement> parents = Lists.newArrayList();

	private IGuiElement owner;

	private IGuiViewer viewer;

	public GuiContext(IGuiViewer viewer, IGuiElement owner)
	{
		this.viewer = viewer;
		this.owner = owner;

		this.owner.state().listen(this);
	}

	@Override
	public IGuiElement getOwner()
	{
		return this.owner;
	}

	@Override
	public void addChildNoMods(final IGuiElement element)
	{
		this.addChildren(element, false);
	}

	@Override
	public void addChildren(final IGuiElement element)
	{
		this.addChildren(element, true);
	}

	@Override
	public void addChildren(final IGuiElement... elements)
	{
		for (IGuiElement element : elements)
		{
			this.addChildren(element, true);
		}
	}

	private void addChildren(final IGuiElement element, final boolean mods)
	{
		final RectHolder elementState = element.state();
		final RectHolder parentModifier = this.getOwner().state();

		if (mods)
		{
			if (elementState.dim().containsModifier("parent", parentModifier))
			{
				elementState.dim().removeModifiers("parent", parentModifier);
			}

			elementState.dim().add("parent", parentModifier, RectModifier.ModifierType.POS, RectModifier.ModifierType.SCALE);
		}

		IGuiState state = element.state();

		if (!state.hasBuilt())
		{
			element.build(this.viewer);
		}

		element.context().addParent(this.getOwner());

		this.children.add(element);

		this.viewer.notifyGlobalContextChange();
		this.viewer.requestRecacheAndReorderAllVisibleElements();
	}

	@Override
	public void removeChild(final IGuiElement element)
	{
		element.context().removeParent(this.getOwner());

		this.children.remove(element);

		this.viewer.notifyGlobalContextChange();
		this.viewer.requestRecacheAndReorderAllVisibleElements();
	}

	@Override
	public void clearChildren()
	{
		this.children.forEach((element) -> element.dim().clearRectModifiers());

		this.children.clear();
	}

	@Override
	public List<IGuiElement> getChildren()
	{
		return this.children;
	}

	@Override
	public void addParent(IGuiElement parent)
	{
		this.parents.add(parent);

		this.viewer.notifyGlobalContextChange();
		this.viewer.requestRecacheAndReorderAllVisibleElements();
	}

	@Override
	public boolean removeParent(IGuiElement parent)
	{
		boolean flag = this.parents.remove(parent);

		this.viewer.notifyGlobalContextChange();
		this.viewer.requestRecacheAndReorderAllVisibleElements();

		return flag;
	}

	@Override
	public List<IGuiElement> getParents()
	{
		return this.parents;
	}

	@Override
	public void onSetVisible(IGuiState state, boolean oldValue, boolean newValue)
	{
		if (oldValue != newValue)
		{
			this.viewer.requestRecacheAndReorderAllVisibleElements();
		}
	}

	@Override
	public void onSetZOrder(IGuiState state, int oldValue, int newValue)
	{
		if (oldValue != newValue)
		{
			this.viewer.requestRecacheAndReorderAllVisibleElements();
		}
	}
}
