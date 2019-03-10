package com.gildedgames.orbis.lib.network.instances;

import com.gildedgames.orbis.lib.OrbisLib;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketUnregisterDimension implements IMessage
{

	private int dimID;

	public PacketUnregisterDimension()
	{

	}

	public PacketUnregisterDimension(final int dimID)
	{
		this.dimID = dimID;
	}

	@Override
	public void fromBytes(final ByteBuf buf)
	{
		this.dimID = buf.readInt();
	}

	@Override
	public void toBytes(final ByteBuf buf)
	{
		buf.writeInt(this.dimID);
	}

	public static class Handler extends MessageHandlerClient<PacketUnregisterDimension, PacketUnregisterDimension>
	{
		@Override
		public PacketUnregisterDimension onMessage(final PacketUnregisterDimension message, final EntityPlayer player)
		{
			if (DimensionManager.isDimensionRegistered(message.dimID))
			{
				DimensionManager.unregisterDimension(message.dimID);
			}
			else
			{
				OrbisLib.LOGGER.warn("Tried to unregister dimension " + message.dimID + " by request of server, but it's not registered");
			}

			return null;
		}
	}

}
