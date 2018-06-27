package com.gildedgames.orbis_api.core.variables.displays;

import com.gildedgames.orbis_api.client.gui.data.Text;
import com.gildedgames.orbis_api.client.gui.util.GuiFrame;
import com.gildedgames.orbis_api.client.gui.util.GuiText;
import com.gildedgames.orbis_api.client.gui.util.vanilla.GuiButtonVanilla;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.client.rect.Rect;
import com.gildedgames.orbis_api.core.variables.IGuiVar;
import com.gildedgames.orbis_api.util.InputHelper;
import com.google.common.collect.Maps;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class GuiVarDisplay extends GuiFrame
{
	private Collection<IGuiVar> variables;

	private Map<IGuiVar, GuiFrame> varToDisplay = Maps.newHashMap();

	private GuiButtonVanilla apply, reset;

	public GuiVarDisplay(Rect rect)
	{
		super(rect);
	}

	public void reset()
	{
		this.clearChildren();
		this.variables = Collections.emptyList();
	}

	public void display(Collection<IGuiVar> variables)
	{
		this.display(variables, null);
	}

	public void display(Collection<IGuiVar> variables, String displayTitle)
	{
		this.clearChildren();

		this.variables = variables;

		for (IGuiVar var : this.variables)
		{
			var.setParentDisplay(this);
		}

		int currentY = 0;

		if (displayTitle != null)
		{
			GuiText title = new GuiText(Dim2D.build().y(currentY).x(this.dim().width() / 2).centerX(true).flush(),
					new Text(new TextComponentString(displayTitle), 1.0F));

			currentY += 15;

			this.addChildren(title);
		}

		for (IGuiVar var : this.variables)
		{
			GuiText text = new GuiText(Dim2D.build().y(currentY).flush(), new Text(new TextComponentString(var.getName()), 1.0F));

			currentY += 13;

			GuiFrame display = var.createDisplay((int) this.dim().width());

			display.dim().mod().y(currentY).flush();

			currentY += display.dim().height() + 5;

			this.varToDisplay.put(var, display);

			this.addChildren(text, display);
		}

		currentY += 5;

		this.apply = new GuiButtonVanilla(Dim2D.build().width((this.dim().width() / 2) - 2).x(0).y(currentY).height(20).flush());
		this.reset = new GuiButtonVanilla(Dim2D.build().width((this.dim().width() / 2) - 2).x((this.dim().width() / 2) + 4).y(currentY).height(20).flush());

		this.apply.getInner().displayString = "Apply";
		this.reset.getInner().displayString = "Reset";

		currentY += this.apply.dim().height();

		this.dim().mod().height(currentY).flush();

		this.addChildren(this.apply, this.reset);
	}

	@Override
	public void init()
	{

	}

	@Override
	public void draw()
	{

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, final int mouseButton) throws IOException
	{
		if (!this.isInputEnabled())
		{
			return;
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);

		if (this.variables != null)
		{
			if (InputHelper.isHovered(this.apply) && mouseButton == 0)
			{
				this.updateVariableData();
			}

			if (InputHelper.isHovered(this.reset) && mouseButton == 0)
			{
				this.resetVariableData();
			}
		}
	}

	public void updateVariableData()
	{
		if (this.variables != null)
		{
			for (IGuiVar var : this.variables)
			{
				var.updateDataFromDisplay(this.varToDisplay.get(var));
			}
		}
	}

	public void resetVariableData()
	{
		if (this.variables != null)
		{
			for (IGuiVar var : this.variables)
			{
				var.resetDisplayFromData(this.varToDisplay.get(var));
			}
		}
	}
}
