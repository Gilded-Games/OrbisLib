package com.gildedgames.orbis.lib.core.variables.displays;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.client.gui.util.GuiTexture;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.rect.Dim2D;
import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

public class GuiTickBox extends GuiElement
{
	private static final ResourceLocation TICK_BOX = OrbisLib.getResource("tick_box.png");

	private static final ResourceLocation TICK_BOX_PRESSED = OrbisLib.getResource("tick_box_pressed.png");

	private final ResourceLocation untickedTexture, tickedTexture;

	private boolean ticked;

	private GuiTexture pressed;

	private List<Consumer<Boolean>> listeners = Lists.newArrayList();

	public GuiTickBox(Pos2D pos, boolean ticked)
	{
		this(pos, ticked, TICK_BOX_PRESSED);
	}

	public GuiTickBox(Pos2D pos, boolean ticked, ResourceLocation tickedTexture)
	{
		this(pos, ticked, TICK_BOX, tickedTexture);
	}

	public GuiTickBox(Pos2D pos, boolean ticked, ResourceLocation untickedTexture, ResourceLocation tickedTexture)
	{
		super(Dim2D.build().width(14).height(14).pos(pos).flush(), true);

		this.untickedTexture = untickedTexture;
		this.tickedTexture = tickedTexture;

		this.setTicked(ticked);
	}

	public void setTickedTexture(ResourceLocation location)
	{
		this.pressed.setResourceLocation(location);
	}

	/**
	 * Boolean == value of whether or not this tick box is ticked.
	 * @param listener The object that listens for a tick box press.
	 */
	public void listenOnPress(Consumer<Boolean> listener)
	{
		if (!this.listeners.contains(listener))
		{
			this.listeners.add(listener);
		}
	}

	public boolean isTicked()
	{
		return this.ticked;
	}

	public void setTicked(boolean ticked)
	{
		this.ticked = ticked;
	}

	@Override
	public void onMouseClicked(IGuiElement element, final double mouseX, final double mouseY, final int mouseButton)
	{
		if (this.state().isHoveredAndTopElement() && mouseButton == 0)
		{
			this.ticked = !this.ticked;

			this.listeners.forEach((c) -> c.accept(this.ticked));
		}
	}

	@Override
	public void build()
	{
		GuiTexture box = new GuiTexture(Dim2D.build().width(14).height(14).flush(), this.untickedTexture);
		this.pressed = new GuiTexture(Dim2D.build().width(14).height(14).flush(), this.tickedTexture);

		this.context().addChildren(box, this.pressed);

		this.state().setCanBeTopHoverElement(true);
	}

	@Override
	public void onDraw(IGuiElement element, int mouseX, int mouseY, float partialTicks)
	{
		this.pressed.state().setVisible(this.ticked);
	}
}
