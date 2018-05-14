package com.gildedgames.orbis_api.world.data;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class WorldDataManagerContainer implements IWorldDataManagerContainer
{
	private final IWorldDataManager manager;

	public WorldDataManagerContainer()
	{
		this.manager = null;
	}

	public WorldDataManagerContainer(IWorldDataManager manager)
	{
		this.manager = manager;
	}

	@Override
	public IWorldDataManager get()
	{
		return this.manager;
	}

	public static class Storage implements Capability.IStorage<IWorldDataManagerContainer>
	{
		@Nullable
		@Override
		public NBTBase writeNBT(final Capability<IWorldDataManagerContainer> capability, final IWorldDataManagerContainer instance, final EnumFacing side)
		{
			return null;
		}

		@Override
		public void readNBT(final Capability<IWorldDataManagerContainer> capability, final IWorldDataManagerContainer instance, final EnumFacing side,
				final NBTBase nbt)
		{

		}
	}
}
