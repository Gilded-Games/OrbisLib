package com.gildedgames.orbis_api.filetransfer;

import java.util.Collection;

public interface IListingReceiver
{
	void receiveTicket(ITransferTicket ticket);

	void receiveListing(ITransferListing listing);

	Collection<ITransferListing> getListings();
}
