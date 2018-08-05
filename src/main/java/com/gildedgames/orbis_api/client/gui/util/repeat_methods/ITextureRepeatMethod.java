package com.gildedgames.orbis_api.client.gui.util.repeat_methods;

import com.gildedgames.orbis_api.client.gui.util.GuiTextureRepeatable;

public interface ITextureRepeatMethod
{
	void draw(GuiTextureRepeatable element, TextureUV centerSpace, TextureUV topLeftCorner, TextureUV topRightCorner,
			TextureUV bottomLeftCorner,
			TextureUV bottomRightCorner,
			TextureUV topCenter, TextureUV bottomCenter, TextureUV leftCenter, TextureUV rightCenter);
}