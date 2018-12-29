package com.gildedgames.orbis_api.filetransfer;

import com.gildedgames.orbis_api.OrbisLib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileReceiver implements IFileReceiver
{
	private File baseDirectory;

	public FileReceiver(File baseDirectory)
	{
		this.baseDirectory = baseDirectory;
	}

	@Override
	public void receive(String relativePath, byte[] data)
	{
		File file = new File(this.baseDirectory, relativePath);

		file.mkdirs();

		try (FileOutputStream output = new FileOutputStream(file))
		{
			output.write(data);
			OrbisLib.LOGGER.info("Successfully transfered file: ", relativePath);
		}
		catch (IOException e)
		{
			OrbisLib.LOGGER.info(e);
		}
	}
}
