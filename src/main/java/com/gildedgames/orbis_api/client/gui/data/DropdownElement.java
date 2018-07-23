package com.gildedgames.orbis_api.client.gui.data;

import com.gildedgames.orbis_api.client.gui.util.GuiDropdownList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.function.Supplier;

public class DropdownElement implements IDropdownElement
{
	private final ITextComponent text;

	private Supplier<GuiDropdownList> subElements;

	public DropdownElement(final ITextComponent text)
	{
		this.text = text;
	}

	public DropdownElement(ITextComponent text, Supplier<GuiDropdownList> subElements)
	{
		this(text);

		this.subElements = subElements;
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

	@Override
	public Supplier<GuiDropdownList> getSubElements()
	{
		return this.subElements;
	}

	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(this.text.toString().hashCode());

		return builder.toHashCode();
	}
}
