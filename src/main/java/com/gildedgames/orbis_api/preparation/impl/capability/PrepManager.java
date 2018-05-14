package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.preparation.*;
import com.gildedgames.orbis_api.preparation.impl.PrepSectorAccessAsyncImpl;
import com.gildedgames.orbis_api.world.WorldObjectManager;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.concurrent.Executors;

public class PrepManager implements IPrepManager
{
	private final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

	private final World world;

	private final IPrepRegistryEntry registry;

	private final Long2ObjectOpenHashMap<ListenableFuture<IPrepSectorData>> sectorsPreparing = new Long2ObjectOpenHashMap<>();

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
	public ListenableFuture<IPrepSectorData> createSector(int sectorX, int sectorZ)
	{
		long key = ChunkPos.asLong(sectorX, sectorZ);

		synchronized (this.sectorsPreparing)
		{
			if (this.sectorsPreparing.containsKey(key))
			{
				return this.sectorsPreparing.get(key);
			}
		}

		SettableFuture<IPrepSectorData> future = SettableFuture.create();

		synchronized (this.sectorsPreparing)
		{
			this.sectorsPreparing.put(key, future);
		}

		this.service.execute(() -> {
			long seed = WorldObjectManager.getWorldSeed(this.world.provider.getDimension())
					^ ((long) sectorX * 341873128712L + (long) sectorZ * 132897987541L);

			IPrepSectorData data = this.registry.createData(this.world, seed, sectorX, sectorZ);

			this.registry.postSectorDataCreate(this.world, data, this.chunkManager);

			synchronized (this.sectorsPreparing)
			{
				this.sectorsPreparing.remove(key);
			}

			future.set(data);
		});

		return future;
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
