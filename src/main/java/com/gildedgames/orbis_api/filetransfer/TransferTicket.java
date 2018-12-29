package com.gildedgames.orbis_api.filetransfer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class TransferTicket implements ITransferTicket
{
	private UUID id;

	private EntityPlayer client;

	private float progress;

	public TransferTicket(UUID id, EntityPlayer client)
	{
		this.id = id;
		this.client = client;
	}

	@Override
	public float getProgress()
	{
		return this.progress;
	}

	@Override
	public void setProgress(float progress)
	{
		this.progress = progress;
	}

	@Override
	public UUID getId()
	{
		return this.id;
	}

	@Override
	public EntityPlayer getClient()
	{
		return this.client;
	}

	@Override
	public void write(NBTTagCompound tag)
	{

	}

	@Override
	public void read(NBTTagCompound tag)
	{

	}
}
