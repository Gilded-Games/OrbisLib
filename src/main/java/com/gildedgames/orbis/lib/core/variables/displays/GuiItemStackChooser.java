package com.gildedgames.orbis.lib.core.variables.displays;

import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiViewer;
import com.gildedgames.orbis.lib.client.gui.util.vanilla.GuiButtonVanilla;
import com.gildedgames.orbis.lib.client.gui.util.vanilla.GuiItemStackRender;
import com.gildedgames.orbis.lib.client.rect.Dim2D;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.gildedgames.orbis.lib.client.rect.RectModifier;
import com.gildedgames.orbis.lib.core.variables.GuiVarItemStack;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class GuiItemStackChooser extends GuiElement
{
	private GuiVarItemStack varItemStack;

	private ItemStack stack;

	private GuiButtonVanilla button;

	private GuiItemStackRender stackRender;

	public GuiItemStackChooser(Rect rect, GuiVarItemStack varItemStack)
	{
		super(rect, true);

		this.varItemStack = varItemStack;

		this.stack = varItemStack.getData();

		this.stackRender = new GuiItemStackRender(Dim2D.flush());
		this.stackRender.setItemStack(this.stack);
	}

	public GuiVarItemStack getVarItemStack()
	{
		return this.varItemStack;
	}

	public ItemStack getChosenStack()
	{
		return this.stack;
	}

	public void setChosenStack(ItemStack stack)
	{
		this.stack = stack;

		this.stackRender.setItemStack(this.stack);
	}

	@Override
	public void build()
	{
		this.button = new GuiButtonVanilla(Dim2D.build().flush());

		this.button.getInner().displayString = I18n.format("orbis.gui.choose_itemstack");

		if (!this.button.dim().containsModifier("area"))
		{
			this.button.dim().add(new RectModifier("area", this, RectModifier.ModifierType.AREA.getModification(), RectModifier.ModifierType.AREA));
		}

		this.stackRender.dim().mod().width(20).height(20).x(1).y(0).flush();

		this.context().addChildren(this.button, this.stackRender);

		this.button.state().setCanBeTopHoverElement(true);
	}

	@Override
	public void onMouseClicked(GuiElement element, final int mouseX, final int mouseY, final int mouseButton)
	{
		if (this.button.state().isHoveredAndTopElement() && mouseButton == 0 && this.viewer().mc().currentScreen instanceof GuiViewer)
		{
			this.viewer().mc().displayGuiScreen(new GuiItemStackChooserScreen((GuiViewer) this.viewer().mc().currentScreen, this));
		}
	}
}
