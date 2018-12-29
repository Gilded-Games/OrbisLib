package com.gildedgames.orbis_api.filetransfer;

public interface IFileReceiver
{
	void receive(String relativePath, byte[] data);
}
