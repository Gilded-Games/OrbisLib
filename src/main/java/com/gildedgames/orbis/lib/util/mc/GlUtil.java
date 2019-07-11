package com.gildedgames.orbis.lib.util.mc;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GlUtil
{
	private final static BufferBuilder buffer = Tessellator.getInstance().getBuffer();

	public static void drawLine(double x1, double y1, double x2, double y2, int r, int g, int b, int alpha, float lineWidth)
	{
		GlStateManager.lineWidth(lineWidth);

		GlStateManager.disableLighting();
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();

		buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);

		buffer.pos(x1, y1, 0).color(r, g, b, alpha).endVertex();
		buffer.pos(x2, y2, 0).color(r, g, b, alpha).endVertex();

		Tessellator.getInstance().draw();

		GlStateManager.enableTexture();

		GlStateManager.lineWidth(1.0F);
	}

}
