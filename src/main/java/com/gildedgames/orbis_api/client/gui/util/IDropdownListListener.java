package com.gildedgames.orbis_api.client.gui.util;

import com.gildedgames.orbis_api.client.gui.data.IDropdownElement;

public interface IDropdownListListener<ELEMENT extends IDropdownElement>
{
	void onClick(ELEMENT element);
}
