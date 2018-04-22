package com.gildedgames.orbis_api.network;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Map;

public interface INetworkMultipleParts
{
	<REQ extends IMessage, REPLY extends IMessage> void reg(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side);

	Map<Integer, ArrayList<byte[]>> getPacketParts();

	void sendPacketToDimension(final IMessage message, final int dimension);

	void sendPacketToAllPlayers(final IMessage message);

	void sendPacketToAllPlayersExcept(final IMessage message, final EntityPlayerMP player);

	void sendPacketToPlayer(final IMessage message, final EntityPlayerMP player);

	void sendPacketToWatching(final IMessage message, final EntityLivingBase entity, final boolean includeSelf);

	void sendPacketToServer(final IMessage message);
}