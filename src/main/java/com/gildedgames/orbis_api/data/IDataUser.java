package com.gildedgames.orbis_api.data;

public interface IDataUser<DATA>
{
	String getDataIdentifier();

	void setUsedData(DATA data);
}
