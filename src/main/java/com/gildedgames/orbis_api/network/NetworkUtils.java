package com.gildedgames.orbis_api.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;
import java.io.IOException;

public class NetworkUtils
{
	@Nullable
	public static NBTTagCompound readTagLimitless(ByteBuf from)
	{
		PacketBuffer pb = new PacketBuffer(from);
		int i = pb.readerIndex();
		byte b0 = pb.readByte();

		if (b0 == 0)
		{
			return null;
		}
		else
		{
			pb.readerIndex(i);

			try
			{
				return CompressedStreamTools.read(new ByteBufInputStream(pb), NBTSizeTracker.INFINITE);
			}
			catch (IOException ioexception)
			{
				throw new EncoderException(ioexception);
			}
		}
	}
}
