package com.gildedgames.orbis_api.data.management.impl;

import com.gildedgames.orbis_api.data.management.IProjectFilesystem;

import java.io.InputStream;

public class ProjectFilesystemClasspath implements IProjectFilesystem
{
	@Override
	public InputStream getInputStream(String path)
	{
		return OrbisProject.class.getResourceAsStream(path);
	}
}
