package com.gildedgames.orbis.lib.client.gui.data;

import com.gildedgames.orbis.lib.client.gui.util.GuiDropdownList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface IDropdownElement
{

	ITextComponent text();

	void onClick(GuiDropdownList list, PlayerEntity player);

	@Nullable
	Supplier<GuiDropdownList> getSubElements();

}
