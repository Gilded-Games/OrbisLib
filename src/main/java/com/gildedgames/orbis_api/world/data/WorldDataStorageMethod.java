package com.gildedgames.orbis_api.world.data;

public enum WorldDataStorageMethod
{
	LMDB("lmdb"),
	FLAT("flat");

	public final String serializedName;

	WorldDataStorageMethod(String serializedName)
	{
		this.serializedName = serializedName;
	}

}
