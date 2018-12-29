package com.gildedgames.orbis_api.filetransfer;

import com.gildedgames.orbis_api.OrbisLib;
import com.gildedgames.orbis_api.network.filetransfer.PacketTransferFile;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileTransferTracker implements IFileTransferTracker
{
	private Map<UUID, ITransferTicket> idToTicket = Maps.newHashMap();

	private IFileReceiver fileReceiver;

	private IListingReceiver listingReceiver;

	private ResourceLocation id;

	public FileTransferTracker(ResourceLocation id, IFileReceiver fileReceiver, IListingReceiver listingReceiver)
	{
		this.id = id;
		this.fileReceiver = fileReceiver;
		this.listingReceiver = listingReceiver;
	}

	public FileTransferTracker(ResourceLocation id, IFileReceiver fileReceiver)
	{
		this(id, fileReceiver, null);
	}

	@Override
	public ResourceLocation getTrackerId()
	{
		return this.id;
	}

	@Override
	public ITransferTicket transfer(UUID ticketId, File file, String relativeDestination, EntityPlayer client)
	{
		if (!file.exists())
		{
			return null;
		}

		try (FileInputStream input = new FileInputStream(file))
		{
			byte[] fileData = IOUtils.toByteArray(input);

			TransferTicket ticket = new TransferTicket(ticketId, client);

			this.idToTicket.put(ticketId, ticket);

			if (client.world.isRemote)
			{
				OrbisLib.services().network().sendPacketToServer(new PacketTransferFile(this.id, ticket, fileData, relativeDestination));
			}
			else
			{
				OrbisLib.services().network().sendPacketToPlayer(new PacketTransferFile(this.id, ticket, fileData, relativeDestination),
						(EntityPlayerMP) client);
			}

			return ticket;
		}
		catch (IOException e)
		{
			OrbisLib.LOGGER.info(e);
		}

		return null;
	}

	@Override
	public IFileReceiver getFileReceiver()
	{
		return this.fileReceiver;
	}

	@Override
	public Optional<IListingReceiver> getListingReceiver()
	{
		return this.listingReceiver == null ? Optional.empty() : Optional.of(this.listingReceiver);
	}
}
