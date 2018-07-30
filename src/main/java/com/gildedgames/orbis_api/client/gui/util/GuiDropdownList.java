package com.gildedgames.orbis_api.client.gui.util;

import com.gildedgames.orbis_api.client.gui.data.IDropdownElement;
import com.gildedgames.orbis_api.client.gui.util.decorators.GuiScrollable;
import com.gildedgames.orbis_api.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.client.rect.Rect;
import com.gildedgames.orbis_api.client.rect.RectModifier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;

import java.util.*;

public class GuiDropdownList<ELEMENT extends IDropdownElement> extends GuiElement
{
	private final List<ELEMENT> elements = Lists.newArrayList();

	private Set<IDropdownListListener<ELEMENT>> listeners = Sets.newHashSet();

	private Map<ELEMENT, GuiDropdownList> subLists = Maps.newHashMap();

	public GuiDropdownList(Rect rect, final ELEMENT... elements)
	{
		super(rect, true);

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
			int width = this.viewer().fontRenderer().getStringWidth(element.text().getFormattedText());

			if (width > largestWidth)
			{
				largestWidth = width;
			}
		}

		this.dim().mod().width(largestWidth + 10).pos(pos).flush();

		this.state().setVisible(true);
		this.state().setEnabled(true);
	}

	public void setDropdownElements(final Collection<ELEMENT> elements)
	{
		this.elements.clear();
		this.elements.addAll(elements);

		this.tryRebuild();

		for (ELEMENT element : elements)
		{
			if (element.getSubElements() != null)
			{
				GuiDropdownList<?> list = element.getSubElements().get();

				int largestWidth = 0;

				for (IDropdownElement e : list.getElements())
				{
					int width = this.viewer().fontRenderer().getStringWidth(e.text().getFormattedText());

					if (width > largestWidth)
					{
						largestWidth = width;
					}
				}

				list.dim().mod().width(largestWidth + 10).flush();

				list.state().setVisible(false);
				list.state().setEnabled(false);

				list.state().setCanBeTopHoverElement(true);

				this.context().addChildren(list);

				this.subLists.put(element, list);
			}
		}
	}

	public void addDropdownElements(Collection<ELEMENT> elements)
	{
		this.elements.addAll(elements);

		this.tryRebuild();
	}

	public void addDropdownElements(ELEMENT... elements)
	{
		this.elements.addAll(Arrays.asList(elements));

		this.tryRebuild();
	}

	@Override
	public void build()
	{
		for (int i = 0; i < this.elements.size(); i++)
		{
			final ELEMENT element = this.elements.get(i);
			final int y = 18 * i;

			final int id = i;

			final GuiTextLabel label = new GuiTextLabel(Dim2D.build().y(y).height(18).flush(), element.text());

			label.state().addEvent(new GuiScrollable.InputEnabledOutsideBounds<GuiTextLabel>()
			{
				@Override
				public boolean shouldHoverOutsideBounds(GuiTextLabel gui)
				{
					return true;
				}

				@Override
				public void onHoverEnter(GuiTextLabel gui)
				{
					if (GuiDropdownList.this.subLists.containsKey(element))
					{
						GuiDropdownList list = GuiDropdownList.this.subLists.get(element);

						list.dim().mod().x(GuiDropdownList.this.dim().width()).y(y).flush();

						if (list.dim().maxX() >= GuiDropdownList.this.viewer().getScreenWidth())
						{
							list.dim().mod().x(-list.dim().width()).y(y).flush();
						}

						list.state().setVisible(true);
						list.state().setEnabled(true);
					}
				}

				@Override
				public void onHoverExit(GuiTextLabel gui)
				{
					if (GuiDropdownList.this.subLists.containsKey(element))
					{
						GuiDropdownList list = GuiDropdownList.this.subLists.get(element);

						if (!list.state().isHoveredAndTopElement())
						{
							list.state().setVisible(false);
							list.state().setEnabled(false);
						}
					}
				}

				@Override
				public void onMouseClickedOutsideBounds(GuiTextLabel gui, final int mouseX, final int mouseY, final int mouseButton)
				{
					this.onMouseClicked(gui, mouseX, mouseY, mouseButton);
				}

				@Override
				public void onMouseClicked(GuiTextLabel gui, final int mouseX, final int mouseY, final int mouseButton)
				{
					if (mouseButton == 0)
					{
						if (gui.state().isHoveredAndTopElement() && GuiDropdownList.this.state().isEnabled())
						{
							element.onClick(GuiDropdownList.this, Minecraft.getMinecraft().player);

							GuiDropdownList.this.listeners.forEach((l) -> l.onClick(element));
						}

						if (id >= GuiDropdownList.this.elements.size() - 1)
						{
							GuiDropdownList.this.state().setVisible(false);
							GuiDropdownList.this.state().setEnabled(false);
						}
					}
				}
			});

			label.dim().add("dropdownListWidth", this, RectModifier.ModifierType.WIDTH);

			this.context().addChildren(label);

			label.state().setCanBeTopHoverElement(true);
		}

		this.dim().mod().height(18 * this.elements.size()).flush();
	}
}
