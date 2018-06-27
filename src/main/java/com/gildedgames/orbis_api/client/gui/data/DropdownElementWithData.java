package com.gildedgames.orbis_api.client.gui.data;

import com.gildedgames.orbis_api.client.gui.util.GuiDropdownList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;

public class DropdownElementWithData<DATA> implements IDropdownElement
{
	private final ITextComponent text;

	private DATA data;

	public DropdownElementWithData(final ITextComponent text, DATA data)
	{
		this.text = text;
		this.data = data;
	}

	@Override
	public ITextComponent text()
	{
		return this.text;
	}

	@Override
	public void onClick(final GuiDropdownList list, final EntityPlayer player)
	{

	}

	public DATA getData()
	{
		return this.data;
	}
}
