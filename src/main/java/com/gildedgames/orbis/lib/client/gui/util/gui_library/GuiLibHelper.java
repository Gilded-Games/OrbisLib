package com.gildedgames.orbis.lib.client.gui.util.gui_library;

import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.google.common.collect.Lists;

import java.util.List;

public class GuiLibHelper
{
	public static void alignVertically(final IGuiViewer viewer, final Pos2D origin, final float yPadding, final GuiElement... elements)
	{
		GuiElement lastElement = null;
		for (final GuiElement e : elements)
		{
			final float yOffset = lastElement != null ? lastElement.dim().maxY() + yPadding : origin.y();

			if (!e.state().hasBuilt())
			{
				e.build(viewer);
			}

			e.dim().mod().x(origin.x()).y(yOffset).flush();

			lastElement = e;
		}
	}

	/**
	 * Will set the min position and max positiot (width/height) based on children inside.
	 * @param topLevel
	 */
	public static void assembleMinMaxArea(final IGuiElement topLevel)
	{
		float maxX = Float.MIN_VALUE;
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxY = Float.MIN_VALUE;

		final List<IGuiElement> elements = Lists.newArrayList(topLevel.context().getChildren());
		elements.add(topLevel);

		for (final IGuiElement child : elements)
		{
			if (child.dim().maxX() > maxX)
			{
				maxX = child.dim().maxX();
			}

			if (child.dim().min().x() < minX)
			{
				minX = child.dim().min().x();
			}

			if (child.dim().maxY() > maxY)
			{
				maxY = child.dim().maxY();
			}

			if (child.dim().min().y() < minY)
			{
				minY = child.dim().min().y();
			}
		}

		topLevel.dim().mod().x(minX).y(minY).width(maxX - minX).height(maxY - minY).flush();
	}

	public static List<IGuiElement> getAllChildrenRecursivelyFor(final IGuiElement element)
	{
		final List<IGuiElement> children = Lists.newArrayList();

		// Prevents from adding the top element itself
		for (final IGuiElement child : element.context().getChildren())
		{
			fetchAllChildrenRecursivelyFor(children, child);
		}

		return children;
	}

	private static void fetchAllChildrenRecursivelyFor(final List<IGuiElement> addTo, final IGuiElement top)
	{
		addTo.add(top);

		for (final IGuiElement child : top.context().getChildren())
		{
			fetchAllChildrenRecursivelyFor(addTo, child);
		}
	}
}
