package com.gildedgames.orbis.lib.client.gui.util;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiState;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;

public class GuiFrameUtils
{

	public static void applyAlpha(IGuiState state)
	{
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager
				.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO,
						GlStateManager.DestFactor.ONE);

		GL11.glEnable(GL11.GL_ALPHA_TEST);

		GlStateManager.enableAlphaTest();

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, state.getAlpha());
	}

	public static int changeAlpha(int origColor, int userInputedAlpha)
	{
		// Anything lower seems to convert to an opaque color. Not sure why?
		userInputedAlpha = Math.max(4, userInputedAlpha);

		return (origColor & 0x00ffffff) | (userInputedAlpha << 24);
	}

}
