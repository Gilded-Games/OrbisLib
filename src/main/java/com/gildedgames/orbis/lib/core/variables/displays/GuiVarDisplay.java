package com.gildedgames.orbis.lib.core.variables.displays;

import com.gildedgames.orbis.lib.client.gui.data.Text;
import com.gildedgames.orbis.lib.client.gui.util.GuiText;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.gui.util.vanilla.GuiButtonVanilla;
import com.gildedgames.orbis.lib.client.rect.Dim2D;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.gildedgames.orbis.lib.core.variables.IGuiVar;
import com.gildedgames.orbis.lib.core.variables.IGuiVarDisplayContents;
import com.google.common.collect.Maps;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuiVarDisplay extends GuiElement
{
	private Collection<IGuiVar> variables;

	private IGuiVarDisplayContents contents;

	private String displayTitle;

	private Map<IGuiVar, GuiElement> varToDisplay = Maps.newHashMap();

	private GuiButtonVanilla apply, reset;

	private int refreshRequests;

	public GuiVarDisplay(Rect rect)
	{
		super(rect, true);
	}

	public void reset()
	{
		this.context().clearChildren();
		this.variables = Collections.emptyList();
	}

	public void refresh()
	{
		this.refreshRequests++;
	}

	public void display(IGuiVarDisplayContents contents)
	{
		this.display(contents, null);
	}

	public void display(IGuiVarDisplayContents contents, String displayTitle)
	{
		if (contents == null)
		{
			return;
		}

		if (this.contents != null)
		{
			this.contents.setParentDisplay(null);
		}

		contents.setParentDisplay(this);

		this.contents = contents;
		this.displayTitle = displayTitle;

		this.context().clearChildren();

		this.variables = new CopyOnWriteArrayList<>(contents.getVariables());

		for (IGuiVar var : this.variables)
		{
			var.setParentDisplay(this);
		}

		int currentY = 0;

		if (this.displayTitle != null)
		{
			GuiText title = new GuiText(Dim2D.build().y(currentY).x(this.dim().width() / 2).centerX(true).flush(),
					new Text(new TranslationTextComponent(this.displayTitle), 1.0F));

			currentY += 15;

			this.context().addChildren(title);
		}

		for (IGuiVar var : this.variables)
		{
			GuiText text = new GuiText(Dim2D.build().y(currentY).flush(), new Text(new TranslationTextComponent(var.getVariableName()), 1.0F));

			currentY += 13;

			GuiElement display = var.createDisplay((int) this.dim().width());

			display.dim().mod().y(currentY).flush();

			currentY += display.dim().height() + 5;

			this.varToDisplay.put(var, display);

			this.context().addChildren(text, display);
		}

		currentY += 5;

		this.apply = new GuiButtonVanilla(Dim2D.build().width((this.dim().width() / 2) - 2).x(0).y(currentY).height(20).flush(), (button) -> this.updateVariableData());
		this.reset = new GuiButtonVanilla(Dim2D.build().width((this.dim().width() / 2) - 2).x((this.dim().width() / 2) + 4).y(currentY).height(20).flush(), (button) -> this.resetVariableData());

		this.apply.getInner().setMessage("Apply");
		this.reset.getInner().setMessage("Reset");

		currentY += this.apply.dim().height();

		this.dim().mod().height(currentY).flush();

		this.context().addChildren(this.apply, this.reset);

		this.apply.state().setCanBeTopHoverElement(true);
		this.reset.state().setCanBeTopHoverElement(true);
	}

	@Override
	public void onDraw(IGuiElement element, int mouseX, int mouseY, float partialTicks)
	{
		if (this.refreshRequests > 0)
		{
			this.display(this.contents, this.displayTitle);

			this.refreshRequests--;
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
