package com.gildedgames.orbis_api.network.filetransfer;

import com.gildedgames.orbis_api.OrbisLib;
import com.gildedgames.orbis_api.filetransfer.IFileReceiver;
import com.gildedgames.orbis_api.filetransfer.IFileTransferTracker;
import com.gildedgames.orbis_api.filetransfer.IListingReceiver;
import com.gildedgames.orbis_api.filetransfer.ITransferTicket;
import com.gildedgames.orbis_api.network.NetworkUtils;
import com.gildedgames.orbis_api.network.instances.MessageHandlerClient;
import com.gildedgames.orbis_api.network.instances.MessageHandlerServer;
import com.gildedgames.orbis_api.network.util.IMessageHeader;
import com.gildedgames.orbis_api.network.util.PacketMultipleParts;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.Optional;

public class PacketTransferFile extends PacketMultipleParts implements IMessageHeader<PacketTransferFile>
{
	private ResourceLocation trackerId;

	private ITransferTicket ticket;

	private byte[] fileData;

	private String relativePath;

	public PacketTransferFile()
	{

	}

	public PacketTransferFile(ResourceLocation trackerId, ITransferTicket ticket, byte[] fileData, String relativePath)
	{
		this.trackerId = trackerId;
		this.ticket = ticket;
		this.fileData = fileData;
		this.relativePath = relativePath;
	}

	public PacketTransferFile(byte[] data)
	{
		super(data);
	}

	@Override
	public PacketMultipleParts createPart(byte[] data)
	{
		return new PacketTransferFile(data);
	}

	@Override
	public void readHeader(ByteBuf buf)
	{
		final NBTTagCompound tag = NetworkUtils.readTagLimitless(buf);
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.trackerId = new ResourceLocation(tag.getString("trackerId"));
		this.ticket = funnel.get("ticket");
	}

	@Override
	public void writeHeader(ByteBuf buf)
	{
		NBTTagCompound tag = new NBTTagCompound();

		NBTFunnel funnel = new NBTFunnel(tag);

		tag.setString("trackerId", this.trackerId.toString());
		funnel.set("ticket", this.ticket);

		ByteBufUtils.writeTag(buf, tag);
	}

	@Override
	public void transferDataFromHeader(PacketTransferFile header)
	{
		this.ticket = header.ticket;
		this.trackerId = header.trackerId;
	}

	@Override
	public void read(ByteBuf buf)
	{
		final NBTTagCompound tag = NetworkUtils.readTagLimitless(buf);
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.trackerId = new ResourceLocation(tag.getString("trackerId"));
		this.ticket = funnel.get("ticket");
	}

	@Override
	public void write(ByteBuf buf)
	{
		NBTTagCompound tag = new NBTTagCompound();

		NBTFunnel funnel = new NBTFunnel(tag);

		tag.setString("trackerId", this.trackerId.toString());
		funnel.set("ticket", this.ticket);

		ByteBufUtils.writeTag(buf, tag);
	}

	public static class HandlerServer extends MessageHandlerServer<PacketTransferFile, IMessage>
	{
		@Override
		public IMessage onPart(int partId, final PacketTransferFile message, final EntityPlayer player)
		{
			if (player == null || player.world == null)
			{
				return null;
			}

			message.ticket.setProgress(message.getTotalParts() / partId);

			OrbisLib.LOGGER.info(message.ticket.getId() + " - Progress on ticket: " + message.ticket.getProgress());

			return null;
		}

		@Override
		public IMessage onHeader(final PacketTransferFile message, final EntityPlayer player)
		{
			if (player == null || player.world == null)
			{
				return null;
			}

			Optional<IFileTransferTracker> tracker = OrbisLib.PROXY.getFileTransferManager().getTracker(message.trackerId);

			if (tracker.isPresent())
			{
				Optional<IListingReceiver> listingReceiver = tracker.get().getListingReceiver();

				if (listingReceiver.isPresent())
				{
					listingReceiver.get().receiveTicket(message.ticket);
				}
				else
				{
					OrbisLib.LOGGER.info("Listing receiver could not be found when sending transfer ticket to server");
				}
			}
			else
			{
				OrbisLib.LOGGER.info("Tracker could not be found when sending transfer ticket to server");
			}

			return null;
		}

		@Override
		public IMessage onMessage(final PacketTransferFile message, final EntityPlayer player)
		{
			if (player == null || player.world == null)
			{
				return null;
			}

			message.ticket.setProgress(1.0F);

			Optional<IFileTransferTracker> tracker = OrbisLib.PROXY.getFileTransferManager().getTracker(message.trackerId);

			if (tracker.isPresent())
			{
				IFileReceiver fileReceiver = tracker.get().getFileReceiver();

				fileReceiver.receive(message.relativePath, message.fileData);
			}
			else
			{
				OrbisLib.LOGGER.info("Tracker could not be found when sending file data to server");
			}

			return null;
		}
	}

	public static class HandlerClient extends MessageHandlerClient<PacketTransferFile, IMessage>
	{
		@Override
		public IMessage onPart(int partId, final PacketTransferFile message, final EntityPlayer player)
		{
			if (player == null || player.world == null)
			{
				return null;
			}

			message.ticket.setProgress(message.getTotalParts() / partId);

			OrbisLib.LOGGER.info(message.ticket.getId() + " - Progress on ticket: " + message.ticket.getProgress());

			return null;
		}

		@Override
		public IMessage onHeader(final PacketTransferFile message, final EntityPlayer player)
		{
			if (player == null || player.world == null)
			{
				return null;
			}

			Optional<IFileTransferTracker> tracker = OrbisLib.PROXY.getFileTransferManager().getTracker(message.trackerId);

			if (tracker.isPresent())
			{
				Optional<IListingReceiver> listingReceiver = tracker.get().getListingReceiver();

				if (listingReceiver.isPresent())
				{
					listingReceiver.get().receiveTicket(message.ticket);
				}
				else
				{
					OrbisLib.LOGGER.info("Listing receiver could not be found when sending transfer ticket to client");
				}
			}
			else
			{
				OrbisLib.LOGGER.info("Tracker could not be found when sending transfer ticket to client");
			}

			return null;
		}

		@Override
		public IMessage onMessage(final PacketTransferFile message, final EntityPlayer player)
		{
			if (player == null || player.world == null)
			{
				return null;
			}

			message.ticket.setProgress(1.0F);

			Optional<IFileTransferTracker> tracker = OrbisLib.PROXY.getFileTransferManager().getTracker(message.trackerId);

			if (tracker.isPresent())
			{
				IFileReceiver fileReceiver = tracker.get().getFileReceiver();

				fileReceiver.receive(message.relativePath, message.fileData);
			}
			else
			{
				OrbisLib.LOGGER.info("Tracker could not be found when sending file data to client");
			}

			return null;
		}
	}
}
