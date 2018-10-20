package com.gildedgames.orbis_api.data.management;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface IDataLoader<PROJECT extends IProject>
{
	void saveData(PROJECT project, IData data, File file, String location, OutputStream output);

	IData loadData(PROJECT project, File file, String location, InputStream input);
}
