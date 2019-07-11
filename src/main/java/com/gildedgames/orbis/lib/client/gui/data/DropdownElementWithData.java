package com.gildedgames.orbis.lib.client.gui.data;

import com.gildedgames.orbis.lib.client.gui.util.GuiDropdownList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

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
	public void onClick(final GuiDropdownList list, final PlayerEntity player)
	{

	}

	@Nullable
	@Override
	public Supplier<GuiDropdownList> getSubElements()
	{
		return null;
	}

	public DATA getData()
	{
		return this.data;
	}
}
