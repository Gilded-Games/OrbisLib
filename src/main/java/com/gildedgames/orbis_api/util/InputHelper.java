package com.gildedgames.orbis_api.util;

import com.gildedgames.orbis_api.client.gui.util.IGuiFrame;
import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.client.rect.Rect;
import com.gildedgames.orbis_api.client.rect.RectHolder;
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

	private static boolean isHovered(final List<IGuiFrame> frames, IGuiFrame ignore)
	{
		if (frames == null)
		{
			return false;
		}

		for (IGuiFrame frame : frames)
		{
			if (frame != ignore && isHovered(frame))
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

	public static boolean isHoveredAndTopElement(IGuiFrame check)
	{
		if (check == null)
		{
			return false;
		}

		if (Minecraft.getMinecraft().currentScreen instanceof IGuiFrame)
		{
			IGuiFrame frame = (IGuiFrame) Minecraft.getMinecraft().currentScreen;

			for (IGuiFrame child : frame.getAllChildrenSortedByZOrder())
			{
				if (child.isVisible() && child.isEnabled() && InputHelper.isHovered(child) && child.getZOrder() > check.getZOrder())
				{
					return false;
				}
			}
		}

		return isHovered(check.dim());
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
