package com.gildedgames.orbis_api.data;

public interface IDataChild<DATA>
{
	Class<? extends DATA> getDataClass();

	DATA getDataParent();

	void setDataParent(DATA data);
}
