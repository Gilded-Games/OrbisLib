package com.gildedgames.orbis_api.data.management;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface IMetadataLoader<PROJECT extends IProject>
{
	void saveMetadata(PROJECT project, IData data, File file, String location, OutputStream output);

	IDataMetadata loadMetadata(PROJECT project, File file, String location, InputStream input);
}
