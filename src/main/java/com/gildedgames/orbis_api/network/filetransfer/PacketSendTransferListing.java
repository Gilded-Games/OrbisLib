package com.gildedgames.orbis_api.network.filetransfer;

import com.gildedgames.orbis_api.OrbisLib;
import com.gildedgames.orbis_api.filetransfer.IFileTransferTracker;
import com.gildedgames.orbis_api.filetransfer.IListingReceiver;
import com.gildedgames.orbis_api.filetransfer.ITransferListing;
import com.gildedgames.orbis_api.network.NetworkUtils;
import com.gildedgames.orbis_api.network.instances.MessageHandlerClient;
import com.gildedgames.orbis_api.network.instances.MessageHandlerServer;
import com.gildedgames.orbis_api.network.util.PacketMultipleParts;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.Optional;

public class PacketSendTransferListing extends PacketMultipleParts
{
	private ResourceLocation trackerId;

	private ITransferListing listing;

	public PacketSendTransferListing()
	{

	}

	public PacketSendTransferListing(ResourceLocation trackerId, ITransferListing listing)
	{
		this.trackerId = trackerId;
		this.listing = listing;
	}

	public PacketSendTransferListing(byte[] data)
	{
		super(data);
	}

	@Override
	public PacketMultipleParts createPart(byte[] data)
	{
		return new PacketSendTransferListing(data);
	}

	@Override
	public void read(ByteBuf buf)
	{
		final NBTTagCompound tag = NetworkUtils.readTagLimitless(buf);
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.trackerId = new ResourceLocation(tag.getString("trackerId"));
		this.listing = funnel.get("listing");
	}

	@Override
	public void write(ByteBuf buf)
	{
		NBTTagCompound tag = new NBTTagCompound();

		NBTFunnel funnel = new NBTFunnel(tag);

		tag.setString("trackerId", this.trackerId.toString());
		funnel.set("listing", this.listing);

		ByteBufUtils.writeTag(buf, tag);
	}

	public static class HandlerServer extends MessageHandlerServer<PacketSendTransferListing, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSendTransferListing message, final EntityPlayer player)
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
					listingReceiver.get().receiveListing(message.listing);
				}
				else
				{
					OrbisLib.LOGGER.info("Listing receiver could not be found when sending transfer listing to server");
				}
			}
			else
			{
				OrbisLib.LOGGER.info("Tracker could not be found when sending transfer listing to server");
			}

			return null;
		}
	}

	public static class HandlerClient extends MessageHandlerClient<PacketSendTransferListing, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSendTransferListing message, final EntityPlayer player)
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
					listingReceiver.get().receiveListing(message.listing);
				}
				else
				{
					OrbisLib.LOGGER.info("Listing receiver could not be found when sending transfer listing to server");
				}
			}
			else
			{
				OrbisLib.LOGGER.info("Tracker could not be found when sending transfer listing to server");
			}

			return null;
		}
	}
}
