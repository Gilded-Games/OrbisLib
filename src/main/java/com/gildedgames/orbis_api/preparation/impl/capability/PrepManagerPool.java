package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.preparation.IPrepManager;
import com.gildedgames.orbis_api.preparation.IPrepManagerPool;
import com.gildedgames.orbis_api.preparation.IPrepRegistry;
import com.gildedgames.orbis_api.preparation.IPrepRegistryEntry;
import com.gildedgames.orbis_api.preparation.impl.PrepManager;
import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.Map;

public class PrepManagerPool implements IPrepManagerPool
{
	private Map<ResourceLocation, IPrepManager> idToManager = Maps.newHashMap();

	public PrepManagerPool()
	{

	}

	public PrepManagerPool(World world, IPrepRegistry registry)
	{
		for (IPrepRegistryEntry entry : registry.getEntries())
		{
			if (entry.shouldAttachTo(world))
			{
				File dir = new File(world.getSaveHandler().getWorldDirectory(),
						world.provider.getSaveFolder() + "/data/orbis/" + entry.getUniqueId().getResourceDomain() + "/" + entry.getUniqueId().getResourcePath()
								+ "/");

				this.idToManager.put(entry.getUniqueId(), new PrepManager(world, dir, entry));
			}
		}
	}

	@Override
	public IPrepManager get(ResourceLocation registryId)
	{
		return this.idToManager.get(registryId);
	}

	@Override
	public Collection<IPrepManager> getManagers()
	{
		return this.idToManager.values();
	}

	public static class Storage implements Capability.IStorage<IPrepManagerPool>
	{
		@Nullable
		@Override
		public NBTBase writeNBT(final Capability<IPrepManagerPool> capability, final IPrepManagerPool instance, final EnumFacing side)
		{
			return null;
		}

		@Override
		public void readNBT(final Capability<IPrepManagerPool> capability, final IPrepManagerPool instance, final EnumFacing side, final NBTBase nbt)
		{

		}
	}
}
