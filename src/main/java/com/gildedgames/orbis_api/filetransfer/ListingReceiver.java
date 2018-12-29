package com.gildedgames.orbis_api.filetransfer;

import com.gildedgames.orbis_api.OrbisLib;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class ListingReceiver implements IListingReceiver
{
	private Map<UUID, ITransferListing> idToListing = Maps.newHashMap();

	public ListingReceiver()
	{

	}

	@Override
	public void receiveListing(ITransferListing listing)
	{
		if (listing == null)
		{
			OrbisLib.LOGGER.info("Received a null transfer listing in the ListingReceiver implementation. Ignoring.");
			return;
		}

		this.idToListing.put(listing.getId(), listing);
	}

	@Override
	public Collection<ITransferListing> getListings()
	{
		return this.idToListing.values();
	}

	@Override
	public void receiveTicket(ITransferTicket ticket)
	{
		if (ticket == null)
		{
			OrbisLib.LOGGER.info("Received a null transfer ticket in the ListingReceiver implementation. Ignoring.");
			return;
		}

		if (!this.idToListing.containsKey(ticket.getId()))
		{
			OrbisLib.LOGGER.info("Received a transfer ticket with an ID that doesn't exist in the ListingReceiver implementation. Ignoring.");
			return;
		}

		ITransferListing listing = this.idToListing.get(ticket.getId());

		listing.attachTicket(ticket);
	}
}
