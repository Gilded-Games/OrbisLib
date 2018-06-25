package com.gildedgames.orbis_api.client.gui.util;

import com.gildedgames.orbis_api.client.gui.data.IDropdownElement;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.util.InputHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GuiDropdownList extends GuiFrame
{
	private final List<IDropdownElement> elements = Lists.newArrayList();

	public GuiDropdownList(final Pos2D pos, final IDropdownElement... elements)
	{
		super(Dim2D.build().pos(pos).flush());

		this.elements.addAll(Arrays.asList(elements));
	}

	public void display(final Collection<IDropdownElement> elements, Pos2D pos)
	{
		this.setDropdownElements(elements);

		this.dim().mod().pos(pos).flush();

		this.setVisible(true);
		this.setEnabled(true);
	}

	public void setDropdownElements(final Collection<IDropdownElement> elements)
	{
		this.clearChildren();

		this.elements.clear();
		this.elements.addAll(elements);

		this.init();
	}

	public void addDropdownElements(Collection<IDropdownElement> elements)
	{
		this.clearChildren();

		this.elements.addAll(elements);

		this.init();
	}

	public void addDropdownElements(IDropdownElement... elements)
	{
		this.clearChildren();

		this.elements.addAll(Arrays.asList(elements));

		this.init();
	}

	@Override
	public void init()
	{
		for (int i = 0; i < this.elements.size(); i++)
		{
			final IDropdownElement element = this.elements.get(i);
			final int y = 17 * i;

			final int id = i;

			final GuiTextLabel label = new GuiTextLabel(Dim2D.build().y(y).area(60, 10).flush(), element.text())
			{
				@Override
				protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton)
				{
					super.mouseReleased(mouseX, mouseY, mouseButton);

					if (mouseButton == 0)
					{
						if (InputHelper.isHovered(this) && GuiDropdownList.this.isEnabled())
						{
							element.onClick(GuiDropdownList.this, Minecraft.getMinecraft().player);
						}

						if (id >= GuiDropdownList.this.elements.size() - 1)
						{
							GuiDropdownList.this.setVisible(false);
							GuiDropdownList.this.setEnabled(false);
						}
					}
				}
			};

			this.addChildren(label);
		}

		this.dim().mod().width(60).height(17 * this.elements.size()).flush();
	}
}
