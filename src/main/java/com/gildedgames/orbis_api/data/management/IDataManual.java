package com.gildedgames.orbis_api.data.management;

/**
 * Same as normal data but manually written and read from the file system.
 * Doesn't use standard NBT data structure.
 *
 * Part of that is setting back the metadata manually.
 */
public interface IDataManual extends IData
{
	void setMetadata(IDataMetadata metadata);
}
