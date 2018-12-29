package com.gildedgames.orbis_api.filetransfer;

import com.gildedgames.orbis_api.util.mc.NBT;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents a file that can possibly be transferred between the network.
 */
public interface ITransferListing extends NBT
{
	String[] getMetadata();

	UUID getId();

	void attachTicket(ITransferTicket ticket);

	Optional<ITransferTicket> getTicket();
}
