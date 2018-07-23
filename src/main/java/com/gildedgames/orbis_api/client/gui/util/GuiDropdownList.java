package com.gildedgames.orbis_api.client.gui.util;

import com.gildedgames.orbis_api.client.gui.data.IDropdownElement;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.client.rect.Rect;
import com.gildedgames.orbis_api.client.rect.RectModifier;
import com.gildedgames.orbis_api.util.InputHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.util.*;

public class GuiDropdownList<ELEMENT extends IDropdownElement> extends GuiFrame
{
	private final List<ELEMENT> elements = Lists.newArrayList();

	private Set<IDropdownListListener<ELEMENT>> listeners = Sets.newHashSet();

	private Map<ELEMENT, GuiDropdownList> subLists = Maps.newHashMap();

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

		int largestWidth = 0;

		for (ELEMENT element : elements)
		{
			int width = this.fontRenderer.getStringWidth(element.text().getFormattedText());

			if (width > largestWidth)
			{
				largestWidth = width;
			}
		}

		this.dim().mod().width(largestWidth + 10).pos(pos).flush();

		this.setVisible(true);
		this.setEnabled(true);
	}

	public void setDropdownElements(final Collection<ELEMENT> elements)
	{
		this.clearChildren();

		this.elements.clear();
		this.elements.addAll(elements);

		this.init();

		for (ELEMENT element : elements)
		{
			if (element.getSubElements() != null)
			{
				GuiDropdownList<?> list = element.getSubElements().get();

				int largestWidth = 0;

				for (IDropdownElement e : list.getElements())
				{
					int width = this.fontRenderer.getStringWidth(e.text().getFormattedText());

					if (width > largestWidth)
					{
						largestWidth = width;
					}
				}

				list.dim().mod().width(largestWidth + 10).flush();

				list.setVisible(false);
				list.setEnabled(false);

				this.addChildren(list);

				this.subLists.put(element, list);
			}
		}
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
	public void draw()
	{

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
				public void onHoverEnter()
				{
					if (GuiDropdownList.this.subLists.containsKey(element))
					{
						GuiDropdownList list = GuiDropdownList.this.subLists.get(element);

						list.dim().mod().x(GuiDropdownList.this.dim().width()).y(y).flush();

						if (list.dim().maxX() >= GuiDropdownList.this.width)
						{
							list.dim().mod().x(-list.dim().width()).y(y).flush();
						}

						list.setVisible(true);
						list.setEnabled(true);
					}
				}

				@Override
				public void onHoverExit()
				{
					if (GuiDropdownList.this.subLists.containsKey(element))
					{
						GuiDropdownList list = GuiDropdownList.this.subLists.get(element);

						if (!InputHelper.isHoveredAndTopElement(list))
						{
							list.setVisible(false);
							list.setEnabled(false);
						}
					}
				}

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
