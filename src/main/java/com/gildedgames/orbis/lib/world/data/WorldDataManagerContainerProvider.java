package com.gildedgames.orbis.lib.world.data;

import com.gildedgames.orbis.lib.OrbisLibCapabilities;
import net.minecraft.nbt.INBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;

public class WorldDataManagerContainerProvider implements ICapabilitySerializable<INBTBase>
{
	private final LazyOptional<IWorldDataManagerContainer> container;

	private final WorldDataManagerContainer.Storage storage = new WorldDataManagerContainer.Storage();

	public WorldDataManagerContainerProvider(World world)
	{
		this.container = LazyOptional.of(() -> new WorldDataManagerContainer(world));
	}

	@Nullable
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
	{
		if (capability == OrbisLibCapabilities.WORLD_DATA)
		{
			return this.container.cast();
		}

		return LazyOptional.empty();
	}

	private IWorldDataManagerContainer unwrap()
	{
		return this.container.orElseThrow(NoSuchElementException::new);
	}
	@Override
	public INBTBase serializeNBT()
	{
		return this.storage.writeNBT(OrbisLibCapabilities.WORLD_DATA, this.unwrap(), null);
	}

	@Override
	public void deserializeNBT(INBTBase nbt)
	{
		this.storage.readNBT(OrbisLibCapabilities.WORLD_DATA, this.unwrap(), null, nbt);
	}
}
