package com.gildedgames.orbis.lib.client.gui.util;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.rect.Rect;
import net.minecraft.client.renderer.BufferBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiTexture extends GuiElement
{
	private ResourceLocation texture;

	public GuiTexture(final Rect rect, final ResourceLocation texture)
	{
		super(rect, true);

		this.texture = texture;
		this.state().setShouldScaleRender(false);
	}

	public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width, float height, float textureWidth,
			float textureHeight)
	{
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) x, (double) (y + height), 0.0D).tex((double) (u * f), (double) ((v + height) * f1)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0D).tex((double) ((u + width) * f), (double) ((v + height) * f1))
				.endVertex();
		bufferbuilder.pos((double) (x + width), (double) y, 0.0D).tex((double) ((u + width) * f), (double) (v * f1)).endVertex();
		bufferbuilder.pos((double) x, (double) y, 0.0D).tex((double) (u * f), (double) (v * f1)).endVertex();
		tessellator.draw();
	}

	public ResourceLocation getResourceLocation()
	{
		return this.texture;
	}

	public void setResourceLocation(final ResourceLocation texture)
	{
		this.texture = texture;
	}

	public void setResourceLocation(final ResourceLocation texture, float width, float height)
	{
		this.texture = texture;
		this.dim().mod().width(width).height(height).flush();
	}

	@Override
	public void onDraw(IGuiElement element, int mouseX, int mouseY, float partialTicks)
	{
		GlStateManager.pushMatrix();

		GuiFrameUtils.applyAlpha(this.state());

		this.viewer().mc().getTextureManager().bindTexture(this.texture);

		drawModalRectWithCustomSizedTexture(this.dim().x(), this.dim().y(), 0, 0, this.dim().width(),
				this.dim().height(),
				this.dim().width(), this.dim().height());

		GlStateManager.popMatrix();
	}

	@Override
	public GuiTexture clone()
	{
		return new GuiTexture(this.dim(), this.texture);
	}
}
