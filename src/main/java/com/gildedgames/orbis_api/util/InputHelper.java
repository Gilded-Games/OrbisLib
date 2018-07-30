package com.gildedgames.orbis_api.util;

import com.gildedgames.orbis_api.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis_api.client.gui.util.gui_library.IGuiViewer;
import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.client.rect.Rect;
import com.gildedgames.orbis_api.client.rect.RectHolder;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.util.List;

public class InputHelper
{

	private static final Minecraft mc = Minecraft.getMinecraft();

	private static ScaledResolution resolution;

	public static int getMouseX()
	{
		return (Mouse.getEventX() * getScreenWidth() / mc.displayWidth);
	}

	public static int getMouseY()
	{
		return (getScreenHeight() - Mouse.getEventY() * getScreenHeight() / mc.displayHeight - 1);
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

	public static void refreshResolution()
	{
		resolution = new ScaledResolution(mc);
	}

	public static int getScreenWidth()
	{
		refreshResolution();

		return resolution.getScaledWidth();
	}

	public static int getScreenHeight()
	{
		refreshResolution();

		return resolution.getScaledHeight();
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

		if (Minecraft.getMinecraft().currentScreen instanceof IGuiViewer)
		{
			IGuiViewer viewer = (IGuiViewer) Minecraft.getMinecraft().currentScreen;

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

		for (IGuiElement element : viewer.getAllVisibleElements())
		{
			element.state().setHoveredAndTopElement(false);
			element.state().setHovered(isHovered(element));

			if (element.state().isHovered())
			{
				if (element.state().canBeTopHoverElement())
				{
					if (element.state().isEnabled() && (element.state().getZOrder() >= highestZOrder))
					{
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

	public static float getScaleFactor()
	{
		return resolution.getScaleFactor();
	}

	public static Pos2D getCenter()
	{
		return Pos2D.flush(InputHelper.getScreenWidth() / 2, InputHelper.getScreenHeight() / 2);
	}

	public static Pos2D getBottomCenter()
	{
		return InputHelper.getCenter().clone().addY(InputHelper.getScreenHeight() / 2).flush();
	}

	public static Pos2D getBottomRight()
	{
		return InputHelper.getBottomCenter().clone().addX(InputHelper.getScreenWidth() / 2).flush();
	}

	public static Pos2D getBottomLeft()
	{
		return InputHelper.getBottomRight().clone().addX(-InputHelper.getScreenWidth()).flush();
	}

	public static Pos2D getCenterLeft()
	{
		return InputHelper.getCenter().clone().addX(-InputHelper.getScreenWidth() / 2).flush();
	}

	public static Pos2D getCenterRight()
	{
		return InputHelper.getCenterLeft().clone().addX(InputHelper.getScreenWidth()).flush();
	}

	public static Pos2D getTopCenter()
	{
		return InputHelper.getCenter().clone().addY(-InputHelper.getScreenHeight() / 2).flush();
	}

	public static Pos2D getTopLeft()
	{
		return InputHelper.getTopCenter().clone().addX(-InputHelper.getScreenWidth() / 2).flush();
	}

	public static Pos2D getTopRight()
	{
		return InputHelper.getTopLeft().clone().addX(InputHelper.getScreenWidth()).flush();
	}

}
