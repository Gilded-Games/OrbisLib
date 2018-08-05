package com.gildedgames.orbis_api.data.management;

import java.io.File;

public interface IManualDataLoader<PROJECT extends IProject>
{
	void saveMetadata(PROJECT project, IData data, File file, String location);

	IData load(PROJECT project, File file, String location);
}
