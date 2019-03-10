package com.gildedgames.orbis.lib.client.gui.util.vanilla;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiViewer;
import com.gildedgames.orbis.lib.client.rect.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class GuiItemStackRender extends GuiElement
{
	private ItemStack stack = ItemStack.EMPTY;

	public GuiItemStackRender(Rect rect)
	{
		super(rect, true);
	}

	public ItemStack getItemStack()
	{
		return this.stack;
	}

	public void setItemStack(@Nonnull ItemStack stack)
	{
		this.stack = stack;
	}

	@Override
	public void build()
	{
		this.state().setCanBeTopHoverElement(true);
	}

	@Override
	public void onDraw(GuiElement element)
	{
		GlStateManager.enableRescaleNormal();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableDepth();

		Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(this.stack, (int) this.dim().x() + 1, (int) this.dim().y() + 1);

		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, 0.0F, 300.0F);

		if (this.stack.getCount() > 1)
		{
			int xOffset = (Math.max(String.valueOf(this.stack.getCount()).length() - 1, 0)) * -6;

			this.viewer().getActualScreen().drawString(Minecraft.getMinecraft().fontRenderer, String.valueOf(this.stack.getCount()),
					(int) this.dim().x() + 12 + xOffset, (int) this.dim().y() + (int) this.dim().height() - 8, 0xFFFFFF);
		}

		if (this.state().isHoveredAndTopElement())
		{
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;

			if (gui instanceof IGuiViewer)
			{
				IGuiViewer viewer = (IGuiViewer) gui;

				viewer.setHoveredDescription(this.stack.getTooltip(Minecraft.getMinecraft().player,
						Minecraft.getMinecraft().gameSettings.advancedItemTooltips ?
								ITooltipFlag.TooltipFlags.ADVANCED :
								ITooltipFlag.TooltipFlags.NORMAL));
			}
		}

		GlStateManager.popMatrix();
	}
}
