package com.gildedgames.orbis.lib.client.gui.util.repeat_methods;

import com.gildedgames.orbis.lib.client.gui.util.GuiTextureRepeatable;

public class TextureRepeatUniformEdges implements ITextureRepeatMethod
{
	@Override
	public void draw(GuiTextureRepeatable element, TextureUV centerSpace, TextureUV topLeftCorner,
			TextureUV topRightCorner,
			TextureUV bottomLeftCorner, TextureUV bottomRightCorner, TextureUV topCenter,
			TextureUV bottomCenter, TextureUV leftCenter, TextureUV rightCenter)
	{
		int eleWidth = (int) element.dim().width();
		int eleHeight = (int) element.dim().height();

		GuiTextureRepeatable.drawModalRectWithCustomSizedTexture(element.dim().x(), element.dim().y(), topLeftCorner.getU(), topLeftCorner.getV(),
				topLeftCorner.getWidth(),
				Math.min(eleHeight - bottomCenter.getHeight(), topLeftCorner.getHeight()),
				element.getTextureWidth(), element.getTextureHeight());

		GuiTextureRepeatable.drawModalRectWithCustomSizedTexture(element.dim().maxX() - topRightCorner.getWidth(), element.dim().y(), topRightCorner.getU(),
				topRightCorner.getV(),
				topRightCorner.getWidth(), Math.min(eleHeight - bottomCenter.getHeight(), topRightCorner.getHeight()),
				element.getTextureWidth(), element.getTextureHeight());

		GuiTextureRepeatable
				.drawModalRectWithCustomSizedTexture(element.dim().x(), element.dim().maxY() - bottomLeftCorner.getHeight(), bottomLeftCorner.getU(),
						bottomLeftCorner.getV(), bottomLeftCorner.getWidth(), bottomLeftCorner.getHeight(),
						element.getTextureWidth(), element.getTextureHeight());

		GuiTextureRepeatable.drawModalRectWithCustomSizedTexture(element.dim().maxX() - bottomRightCorner.getWidth(),
				element.dim().maxY() - bottomRightCorner.getHeight(),
				bottomRightCorner.getU(), bottomRightCorner.getV(), bottomRightCorner.getWidth(), bottomRightCorner.getHeight(),
				element.getTextureWidth(), element.getTextureHeight());

		int maxX = Math.max(0, eleWidth - topRightCorner.getWidth());

		for (int x = topCenter.getU(); x < maxX; x += topCenter.getWidth())
		{
			int widthLeft = Math.max(0, maxX - x);

			GuiTextureRepeatable.drawModalRectWithCustomSizedTexture(element.dim().x() + x, element.dim().y(), topCenter.getU(),
					topCenter.getV(), Math.min(widthLeft, topCenter.getWidth()),
					Math.min(eleHeight - bottomCenter.getHeight(), topCenter.getHeight()),
					element.getTextureWidth(), element.getTextureHeight());
		}

		maxX = Math.max(0, eleWidth - bottomRightCorner.getWidth());

		for (int x = bottomCenter.getU(); x < maxX; x += bottomCenter.getWidth())
		{
			int widthLeft = Math.max(0, maxX - x);

			GuiTextureRepeatable
					.drawModalRectWithCustomSizedTexture(element.dim().x() + x, element.dim().maxY() - bottomCenter.getHeight(), bottomCenter.getU(),
							bottomCenter.getV(), Math.min(widthLeft, bottomCenter.getWidth()), bottomCenter.getHeight(),
							element.getTextureWidth(), element.getTextureHeight());
		}

		int maxY = eleHeight - bottomLeftCorner.getHeight();

		for (int y = leftCenter.getV(); y < maxY; y += leftCenter.getHeight())
		{
			int heightLeft = maxY - y;

			GuiTextureRepeatable.drawModalRectWithCustomSizedTexture(element.dim().x(), element.dim().y() + y, leftCenter.getU(),
					leftCenter.getV(), leftCenter.getWidth(),
					Math.min(eleHeight - bottomCenter.getHeight(), Math.min(heightLeft, leftCenter.getHeight())),
					element.getTextureWidth(), element.getTextureHeight());
		}

		maxY = eleHeight - bottomRightCorner.getHeight();

		for (int y = rightCenter.getV(); y < maxY; y += rightCenter.getHeight())
		{
			int heightLeft = maxY - y;

			GuiTextureRepeatable
					.drawModalRectWithCustomSizedTexture(element.dim().maxX() - rightCenter.getWidth(), element.dim().y() + y, rightCenter.getU(),
							rightCenter.getV(), rightCenter.getWidth(), Math.min(heightLeft, rightCenter.getHeight()),
							element.getTextureWidth(), element.getTextureHeight());
		}

		maxX = eleWidth - rightCenter.getWidth();
		maxY = eleHeight - bottomCenter.getHeight();

		for (int y = centerSpace.getU(); y < maxY; y += centerSpace.getHeight())
		{
			int heightLeft = maxY - y;

			for (int x = centerSpace.getU(); x < maxX; x += centerSpace.getWidth())
			{
				int widthLeft = maxX - x;

				GuiTextureRepeatable
						.drawModalRectWithCustomSizedTexture(element.dim().x() + x, element.dim().y() + y, centerSpace.getU(),
								centerSpace.getV(), Math.min(widthLeft, centerSpace.getWidth()), Math.min(heightLeft, centerSpace.getHeight()),
								element.getTextureWidth(), element.getTextureHeight());
			}
		}
	}
}
