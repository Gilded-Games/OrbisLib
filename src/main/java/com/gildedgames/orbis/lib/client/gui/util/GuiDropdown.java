package com.gildedgames.orbis.lib.client.gui.util;

import com.gildedgames.orbis.lib.client.gui.data.IDropdownElement;
import com.gildedgames.orbis.lib.client.gui.util.decorators.GuiScrollable;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiState;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiStateListener;
import com.gildedgames.orbis.lib.client.rect.Dim2D;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.gildedgames.orbis.lib.client.rect.RectModifier;
import net.minecraft.util.text.TextComponentTranslation;

public class GuiDropdown<ELEMENT extends IDropdownElement> extends GuiElement
		implements IDropdownListListener<ELEMENT>, IGuiStateListener, GuiScrollable.IInputEnabledOutsideBounds<GuiElement>
{
	private GuiDropdownList<ELEMENT> list;

	private ELEMENT chosen;

	private GuiTextLabel chosenLabel;

	private IDropdownListListener<ELEMENT> onClickListener;

	public GuiDropdown(Rect rect, IDropdownListListener<ELEMENT> onClickListener, ELEMENT... elements)
	{
		super(rect.rebuild().height(18).flush(), true);

		this.list = new GuiDropdownList<>(Dim2D.build().y(17).flush(), elements);
		this.list.dim().add("dropdownWidth", this, RectModifier.ModifierType.WIDTH);

		this.list.listen(this);

		this.onClickListener = onClickListener;
	}

	@Override
	public void onSetVisible(IGuiState state, boolean oldValue, boolean newValue)
	{
		if (!newValue)
		{
			this.list.state().setVisible(false);
			this.list.state().setEnabled(false);
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
	public void build()
	{
		if (this.chosen == null)
		{
			this.chosen = this.list.getElements().isEmpty() ? null : this.list.getElements().get(0);
		}

		this.chosenLabel = new GuiTextLabel(Dim2D.build().height(18).flush(),
				this.chosen == null ? new TextComponentTranslation("orbis.gui.none_chosen") : this.chosen.text());

		this.chosenLabel.dim().add("dropdownWidth", this, RectModifier.ModifierType.WIDTH);

		this.list.state().setEnabled(false);
		this.list.state().setVisible(false);

		this.context().addChildren(this.list, this.chosenLabel);

		this.list.state().setZOrder(5);

		this.state().listen(this);

		this.chosenLabel.state().setCanBeTopHoverElement(true);
	}

	@Override
	public void onMouseClicked(IGuiElement element, final double mouseX, final double mouseY, final int mouseButton)
	{
		if (mouseButton == 0)
		{
			if (this.chosenLabel.state().isHoveredAndTopElement() && this.chosenLabel.state().isVisible())
			{
				this.list.state().setEnabled(true);
				this.list.state().setVisible(true);
			}
		}
	}

	@Override
	public void onMouseClickedOutsideBounds(GuiElement element, final double mouseX, final double mouseY, final int mouseButton)
	{
		this.onMouseClicked(element, mouseX, mouseY, mouseButton);

		this.list.state().setEnabled(false);
		this.list.state().setVisible(false);
	}

	@Override
	public void onClick(ELEMENT element)
	{
		this.chosen = element;

		this.chosenLabel.setText(element.text());

		this.onClickListener.onClick(element);
	}
}
