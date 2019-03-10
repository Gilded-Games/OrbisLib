package com.gildedgames.orbis.lib.data.management;

import java.io.InputStream;

public interface IProjectFilesystem
{
	InputStream getInputStream(String path);
}
