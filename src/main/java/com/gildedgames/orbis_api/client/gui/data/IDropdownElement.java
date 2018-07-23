package com.gildedgames.orbis_api.client.gui.data;

import com.gildedgames.orbis_api.client.gui.util.GuiDropdownList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface IDropdownElement
{

	ITextComponent text();

	void onClick(GuiDropdownList list, EntityPlayer player);

	@Nullable
	Supplier<GuiDropdownList> getSubElements();

}
