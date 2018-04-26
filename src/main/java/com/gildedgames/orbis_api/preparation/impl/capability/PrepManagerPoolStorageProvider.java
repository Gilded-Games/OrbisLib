package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.OrbisAPICapabilities;
import com.gildedgames.orbis_api.preparation.IPrepManagerPool;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrepManagerPoolStorageProvider implements ICapabilityProvider
{
	private final IPrepManagerPool pool;

	public PrepManagerPoolStorageProvider(World world)
	{
		this.pool = new PrepManagerPool(world, OrbisAPI.sectors());
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == OrbisAPICapabilities.PREP_MANAGER_POOL;
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
	{
		if (this.hasCapability(capability, facing))
		{
			return (T) this.pool;
		}

		return null;
	}
}