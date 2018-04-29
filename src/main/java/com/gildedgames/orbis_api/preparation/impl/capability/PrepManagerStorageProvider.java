package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.OrbisAPICapabilities;
import com.gildedgames.orbis_api.preparation.IPrepManager;
import com.gildedgames.orbis_api.preparation.IPrepRegistryEntry;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public class PrepManagerStorageProvider implements ICapabilityProvider
{
	private final IPrepManager manager;

	public PrepManagerStorageProvider(World world, IPrepRegistryEntry entry)
	{
		File dir = new File(world.getSaveHandler().getWorldDirectory(),
				world.provider.getSaveFolder() + "/data/orbis/" + entry.getUniqueId().getResourceDomain() + "/" + entry.getUniqueId().getResourcePath()
						+ "/");

		this.manager = new PrepManager(world, dir, entry);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == OrbisAPICapabilities.PREP_MANAGER;
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
	{
		if (this.hasCapability(capability, facing))
		{
			return (T) this.manager;
		}

		return null;
	}
}