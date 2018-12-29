package com.gildedgames.orbis_api.filetransfer;

import net.minecraft.util.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

public interface IFileTransferManager
{
	void registerTracker(ResourceLocation id, Supplier<IFileTransferTracker> tracker);

	Optional<IFileTransferTracker> getTracker(ResourceLocation id);

	void close();

	void open();
}
