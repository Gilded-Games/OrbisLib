package com.gildedgames.orbis.lib.client.gui.util;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.repeat_methods.ITextureRepeatMethod;
import com.gildedgames.orbis.lib.client.gui.util.repeat_methods.TextureUV;
import com.gildedgames.orbis.lib.client.rect.Rect;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiTextureRepeatable extends GuiElement
{
	private ResourceLocation texture;

	private int textureWidth, textureHeight;

	private TextureUV centerSpace, topLeftCorner, topRightCorner, bottomLeftCorner, bottomRightCorner, topCenter, bottomCenter, leftCenter, rightCenter;

	private ITextureRepeatMethod method;

	public GuiTextureRepeatable(final Rect rect, final ResourceLocation texture, TextureUV centerSpace, int textureWidth, int textureHeight,
			ITextureRepeatMethod method)
	{
		super(rect, true);

		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;

		this.method = method;

		this.createUVMaps(centerSpace, textureWidth, textureHeight);

		this.texture = texture;
		this.state().setShouldScaleRender(false);
	}

	public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width, float height, float textureWidth,
			float textureHeight)
	{
		x = Math.max(0, x);
		y = Math.max(0, y);
		u = Math.max(0, u);
		v = Math.max(0, v);
		width = Math.max(0, width);
		height = Math.max(0, height);

		if (width == 0 || height == 0)
		{
			return;
		}

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

	public TextureUV getCenterSpace()
	{
		return this.centerSpace;
	}

	public TextureUV getBottomRightCorner()
	{
		return this.bottomRightCorner;
	}

	public int getTextureWidth()
	{
		return this.textureWidth;
	}

	public int getTextureHeight()
	{
		return this.textureHeight;
	}

	private void createUVMaps(TextureUV centerSpace, int textureWidth, int textureHeight)
	{
		this.centerSpace = centerSpace;

		this.topLeftCorner = new TextureUV(0, 0, centerSpace.getU(), centerSpace.getV());

		this.leftCenter = new TextureUV(0, centerSpace.getV(), centerSpace.getU(), centerSpace.getHeight());
		this.bottomLeftCorner = new TextureUV(0, centerSpace.getMaxV(), centerSpace.getU(), textureHeight - centerSpace.getMaxV());

		this.topRightCorner = new TextureUV(centerSpace.getMaxU(), 0, textureWidth - centerSpace.getMaxU(), centerSpace.getV());

		this.rightCenter = new TextureUV(centerSpace.getMaxU(), centerSpace.getV(), textureWidth - centerSpace.getMaxU(), centerSpace.getHeight());
		this.bottomRightCorner = new TextureUV(centerSpace.getMaxU(), centerSpace.getMaxV(), textureWidth - centerSpace.getMaxU(),
				textureHeight - centerSpace.getMaxV());

		this.topCenter = new TextureUV(centerSpace.getU(), 0, centerSpace.getWidth(), centerSpace.getV());
		this.bottomCenter = new TextureUV(centerSpace.getU(), centerSpace.getMaxV(), centerSpace.getWidth(), textureHeight - centerSpace.getMaxV());
	}

	public ResourceLocation getResourceLocation()
	{
		return this.texture;
	}

	public void setResourceLocation(final ResourceLocation texture)
	{
		this.texture = texture;
	}

	public void setResourceLocation(final ResourceLocation texture, TextureUV centerSpace, int textureWidth, int textureHeight)
	{
		this.texture = texture;

		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;

		this.createUVMaps(centerSpace, textureWidth, textureHeight);
	}

	@Override
	public void onDraw(GuiElement element)
	{
		GlStateManager.pushMatrix();

		GuiFrameUtils.applyAlpha(this.state());

		this.viewer().mc().getTextureManager().bindTexture(this.texture);

		this.method.draw(this, this.centerSpace, this.topLeftCorner, this.topRightCorner, this.bottomLeftCorner, this.bottomRightCorner, this.topCenter,
				this.bottomCenter, this.leftCenter, this.rightCenter);

		GlStateManager.popMatrix();
	}

	@Override
	public GuiTextureRepeatable clone()
	{
		return new GuiTextureRepeatable(this.dim(), this.texture, this.centerSpace, this.textureWidth, this.textureHeight, this.method);
	}
}
