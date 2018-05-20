package com.gildedgames.orbis_api.world.data;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.io.File;

public class WorldDataManagerContainer implements IWorldDataManagerContainer
{
	private final World world;

	private IWorldDataManager manager;

	private WorldDataStorageMethod lastStorageMethod;

	public WorldDataManagerContainer()
	{
		this.world = null;
	}

	public WorldDataManagerContainer(World world)
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

	private IWorldDataManager createDataManager(World world)
	{
		if (world.isRemote)
		{
			return new WorldDataManagerNOOP();
		}
		else
		{
			if (this.lastStorageMethod == null)
			{
				this.lastStorageMethod = WorldDataStorageMethod.FLAT;
			}

			File dir = new File(world.getSaveHandler().getWorldDirectory(), (world.provider.getSaveFolder() == null ? "" : world.provider.getSaveFolder()) + "/data/orbis/");

			if (this.lastStorageMethod == WorldDataStorageMethod.FLAT)
			{
				return new WorldDataManagerFlat(dir);
			}
			else
			{
				throw new IllegalStateException("Don't know how to initialize '" + this.lastStorageMethod.serializedName + "' storage");
			}
		}
	}

	public static class Storage implements Capability.IStorage<IWorldDataManagerContainer>
	{
		@Nullable
		@Override
		public NBTBase writeNBT(final Capability<IWorldDataManagerContainer> capability, final IWorldDataManagerContainer instance, final EnumFacing side)
		{
			NBTTagCompound tag = new NBTTagCompound();

			if (instance.getLastStorageMethod() != null)
			{
				tag.setString("LastStorageMethod", instance.getLastStorageMethod().serializedName);
			}

			return tag;
		}

		@Override
		public void readNBT(final Capability<IWorldDataManagerContainer> capability, final IWorldDataManagerContainer instance, final EnumFacing side, final NBTBase nbt)
		{
			String name = ((NBTTagCompound) nbt).getString("LastStorageMethod");

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
