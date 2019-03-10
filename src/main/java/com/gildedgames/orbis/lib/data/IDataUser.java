package com.gildedgames.orbis.lib.data;

public interface IDataUser<DATA>
{
	String getDataIdentifier();

	void setUsedData(DATA data);
}
