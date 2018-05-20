package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.preparation.*;
import com.gildedgames.orbis_api.preparation.impl.PrepSectorAccessAsyncImpl;
import com.gildedgames.orbis_api.world.WorldObjectManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PrepManager implements IPrepManager
{
	private final World world;

	private final IPrepRegistryEntry registry;

	private IPrepSectorAccessAsync access;

	private IPrepChunkManager chunkManager;

	public PrepManager()
	{
		this.world = null;
		this.registry = null;
	}

	public PrepManager(World world, IPrepRegistryEntry registry)
	{
		this.world = world;
		this.registry = registry;

		this.access = new PrepSectorAccessAsyncImpl(this.world, this.registry, this, OrbisAPI.services().getWorldDataManager(world));
		this.chunkManager = new PrepChunkManager(this.world, this.registry);
	}

	@Override
	public IPrepRegistryEntry getRegistryEntry()
	{
		return this.registry;
	}

	@Override
	public IPrepChunkManager getChunkManager()
	{
		return this.chunkManager;
	}

	@Override
	public IPrepSectorAccessAsync access()
	{
		return this.access;
	}

	@Override
	public IPrepSectorData createSector(int sectorX, int sectorZ)
	{
		long seed = WorldObjectManager.getWorldSeed(this.world.provider.getDimension())
				^ ((long) sectorX * 341873128712L + (long) sectorZ * 132897987541L);

		return this.registry.createData(this.world, seed, sectorX, sectorZ);
	}

	@Override
	public void decorateSectorData(IPrepSectorData data)
	{
		this.registry.postSectorDataCreate(this.world, data, this.chunkManager);
	}

	public static class Storage implements Capability.IStorage<IPrepManager>
	{
		@Nullable
		@Override
		public NBTBase writeNBT(final Capability<IPrepManager> capability, final IPrepManager instance, final EnumFacing side)
		{
			return null;
		}

		@Override
		public void readNBT(final Capability<IPrepManager> capability, final IPrepManager instance, final EnumFacing side, final NBTBase nbt)
		{

		}
	}
}
