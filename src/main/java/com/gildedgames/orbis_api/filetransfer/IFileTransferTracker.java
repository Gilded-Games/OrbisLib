package com.gildedgames.orbis_api.filetransfer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public interface IFileTransferTracker
{
	/**
	 * @return The id used to identify the trackers across the network.
	 */
	ResourceLocation getTrackerId();

	ITransferTicket transfer(UUID ticketId, File file, String relativeDestination, EntityPlayer client);

	/**
	 * Handles the receiving of all files transferred to this FileTransferTracker
	 * @return The file receiver implementation for this FileTransferTracker
	 */
	IFileReceiver getFileReceiver();

	/**
	 * Optional listing receiver if this implementation requires
	 * listings to function.
	 * @return An optional return of an IListingReceiver implementation
	 */
	Optional<IListingReceiver> getListingReceiver();
}