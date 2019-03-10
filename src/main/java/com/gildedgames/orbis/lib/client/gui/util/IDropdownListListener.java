package com.gildedgames.orbis.lib.client.gui.util;

import com.gildedgames.orbis.lib.client.gui.data.IDropdownElement;

public interface IDropdownListListener<ELEMENT extends IDropdownElement>
{
	void onClick(ELEMENT element);
}
