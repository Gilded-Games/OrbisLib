package com.gildedgames.orbis_api.data.management;

import java.io.InputStream;

public interface IProjectFilesystem
{
	InputStream getInputStream(String path);
}
