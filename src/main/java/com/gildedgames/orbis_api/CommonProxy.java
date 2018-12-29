package com.gildedgames.orbis_api;

import com.gildedgames.orbis_api.filetransfer.FileTransferManager;
import com.gildedgames.orbis_api.filetransfer.IFileTransferManager;

public class CommonProxy
{
	private IFileTransferManager fileTransferManager;

	public IFileTransferManager getFileTransferManager()
	{
		if (this.fileTransferManager == null)
		{
			this.fileTransferManager = new FileTransferManager();
		}

		return this.fileTransferManager;
	}
}
