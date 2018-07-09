package com.gildedgames.orbis_api.client.gui.util;

import com.gildedgames.orbis_api.client.gui.data.IDropdownElement;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.client.rect.Rect;
import com.gildedgames.orbis_api.client.rect.RectModifier;
import com.gildedgames.orbis_api.util.InputHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GuiDropdownList<ELEMENT extends IDropdownElement> extends GuiFrame
{
	private final List<ELEMENT> elements = Lists.newArrayList();

	private Set<IDropdownListListener<ELEMENT>> listeners = Sets.newHashSet();

	public GuiDropdownList(Rect rect, final ELEMENT... elements)
	{
		super(rect);

		this.elements.addAll(Arrays.asList(elements));
	}

	public void listen(IDropdownListListener<ELEMENT> listener)
	{
		this.listeners.add(listener);
	}

	public boolean unlisten(IDropdownListListener<ELEMENT> listener)
	{
		return this.listeners.remove(listener);
	}

	public List<ELEMENT> getElements()
	{
		return this.elements;
	}

	public void display(final Collection<ELEMENT> elements, Pos2D pos)
	{
		this.setDropdownElements(elements);

		this.dim().mod().pos(pos).flush();

		this.setVisible(true);
		this.setEnabled(true);
	}

	public void setDropdownElements(final Collection<ELEMENT> elements)
	{
		this.clearChildren();

		this.elements.clear();
		this.elements.addAll(elements);

		this.init();
	}

	public void addDropdownElements(Collection<ELEMENT> elements)
	{
		this.clearChildren();

		this.elements.addAll(elements);

		this.init();
	}

	public void addDropdownElements(ELEMENT... elements)
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
			final ELEMENT element = this.elements.get(i);
			final int y = 18 * i;

			final int id = i;

			final GuiTextLabel label = new GuiTextLabel(Dim2D.build().y(y).height(18).flush(), element.text())
			{
				@Override
				public void mouseClickedOutsideBounds(final int mouseX, final int mouseY, final int mouseButton)
				{
					super.mouseClickedOutsideBounds(mouseX, mouseY, mouseButton);

					try
					{
						this.mouseClicked(mouseX, mouseY, mouseButton);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}

				@Override
				protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException
				{
					super.mouseClicked(mouseX, mouseY, mouseButton);

					if (mouseButton == 0)
					{
						if (InputHelper.isHoveredAndTopElement(this) && GuiDropdownList.this.isEnabled())
						{
							element.onClick(GuiDropdownList.this, Minecraft.getMinecraft().player);

							GuiDropdownList.this.listeners.forEach((l) -> l.onClick(element));
						}

						if (id >= GuiDropdownList.this.elements.size() - 1)
						{
							GuiDropdownList.this.setVisible(false);
							GuiDropdownList.this.setEnabled(false);
						}
					}
				}
			};

			label.dim().add("dropdownListWidth", this, RectModifier.ModifierType.WIDTH);

			this.addChildren(label);
		}

		this.dim().mod().height(18 * this.elements.size()).flush();
	}
}
