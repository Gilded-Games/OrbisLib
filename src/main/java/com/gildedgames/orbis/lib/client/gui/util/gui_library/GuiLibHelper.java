package com.gildedgames.orbis.lib.client.gui.util.gui_library;

import com.google.common.collect.Lists;

import java.util.List;

public class GuiLibHelper
{
	public static List<IGuiElement> getAllChildrenRecursivelyFor(IGuiElement element)
	{
		List<IGuiElement> children = Lists.newArrayList();

		// Prevents from adding the top element itself
		for (IGuiElement child : element.context().getChildren())
		{
			fetchAllChildrenRecursivelyFor(children, child);
		}

		return children;
	}

	private static void fetchAllChildrenRecursivelyFor(List<IGuiElement> addTo, IGuiElement top)
	{
		addTo.add(top);

		for (IGuiElement child : top.context().getChildren())
		{
			fetchAllChildrenRecursivelyFor(addTo, child);
		}
	}
}
