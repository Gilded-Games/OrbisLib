package com.gildedgames.orbis_api.filetransfer;

import com.gildedgames.orbis_api.util.mc.NBT;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

/**
 * Represents the on-going info of a file transfer taking place.
 * Can be used to track progress as well as find out which player it's either
 * sending to or coming from.
 */
public interface ITransferTicket extends NBT
{
	/**
	 * @return The progress of the transfer represented as a percentage between 0.0 and 1.0
	 */
	float getProgress();

	/**
	 * Set the progress of this transfer.
	 * @param progress The progress of the transfer represented as a percentage between 0.0 and 1.0
	 */
	void setProgress(float progress);

	/**
	 * @return The unique id used to identify this transfer.
	 */
	UUID getId();

	/**
	 * @return The client/player this transfer is either sending to or sending from.
	 */
	EntityPlayer getClient();
}
