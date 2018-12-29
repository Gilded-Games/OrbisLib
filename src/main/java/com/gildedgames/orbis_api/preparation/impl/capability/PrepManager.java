package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.OrbisLib;
import com.gildedgames.orbis_api.preparation.*;
import com.gildedgames.orbis_api.preparation.impl.PrepSectorAccessClientImpl;
import com.gildedgames.orbis_api.preparation.impl.PrepSectorAccessServerImpl;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PrepManager implements IPrepManager
{
	private final World world;

	private final IPrepRegistryEntry registry;

	private IPrepSectorAccess access;

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

		if (world.isRemote)
		{
			this.access = new PrepSectorAccessClientImpl(this.world, this.registry);
		}
		else
		{
			this.access = new PrepSectorAccessServerImpl(this.world, this.registry, this, OrbisLib.services().getWorldDataManager(world));
		}

		this.chunkManager = new PrepChunkManager(this.world, this.registry);
	}

	@Override
	public IPrepRegistryEntry getRegistryEntry()
	{
		return this.registry;
	}

	@Override
	public IPrepSectorAccessClient getClientAccess()
	{
		return (IPrepSectorAccessClient) this.access;
	}

	@Override
	public IPrepSectorAccess getAccess()
	{
		return this.access;
	}

	@Override
	public IPrepSectorData createSector(int sectorX, int sectorZ)
	{
		long seed = this.world.getSeed() ^ ((long) sectorX * 341873128712L + (long) sectorZ * 132897987541L);

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
