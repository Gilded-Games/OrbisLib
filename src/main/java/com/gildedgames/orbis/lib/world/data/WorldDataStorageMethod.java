package com.gildedgames.orbis.lib.world.data;

public enum WorldDataStorageMethod
{
	FLAT("flat");

	public final String serializedName;

	WorldDataStorageMethod(String serializedName)
	{
		this.serializedName = serializedName;
	}

}
