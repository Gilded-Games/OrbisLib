package com.gildedgames.orbis.lib.data.management.impl;

import com.gildedgames.orbis.lib.data.management.IProjectFilesystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProjectFilesystemLocal implements IProjectFilesystem
{
	@Override
	public InputStream getInputStream(String path)
	{
		try
		{
			return new FileInputStream(path);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
