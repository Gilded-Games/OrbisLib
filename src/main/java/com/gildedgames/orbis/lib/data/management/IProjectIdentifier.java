package com.gildedgames.orbis.lib.data.management;

import com.gildedgames.orbis.lib.util.mc.NBT;

import java.util.UUID;

/**
 * An object used to identify a unique project.
 */
public interface IProjectIdentifier extends NBT
{

	/**
	 * Decided by authors.
	 * @return A unique id that represents the project.
	 */
	UUID getProjectId();

	/**
	 * @return The username of the player who first created this project.
	 */
	String getOriginalCreator();

}
