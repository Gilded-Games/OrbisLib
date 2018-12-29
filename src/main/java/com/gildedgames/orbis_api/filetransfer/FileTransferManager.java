package com.gildedgames.orbis_api.filetransfer;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class FileTransferManager implements IFileTransferManager
{
	private Map<ResourceLocation, Supplier<IFileTransferTracker>> idToSupplier = Maps.newHashMap();

	private Map<ResourceLocation, IFileTransferTracker> idToTracker = Maps.newHashMap();

	public FileTransferManager()
	{

	}

	@Override
	public void registerTracker(ResourceLocation id, Supplier<IFileTransferTracker> tracker)
	{
		this.idToSupplier.put(id, tracker);
	}

	@Override
	public Optional<IFileTransferTracker> getTracker(ResourceLocation id)
	{
		return Optional.ofNullable(this.idToTracker.get(id));
	}

	@Override
	public void close()
	{
		this.idToTracker.clear();
	}

	@Override
	public void open()
	{
		for (Map.Entry<ResourceLocation, Supplier<IFileTransferTracker>> entry : this.idToSupplier.entrySet())
		{
			this.idToTracker.put(entry.getKey(), entry.getValue().get());
		}
	}
}
