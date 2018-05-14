package com.gildedgames.orbis_api.world.data;

import com.gildedgames.orbis_api.OrbisAPICapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public class WorldDataManagerContainerProvider implements ICapabilityProvider
{
	private final IWorldDataManagerContainer container;

	public WorldDataManagerContainerProvider(World world)
	{
		this.container = new WorldDataManagerContainer(this.createDataManager(world));
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == OrbisAPICapabilities.WORLD_DATA;
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
	{
		if (this.hasCapability(capability, facing))
		{
			return (T) this.container;
		}

		return null;
	}

	private IWorldDataManager createDataManager(World world)
	{
		if (world.isRemote)
		{
			return new WorldDataManagerNOOP();
		}
		else
		{
			File dir = new File(world.getSaveHandler().getWorldDirectory(),
					(world.provider.getSaveFolder() == null ? "" : world.provider.getSaveFolder()) + "/data/orbis/");

			return new WorldDataManagerLmdb(dir);
		}
	}
}
