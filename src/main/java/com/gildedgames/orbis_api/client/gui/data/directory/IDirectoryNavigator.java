package com.gildedgames.orbis_api.client.gui.data.directory;

import java.io.File;
import java.util.List;

/**
 * An interface that will dictate/control the directory viewing state.
 * This is all non-graphical, while a Gui will listen in to this navigator
 * and update the interface based on the state this navigator provides.
 */
public interface IDirectoryNavigator
{

	/**
	 * Adds a listener to the navigator. An example use is a
	 * Gui which will display the state this navigator provides.
	 * @param listener
	 */
	void addListener(IDirectoryNavigatorListener listener);

	/**
	 * Removes a listener from the navigator.
	 * @param listener
	 * @return Whether the listener was removed or not.
	 */
	boolean removeListener(IDirectoryNavigatorListener listener);

	void onClickNode(INavigatorNode node);

	void onOpenNode(INavigatorNode node);

	/**
	 * Opens the provided file if it is a directory.
	 * @param file
	 */
	void openDirectory(File file);

	/**
	 * Injects a list of directories into the navigator of an existing directory when its viewed.
	 * @param forDirectory Will only inject directories in this directory.
	 * @param injectedDirectories The injected directories.
	 */
	void injectDirectories(File forDirectory, List<File> injectedDirectories);

	/**
	 * @return Whether or not the navigator has any history to
	 * go back to.
	 */
	boolean canGoBack();

	/**
	 * @return Whether or not the navigator has any history to
	 * go forward to.
	 */
	boolean canGoForward();

	/**
	 * If the implementation supports history, this method will
	 * navigate backwards if possible.
	 */
	void back();

	/**
	 * If the implementation supports history, this method will
	 * navigate forwards if possible.
	 */
	void forward();

	/**
	 * This will refresh the state within the currently navigated directory.
	 */
	void refresh();

	/**
	 * @return The current directory this navigator has navigated to.
	 */
	File currentDirectory();

	/**
	 * Provides a list of navigator tree within the current directory.
	 */
	List<INavigatorNode> getNodes();

}
