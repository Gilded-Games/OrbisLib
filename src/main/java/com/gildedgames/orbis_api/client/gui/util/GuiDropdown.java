package com.gildedgames.orbis_api.client.gui.util;

import com.gildedgames.orbis_api.client.gui.data.IDropdownElement;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.client.rect.Rect;
import com.gildedgames.orbis_api.client.rect.RectModifier;
import com.gildedgames.orbis_api.util.InputHelper;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.IOException;

public class GuiDropdown<ELEMENT extends IDropdownElement> extends GuiFrame implements IDropdownListListener<ELEMENT>
{
	private GuiDropdownList<ELEMENT> list;

	private ELEMENT chosen;

	private GuiTextLabel chosenLabel;

	private IDropdownListListener<ELEMENT> onClickListener;

	public GuiDropdown(Rect rect, IDropdownListListener<ELEMENT> onClickListener, ELEMENT... elements)
	{
		super(rect.rebuild().height(18).flush());

		this.list = new GuiDropdownList<>(Dim2D.build().y(17).flush(), elements);
		this.list.dim().add("dropdownWidth", this, RectModifier.ModifierType.WIDTH);

		this.list.listen(this);

		this.onClickListener = onClickListener;
	}

	@Override
	public void setVisible(boolean flag)
	{
		super.setVisible(flag);

		if (!flag)
		{
			this.list.setVisible(false);
			this.list.setEnabled(false);
		}
	}

	public GuiDropdownList<ELEMENT> getList()
	{
		return this.list;
	}

	public ELEMENT getChosenElement()
	{
		return this.chosen;
	}

	public void setChosenElement(ELEMENT element)
	{
		this.chosen = element;

		if (this.chosen != null && this.chosenLabel != null)
		{
			this.chosenLabel.setText(this.chosen.text());
		}
	}

	@Override
	public void init()
	{
		if (this.chosen == null)
		{
			this.chosen = this.list.getElements().isEmpty() ? null : this.list.getElements().get(0);
		}

		this.chosenLabel = new GuiTextLabel(Dim2D.build().height(18).flush(),
				this.chosen == null ? new TextComponentTranslation("orbis.gui.none_chosen") : this.chosen.text());

		this.chosenLabel.dim().add("dropdownWidth", this, RectModifier.ModifierType.WIDTH);

		this.list.setEnabled(false);
		this.list.setVisible(false);

		this.list.setZOrder(5);

		this.addChildren(this.list, this.chosenLabel);
	}

	@Override
	public void draw()
	{

	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if (mouseButton == 0)
		{
			if (InputHelper.isHoveredAndTopElement(this.chosenLabel) && this.chosenLabel.isVisible())
			{
				this.list.setEnabled(true);
				this.list.setVisible(true);
			}
		}
	}

	@Override
	public void mouseClickedOutsideBounds(final int mouseX, final int mouseY, final int mouseButton)
	{
		super.mouseClickedOutsideBounds(mouseX, mouseY, mouseButton);

		this.list.setEnabled(false);
		this.list.setVisible(false);
	}

	@Override
	public void onClick(ELEMENT element)
	{
		this.chosen = element;

		this.chosenLabel.setText(element.text());

		this.onClickListener.onClick(element);
	}
}
