package com.gildedgames.orbis_api.world.data;

public enum WorldDataStorageMethod
{
	FLAT("flat");

	public final String serializedName;

	WorldDataStorageMethod(String serializedName)
	{
		this.serializedName = serializedName;
	}

}
