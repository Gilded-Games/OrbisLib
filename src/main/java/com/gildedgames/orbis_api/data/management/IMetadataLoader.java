package com.gildedgames.orbis_api.data.management;

import java.io.InputStream;
import java.io.OutputStream;

public interface IMetadataLoader<PROJECT extends IProject>
{
	void saveMetadata(PROJECT project, IData data, OutputStream output);

	IDataMetadata loadMetadata(PROJECT project, InputStream input);
}
