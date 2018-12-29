package com.gildedgames.orbis_api.network;

import com.gildedgames.orbis_api.network.util.IMessageMultipleParts;
import com.google.common.collect.Maps;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetworkMultipleParts implements INetworkMultipleParts
{
	private final HashMap<Integer, ArrayList<byte[]>> packetParts = Maps.newHashMap();

	private final HashMap<Integer, ? extends IMessage> packetHeaders = Maps.newHashMap();

	private SimpleNetworkWrapper instance;

	private int discriminant;

	public NetworkMultipleParts(String modID)
	{
		this.instance = NetworkRegistry.INSTANCE.newSimpleChannel(modID);
	}

	@Override
	public <REQ extends IMessage, REPLY extends IMessage> void reg(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType,
			Side side)
	{
		this.instance.registerMessage(messageHandler, requestMessageType, this.discriminant++, side);
	}

	@Override
	public Map<Integer, ArrayList<byte[]>> getPacketParts()
	{
		return this.packetParts;
	}

	@Override
	public Map<Integer, ? extends IMessage> getPacketHeaders()
	{
		return this.packetHeaders;
	}

	private IMessage[] fetchParts(final IMessage message)
	{
		final IMessage[] parts;

		if (message instanceof IMessageMultipleParts)
		{
			final IMessageMultipleParts multipleParts = (IMessageMultipleParts) message;
			parts = multipleParts.getParts();
		}
		else
		{
			parts = new IMessage[1];

			parts[0] = message;
		}

		return parts;
	}

	@Override
	public void sendPacketToDimension(final IMessage message, final int dimension)
	{
		for (final IMessage part : this.fetchParts(message))
		{
			this.instance.sendToDimension(part, dimension);
		}
	}

	@Override
	public void sendPacketToAllPlayers(final IMessage message)
	{
		for (final IMessage part : this.fetchParts(message))
		{
			this.instance.sendToAll(part);
		}
	}

	@Override
	public void sendPacketToAllPlayersExcept(final IMessage message, final EntityPlayerMP player)
	{
		final PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

		for (final IMessage part : this.fetchParts(message))
		{
			for (final EntityPlayerMP p : playerList.getPlayers())
			{
				if (p != player)
				{
					this.instance.sendTo(part, p);
				}
			}
		}
	}

	@Override
	public void sendPacketToPlayer(final IMessage message, final EntityPlayerMP player)
	{
		for (final IMessage part : this.fetchParts(message))
		{
			this.instance.sendTo(part, player);
		}
	}

	@Override
	public void sendPacketToWatching(final IMessage message, final EntityLivingBase entity, final boolean includeSelf)
	{
		for (final IMessage part : this.fetchParts(message))
		{
			final WorldServer world = (WorldServer) entity.world;

			final EntityTracker tracker = world.getEntityTracker();

			for (final EntityPlayer player : tracker.getTrackingPlayers(entity))
			{
				this.sendPacketToPlayer(part, (EntityPlayerMP) player);
			}

			// Entities don't watch themselves, take special care here
			if (includeSelf && entity instanceof EntityPlayer)
			{
				this.sendPacketToPlayer(part, (EntityPlayerMP) entity);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void sendPacketToServer(final IMessage message)
	{
		for (final IMessage part : this.fetchParts(message))
		{
			this.instance.sendToServer(part);
		}
	}

}
