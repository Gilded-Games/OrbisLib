package com.gildedgames.orbis.lib.world.instances;

import com.gildedgames.orbis.lib.OrbisLibCapabilities;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.NoSuchElementException;

public class PlayerInstancesProvider implements ICapabilitySerializable<INBT>
{
	private final PlayerInstances.Storage storage = new PlayerInstances.Storage();

	private LazyOptional<PlayerInstances> capability;

	public PlayerInstancesProvider()
	{
		this.capability = LazyOptional.of(PlayerInstances::new);
	}

	@Override
	public <T> LazyOptional<T> getCapability(final Capability<T> capability, final Direction facing)
	{
		if (capability == OrbisLibCapabilities.PLAYER_INSTANCES)
		{
			return this.capability.cast();
		}

		return LazyOptional.empty();
	}

	private PlayerInstances unwrap()
	{
		return this.capability.orElseThrow(NoSuchElementException::new);
	}

	@Override
	public INBT serializeNBT()
	{
		return this.storage.writeNBT(OrbisLibCapabilities.PLAYER_INSTANCES, this.unwrap(), null);
	}

	@Override
	public void deserializeNBT(final INBT nbt)
	{
		this.storage.readNBT(OrbisLibCapabilities.PLAYER_INSTANCES, this.unwrap(), null, nbt);
	}

}
