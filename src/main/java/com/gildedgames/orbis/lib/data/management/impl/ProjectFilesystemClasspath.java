package com.gildedgames.orbis.lib.data.management.impl;

import com.gildedgames.orbis.lib.data.management.IProjectFilesystem;

import java.io.InputStream;

public class ProjectFilesystemClasspath implements IProjectFilesystem
{
	@Override
	public InputStream getInputStream(String path)
	{
		return OrbisProject.class.getResourceAsStream(path);
	}
}
