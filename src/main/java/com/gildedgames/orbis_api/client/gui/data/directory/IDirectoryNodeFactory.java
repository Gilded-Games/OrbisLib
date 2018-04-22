package com.gildedgames.orbis_api.client.gui.data.directory;

import java.io.File;

public interface IDirectoryNodeFactory
{

	INavigatorNode createFrom(File file, String extension);

}
