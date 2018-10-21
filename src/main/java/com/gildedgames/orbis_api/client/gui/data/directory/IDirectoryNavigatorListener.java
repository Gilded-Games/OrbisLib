package com.gildedgames.orbis_api.client.gui.data.directory;

import java.io.File;
import java.util.List;

public interface IDirectoryNavigatorListener
{

	void onNodeClick(IDirectoryNavigator navigator, INavigatorNode node);

	void onNodeOpen(IDirectoryNavigator navigator, INavigatorNode node);

	void onDirectoryOpen(IDirectoryNavigator navigator, File file);

	void onDirectoriesViewed(IDirectoryNavigator navigator, List<File> files);

	void onBack(IDirectoryNavigator navigator);

	void onForward(IDirectoryNavigator navigator);

	void onRefresh(IDirectoryNavigator navigator);

}
