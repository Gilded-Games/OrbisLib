package com.gildedgames.orbis_api.network.util;

import io.netty.buffer.ByteBuf;

public interface IMessageHeader<MESSAGE>
{
	void readHeader(ByteBuf buf);

	void writeHeader(ByteBuf buf);

	void transferDataFromHeader(MESSAGE header);
}
