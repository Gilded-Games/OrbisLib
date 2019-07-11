package com.gildedgames.orbis.lib.core.variables.displays;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.client.gui.util.GuiTexture;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiViewer;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiContext;
import com.gildedgames.orbis.lib.client.gui.util.vanilla.GuiButtonVanilla;
import com.gildedgames.orbis.lib.client.gui.util.vanilla.GuiItemStackRender;
import com.gildedgames.orbis.lib.client.rect.Dim2D;
import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.util.InputHelper;
import com.gildedgames.orbis.lib.util.mc.ContainerGeneric;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class GuiItemStackChooserScreen extends GuiViewer<ContainerGeneric>
{
	private static final ResourceLocation STACK_INVENTORY = OrbisLib.getResource("stack_inventory.png");

	private GuiItemStackChooser chooser;

	private GuiButtonVanilla back;

	private List<ItemStackButton> buttons = Lists.newArrayList();

	public GuiItemStackChooserScreen(GuiViewer viewer, GuiItemStackChooser chooser, final PlayerEntity entity)
	{
		super(new GuiElement(Dim2D.flush(), false), viewer, new ContainerGeneric(), entity, new StringTextComponent("Item Stack Chooser"));

		this.chooser = chooser;

		this.setDrawDefaultBackground(true);
	}

	@Override
	public void build(IGuiContext context)
	{
		Pos2D center = InputHelper.getCenter();

		GuiTexture stack_inventory = new GuiTexture(Dim2D.build().center(true).pos(center).width(176).height(86).flush(), STACK_INVENTORY);

		context.addChildren(stack_inventory);

		int validIndex = 0;

		for (int i = 0; i < this.minecraft.player.inventory.mainInventory.size(); i++)
		{
			ItemStack stack = this.minecraft.player.inventory.mainInventory.get(i);

			if (this.chooser.getVarItemStack().getStackValidator() == null || this.chooser.getVarItemStack().getStackValidator().apply(stack))
			{
				ItemStackButton button = new ItemStackButton(Pos2D.flush(7 + (validIndex % 9) * 18, 7 + (validIndex / 9) * 18), stack, (button1) -> {
					this.chooser.setChosenStack(stack);

					this.minecraft.displayGuiScreen(this.getPreviousViewer().getActualScreen());
				});

				stack_inventory.context().addChildren(button);

				this.buttons.add(button);

				validIndex++;
			}
		}

		this.back = new GuiButtonVanilla(Dim2D.build().pos(center).center(true).addY(60).width(80).height(20).flush(),
				(button) -> this.minecraft.displayGuiScreen(this.getPreviousViewer().getActualScreen()));

		this.back.getInner().setMessage(I18n.format("orbis.gui.back"));

		context.addChildren(this.back);

		this.back.state().setCanBeTopHoverElement(true);
	}

	public class ItemStackButton extends GuiElement
	{
		private ItemStack stack;

		private Button.IPressable runnable;

		public ItemStackButton(Pos2D pos, ItemStack stack, Button.IPressable runnable)
		{
			super(Dim2D.build().width(20).height(20).pos(pos).flush(), false);

			this.stack = stack;
			this.runnable = runnable;
		}

		public ItemStack getStack()
		{
			return this.stack;
		}

		@Override
		public void build()
		{
			GuiButtonVanilla button = new GuiButtonVanilla(Dim2D.build().width(20).height(20).flush(), this.runnable);

			GuiItemStackRender stackRender = new GuiItemStackRender(Dim2D.flush());

			stackRender.setItemStack(this.stack);

			stackRender.dim().mod().width(20).height(20).x(1).y(0).flush();

			this.context().addChildren(button, stackRender);

			this.state().setCanBeTopHoverElement(true);
		}
	}
}
