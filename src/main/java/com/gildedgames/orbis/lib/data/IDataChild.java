package com.gildedgames.orbis.lib.data;

public interface IDataChild<DATA>
{
	Class<? extends DATA> getDataClass();

	DATA getDataParent();

	void setDataParent(DATA data);
}
