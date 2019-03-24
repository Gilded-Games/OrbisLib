package com.gildedgames.orbis.lib.client.gui.util;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.rect.Dim2D;
import com.gildedgames.orbis.lib.util.mc.GlUtil;

public class GuiLine extends GuiElement
{
	private int r, g, b, alpha;

	private float thickness;

	private float x1, y1, x2, y2;

	public GuiLine(float x1, float y1, float x2, float y2, int r, int g, int b, int alpha, float thickness)
	{
		//TODO: Proper bounds for line from pos1 to pos2
		super(Dim2D.flush(), true);

		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		this.r = r;
		this.g = g;
		this.b = b;

		this.alpha = alpha;

		this.thickness = thickness;
	}

	@Override
	public void build()
	{

	}

	@Override
	public void onDraw(IGuiElement element, int mouseX, int mouseY, float partialTicks)
	{
		GlUtil.drawLine(this.dim().x() + this.x1, this.dim().y() + this.y1, this.dim().x() + this.x2, this.dim().y() + this.y2, this.r, this.g, this.b,
				this.alpha, this.thickness);
	}
}
