package com.gildedgames.orbis.lib.util;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiEvent;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiViewer;
import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.gildedgames.orbis.lib.client.rect.RectHolder;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;

import java.util.List;

public class InputHelper
{

	private static final Minecraft mc = Minecraft.getInstance();

	public static double getMouseX()
	{
		return (mc.mouseHelper.getMouseX() * getScreenWidth() / mc.mainWindow.getWidth());
	}

	public static double getMouseY()
	{
		return (getScreenHeight() - mc.mouseHelper.getMouseY() * getScreenHeight() / mc.mainWindow.getHeight() - 1);
	}

	private static boolean isHovered(final Rect dim)
	{
		if (dim == null)
		{
			return false;
		}

		return getMouseX() >= dim.x() && getMouseY() >= dim.y() && getMouseX() < dim.x() + dim.width() && getMouseY() < dim.y() + dim
				.height();
	}

	public static int getScreenWidth()
	{
		return mc.mainWindow.getScaledWidth();
	}

	public static int getScreenHeight()
	{
		return mc.mainWindow.getScaledHeight();
	}

	private static boolean isHovered(final List<IGuiElement> elements, IGuiElement ignore)
	{
		if (elements == null)
		{
			return false;
		}

		for (IGuiElement element : elements)
		{
			if (element != ignore && isHovered(element.state()))
			{
				return true;
			}
		}

		return false;
	}

	public static boolean isHovered(final RectHolder holder)
	{
		if (holder == null)
		{
			return false;
		}

		return isHovered(holder.dim());
	}

	private static boolean isHoveredAndTopElement(IGuiElement check)
	{
		return isHoveredAndTopElement(check, true);
	}

	private static boolean isHoveredAndTopElement(IGuiElement check, boolean topElementsCanBeChildren)
	{
		if (check == null)
		{
			return false;
		}

		if (Minecraft.getInstance().currentScreen instanceof IGuiViewer)
		{
			IGuiViewer viewer = (IGuiViewer) Minecraft.getInstance().currentScreen;

			for (IGuiElement child : viewer.getAllVisibleElements())
			{
				return false;
			}
		}

		return isHovered(check.state().dim());
	}

	private static boolean isViable(IGuiViewer viewer, IGuiElement element, boolean topElementsCanBeChildren)
	{
		return topElementsCanBeChildren || !viewer.getAllVisibleElementsBelow(element).contains(element);
	}

	public static void markHoveredAndTopElements(IGuiViewer viewer, boolean topElementsCanBeChildren)
	{
		int highestZOrder = Integer.MIN_VALUE;
		List<IGuiElement> topHovered = Lists.newArrayList();

		outer:
		for (IGuiElement element : viewer.getAllVisibleElements())
		{
			element.state().setHoveredAndTopElement(false);

			for (IGuiEvent event : element.state().getEvents())
			{
				if (!event.canBeHovered(element))
				{
					element.state().setHovered(false);

					continue outer;
				}
			}

			element.state().setHovered(isHovered(element));

			if (element.state().isHovered())
			{
				if (element.state().canBeTopHoverElement())
				{
					if (element.state().isEnabled())
					{
						if (element.state().getZOrder() > highestZOrder)
						{
							topHovered.clear();
						}

						topHovered.add(element);
						highestZOrder = element.state().getZOrder();
					}
				}
			}
		}

		for (IGuiElement element : topHovered)
		{
			recursiveSetHoverAndParents(element);
		}
	}

	private static void recursiveSetHoverAndParents(IGuiElement element)
	{
		element.state().setHoveredAndTopElement(true);

		for (IGuiElement parent : element.context().getParents())
		{
			if (parent.state().canBeTopHoverElement())
			{
				recursiveSetHoverAndParents(parent);
			}
		}
	}

	public static double getScaleFactor()
	{
		return mc.mainWindow.getGuiScaleFactor();
	}

	public static Pos2D getCenter()
	{
		return Pos2D.flush(InputHelper.getScreenWidth() / 2.0f, InputHelper.getScreenHeight() / 2.0f);
	}

	public static Pos2D getBottomCenter()
	{
		return InputHelper.getCenter().clone().addY(InputHelper.getScreenHeight() / 2.0f).flush();
	}

	public static Pos2D getBottomRight()
	{
		return InputHelper.getBottomCenter().clone().addX(InputHelper.getScreenWidth() / 2.0f).flush();
	}

	public static Pos2D getBottomLeft()
	{
		return InputHelper.getBottomRight().clone().addX(-InputHelper.getScreenWidth()).flush();
	}

	public static Pos2D getCenterLeft()
	{
		return InputHelper.getCenter().clone().addX(-InputHelper.getScreenWidth() / 2.0f).flush();
	}

	public static Pos2D getCenterRight()
	{
		return InputHelper.getCenterLeft().clone().addX(InputHelper.getScreenWidth()).flush();
	}

	public static Pos2D getTopCenter()
	{
		return InputHelper.getCenter().clone().addY(-InputHelper.getScreenHeight() / 2.0f).flush();
	}

	public static Pos2D getTopLeft()
	{
		return InputHelper.getTopCenter().clone().addX(-InputHelper.getScreenWidth() / 2.0f).flush();
	}

	public static Pos2D getTopRight()
	{
		return InputHelper.getTopLeft().clone().addX(InputHelper.getScreenWidth()).flush();
	}

}
