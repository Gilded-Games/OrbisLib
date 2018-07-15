package com.gildedgames.orbis_api.core.variables.displays;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.client.gui.util.GuiFrame;
import com.gildedgames.orbis_api.client.gui.util.GuiTexture;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.util.InputHelper;
import com.google.common.collect.Sets;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;

public class GuiTickBox extends GuiFrame
{
	private static final ResourceLocation TICK_BOX = OrbisAPI.getResource("tick_box.png");

	private static final ResourceLocation TICK_BOX_PRESSED = OrbisAPI.getResource("tick_box_pressed.png");

	private final ResourceLocation untickedTexture, tickedTexture;

	private boolean ticked;

	private GuiTexture pressed;

	private Set<Consumer<Boolean>> listeners = Sets.newHashSet();

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
		super(Dim2D.build().width(14).height(14).pos(pos).flush());

		this.untickedTexture = untickedTexture;
		this.tickedTexture = tickedTexture;

		this.setTicked(ticked);
	}

	/**
	 * Boolean == value of whether or not this tick box is ticked.
	 * @param listener The object that listens for a tick box press.
	 */
	public void listenOnPress(Consumer<Boolean> listener)
	{
		this.listeners.add(listener);
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
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if (InputHelper.isHoveredAndTopElement(this) && mouseButton == 0)
		{
			this.ticked = !this.ticked;

			this.listeners.forEach((c) -> c.accept(this.ticked));
		}
	}

	@Override
	public void init()
	{
		GuiTexture box = new GuiTexture(Dim2D.build().width(14).height(14).flush(), this.untickedTexture);
		this.pressed = new GuiTexture(Dim2D.build().width(14).height(14).flush(), this.tickedTexture);

		this.addChildren(box, this.pressed);
	}

	@Override
	public void draw()
	{
		this.pressed.setVisible(this.ticked);
	}
}
