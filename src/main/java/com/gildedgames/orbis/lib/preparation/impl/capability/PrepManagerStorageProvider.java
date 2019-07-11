package com.gildedgames.orbis.lib.preparation.impl.capability;

import com.gildedgames.orbis.lib.OrbisLibCapabilities;
import com.gildedgames.orbis.lib.preparation.IPrepManager;
import com.gildedgames.orbis.lib.preparation.IPrepRegistryEntry;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrepManagerStorageProvider implements ICapabilityProvider
{
	private LazyOptional<IPrepManager> value;

	public PrepManagerStorageProvider(World world, IPrepRegistryEntry entry)
	{
		this.value = LazyOptional.of(() -> new PrepManager(world, entry));
	}

	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
	{
		return capability == OrbisLibCapabilities.PREP_MANAGER ? this.value.cast() : LazyOptional.empty();
	}
}