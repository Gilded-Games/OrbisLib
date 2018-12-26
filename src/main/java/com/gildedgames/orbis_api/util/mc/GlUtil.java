package com.gildedgames.orbis_api.util.mc;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GlUtil
{
	private final static BufferBuilder buffer = Tessellator.getInstance().getBuffer();

	public static void drawLine(double x1, double y1, double x2, double y2, int r, int g, int b, int alpha, float lineWidth)
	{
		GlStateManager.glLineWidth(lineWidth);

		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();

		buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);

		buffer.pos(x1, y1, 0).color(r, g, b, alpha).endVertex();
		buffer.pos(x2, y2, 0).color(r, g, b, alpha).endVertex();

		Tessellator.getInstance().draw();

		GlStateManager.enableTexture2D();

		GlStateManager.glLineWidth(1.0F);
	}

}
