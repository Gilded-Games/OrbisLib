package com.gildedgames.orbis.lib.world.data;

import com.gildedgames.orbis.lib.OrbisLibCapabilities;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldDataManagerContainerProvider implements ICapabilitySerializable<NBTBase>
{
	private final IWorldDataManagerContainer container;

	private final WorldDataManagerContainer.Storage storage = new WorldDataManagerContainer.Storage();

	public WorldDataManagerContainerProvider(World world)
	{
		this.container = new WorldDataManagerContainer(world);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == OrbisLibCapabilities.WORLD_DATA;
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

	@Override
	public NBTBase serializeNBT()
	{
		return this.storage.writeNBT(OrbisLibCapabilities.WORLD_DATA, this.container, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt)
	{
		this.storage.readNBT(OrbisLibCapabilities.WORLD_DATA, this.container, null, nbt);
	}
}
