package com.gildedgames.orbis.lib.world.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.io.File;

public class WorldDataManagerContainer implements IWorldDataManagerContainer
{
	private final ServerWorld world;

	private IWorldDataManager manager;

	private WorldDataStorageMethod lastStorageMethod;

	public WorldDataManagerContainer()
	{
		this.world = null;
	}

	public WorldDataManagerContainer(ServerWorld world)
	{
		this.world = world;
	}

	@Override
	public IWorldDataManager get()
	{
		if (this.manager == null && this.world != null)
		{
			this.manager = this.createDataManager(this.world);
		}

		return this.manager;
	}

	@Override
	public WorldDataStorageMethod getLastStorageMethod()
	{
		return this.lastStorageMethod;
	}

	@Override
	public void setLastStorageMethod(WorldDataStorageMethod method)
	{
		this.lastStorageMethod = method;
	}

	private IWorldDataManager createDataManager(ServerWorld world)
	{
		if (this.lastStorageMethod == null)
		{
			this.lastStorageMethod = WorldDataStorageMethod.FLAT;
		}

		File dir = new File(world.getDimension().getType().getDirectory(world.getSaveHandler().getWorldDirectory()) ,"data/orbis");

		if (this.lastStorageMethod == WorldDataStorageMethod.FLAT)
		{
			return new WorldDataManagerFlat(dir);
		}
		else
		{
			throw new IllegalStateException("Don't know how to initialize '" + this.lastStorageMethod.serializedName + "' storage");
		}
	}

	public static class Storage implements Capability.IStorage<IWorldDataManagerContainer>
	{
		@Nullable
		@Override
		public INBT writeNBT(final Capability<IWorldDataManagerContainer> capability, final IWorldDataManagerContainer instance, final Direction side)
		{
			CompoundNBT tag = new CompoundNBT();

			if (instance.getLastStorageMethod() != null)
			{
				tag.putString("LastStorageMethod", instance.getLastStorageMethod().serializedName);
			}

			return tag;
		}

		@Override
		public void readNBT(final Capability<IWorldDataManagerContainer> capability, final IWorldDataManagerContainer instance, final Direction side, final INBT nbt)
		{
			String name = ((CompoundNBT) nbt).getString("LastStorageMethod");

			for (WorldDataStorageMethod method : WorldDataStorageMethod.values())
			{
				if (method.serializedName.equals(name))
				{
					instance.setLastStorageMethod(method);

					break;
				}
			}
		}
	}
}
