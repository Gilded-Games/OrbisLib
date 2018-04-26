package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.OrbisAPICapabilities;
import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrepChunkManagerStorageProvider implements ICapabilityProvider
{
	private final IPrepChunkManager chunkManager;

	public PrepChunkManagerStorageProvider(World world)
	{
		this.chunkManager = new PrepChunkManager(world);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == OrbisAPICapabilities.PREP_CHUNK_MANAGER;
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
	{
		if (this.hasCapability(capability, facing))
		{
			return (T) this.chunkManager;
		}

		return null;
	}
}