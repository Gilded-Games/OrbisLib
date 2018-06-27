package com.gildedgames.orbis_api.client.gui.util.decorators;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.client.gui.util.GuiFrame;
import com.gildedgames.orbis_api.client.gui.util.GuiFrameDummy;
import com.gildedgames.orbis_api.client.gui.util.GuiTexture;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.client.rect.Rect;
import com.gildedgames.orbis_api.client.rect.RectModifier;
import com.gildedgames.orbis_api.util.InputHelper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiScrollable extends GuiFrame
{
	private static final ResourceLocation SCROLL_KNOB = OrbisAPI.getResource("list/scroll_knob.png");

	private static final ResourceLocation SCROLL_KNOB_DISABLED = OrbisAPI.getResource("list/scroll_knob_disabled.png");

	private static final ResourceLocation SCROLL_BAR = OrbisAPI.getResource("list/scroll_bar.png");

	private GuiFrameDummy window, pane;

	private float scroll;

	private GuiFrame decorated;

	private GuiTexture scrollKnob, scrollBar;

	public GuiScrollable(GuiFrame decorated, Rect pane)
	{
		super(pane);

		this.dim().mod().addWidth(16).x(decorated.dim().x()).y(decorated.dim().y()).flush();

		this.decorated = decorated;

		this.window = new GuiFrameDummy(Dim2D.build().width(16).x(0).y(0).flush());
		this.pane = new GuiFrameDummy(Dim2D.build().x(16).y(0).flush());

		this.window.dim().add(this, RectModifier.ModifierType.WIDTH, RectModifier.ModifierType.HEIGHT);
		this.pane.dim().add(this, RectModifier.ModifierType.WIDTH, RectModifier.ModifierType.HEIGHT);
	}

	@Override
	public void init()
	{
		this.decorated.dim().mod().x(0).y(0).flush();

		this.pane.addChildren(this.decorated);

		this.scrollKnob = new GuiTexture(Dim2D.build().width(12).height(15).x(1).y(1).flush(), SCROLL_KNOB);
		this.scrollBar = new GuiTexture(Dim2D.build().width(14).flush(), SCROLL_BAR);

		this.scrollBar.dim().add(this, RectModifier.ModifierType.HEIGHT);

		this.addChildren(this.window, this.pane, this.scrollBar, this.scrollKnob);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		if (this.isVisible())
		{
			if (this.dim().height() >= this.decorated.dim().height())
			{
				this.scrollKnob.setResourceLocation(SCROLL_KNOB_DISABLED);
			}
			else
			{
				this.scrollKnob.setResourceLocation(SCROLL_KNOB);
			}

			ScaledResolution res = new ScaledResolution(this.mc);

			double scaleW = this.mc.displayWidth / res.getScaledWidth_double();
			double scaleH = this.mc.displayHeight / res.getScaledHeight_double();

			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			GL11.glScissor((int) ((this.window.dim().x()) * scaleW),
					(int) (this.mc.displayHeight - ((this.window.dim().y() + this.window.dim().height()) * scaleH)),
					(int) (this.window.dim().width() * scaleW), (int) (this.window.dim().height() * scaleH));
		}

		super.drawScreen(mouseX, mouseY, partialTicks);

		if (this.isVisible())
		{
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
	}

	@Override
	public void onMouseWheel(final int state)
	{
		super.onMouseWheel(state);

		if (InputHelper.isHovered(this.window))
		{
			float prevScroll = this.scroll;

			this.scroll -= (float) (state / 120) * 10.0F;

			this.scroll = Math.max(0.0F, Math.min(this.decorated.dim().height() - this.dim().height(), this.scroll));

			this.pane.dim().mod().addY(prevScroll - this.scroll).flush();
			float height = this.decorated.dim().height() - this.dim().height();

			float percent = this.scroll <= 0.0F ? 0.0F : ((height) / this.scroll);

			float y = Math.max(0.0F, (percent * this.dim().height()) - this.scrollKnob.dim().height());

			this.scrollKnob.dim().mod().y(Math.min(this.dim().height() - this.scrollKnob.dim().height() - 1, y + 1)).flush();
		}
	}

	public void resetScroll()
	{
		this.pane.dim().mod().y(0).flush();

		this.scrollKnob.dim().mod().y(1).flush();

		this.scroll = 0.0F;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, final int mouseButton) throws IOException
	{
		if (!this.isInputEnabled() || !InputHelper.isHovered(this.window))
		{
			this.mouseClickedOutsideBounds(mouseX, mouseY, mouseButton);

			return;
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick)
	{
		if (!this.isInputEnabled() || !InputHelper.isHovered(this.window))
		{
			this.mouseClickMoveOutsideBounds(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

			return;
		}

		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}

	@Override
	protected void mouseReleased(final int mouseX, final int mouseY, final int state)
	{
		if (!this.isInputEnabled() || !InputHelper.isHovered(this.window))
		{
			this.mouseReleasedOutsideBounds(mouseX, mouseY, state);

			return;
		}

		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void handleMouseClick(final Slot slotIn, final int slotId, final int mouseButton, final ClickType type)
	{
		if (!this.isInputEnabled() || !InputHelper.isHovered(this.window))
		{
			return;
		}

		super.handleMouseClick(slotIn, slotId, mouseButton, type);
	}
}
