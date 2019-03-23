package com.gildedgames.orbis.lib.data.management.impl;

import com.gildedgames.orbis.lib.data.management.IProjectMetadata;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.util.mc.IText;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A basic concrete implementation of IDataMetadata.
 * Can be decorated with ProjectMetadataDecorator.
 */
public class ProjectMetadata implements IProjectMetadata
{
	private LocalDateTime lastChanged = LocalDateTime.now();

	private List<IText> display;

	private boolean downloaded, downloading;

	public ProjectMetadata()
	{
		this.display = Lists.newArrayList();
	}

	private ProjectMetadata(LocalDateTime lastChanged, List<IText> display, boolean downloaded, boolean downloading)
	{
		this.lastChanged = lastChanged;
		this.display = display;
		this.downloaded = downloaded;
		this.downloading = downloading;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.setList("display", this.display);

		tag.putBoolean("downloaded", this.downloaded);
		tag.putBoolean("downloading", this.downloading);

		funnel.setDate("lastChanged", this.lastChanged);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.display = funnel.getList("display");

		this.downloaded = tag.getBoolean("downloaded");
		this.downloading = tag.getBoolean("downloading");

		this.lastChanged = funnel.getDate("lastChanged");
	}

	@Override
	public List<IText> getMetadataDisplay()
	{
		return this.display;
	}

	@Override
	public boolean isDownloaded()
	{
		return this.downloaded;
	}

	@Override
	public void setDownloaded(final boolean downloaded)
	{
		this.downloaded = downloaded;
	}

	@Override
	public boolean isDownloading()
	{
		return this.downloading;
	}

	@Override
	public void setDownloading(final boolean downloading)
	{
		this.downloading = downloading;
	}

	@Override
	public LocalDateTime getLastChanged()
	{
		return this.lastChanged;
	}

	@Override
	public void setLastChanged(final LocalDateTime lastChanged)
	{
		this.lastChanged = lastChanged;
	}

}
