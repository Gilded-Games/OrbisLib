package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.preparation.*;
import com.gildedgames.orbis_api.preparation.impl.ChunkMask;
import com.gildedgames.orbis_api.util.ChunkMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PrepChunkManager<T extends IChunkColumnInfo> implements IPrepChunkManager<T>
{
	private World world;

	private IPrepRegistryEntry<T> registryEntry;

	private final Long2ObjectOpenHashMap<ChunkMask> chunkCache = new Long2ObjectOpenHashMap<>();

	private final ChunkMap<T> columnCache = new ChunkMap<>();

	public PrepChunkManager()
	{

	}

	public PrepChunkManager(World world, IPrepRegistryEntry<T> registryEntry)
	{
		this.world = world;
		this.registryEntry = registryEntry;
	}

	@Override
	public World getWorld()
	{
		return this.world;
	}

	@Nullable
	@Override
	public ChunkMask getChunk(IPrepSectorData sectorData, int chunkX, int chunkZ)
	{
		long key = this.getChunkKey(chunkX, chunkZ);

		ChunkMask mask = this.chunkCache.get(key);

		if (mask != null)
		{
			return mask;
		}

		T info = this.getChunkColumnInfo(sectorData, chunkX, chunkZ);

		mask = new ChunkMask(chunkX, chunkZ);

		this.registryEntry.threadSafeGenerateMask(info, this.world, sectorData, mask, chunkX, chunkZ);

		this.chunkCache.put(key, mask);

		return mask;
	}

	@Override
	public T getChunkColumnInfo(IPrepSectorData sectorData, int chunkX, int chunkZ)
	{
		T info = this.columnCache.get(chunkX, chunkZ);

		if (info != null)
		{
			return info;
		}

		info = this.registryEntry.generateChunkColumnInfo(this.world, sectorData, chunkX, chunkZ);

		this.columnCache.put(chunkX, chunkZ, info);

		return info;
	}

	private long getChunkKey(int x, int z)
	{
		return ChunkPos.asLong(x, z);
	}

	@Override
	public IChunkMaskTransformer createMaskTransformer()
	{
		return this.registryEntry.createMaskTransformer();
	}

	public static class Storage implements Capability.IStorage<IPrepChunkManager>
	{
		@Nullable
		@Override
		public NBTBase writeNBT(final Capability<IPrepChunkManager> capability, final IPrepChunkManager instance, final EnumFacing side)
		{
			return null;
		}

		@Override
		public void readNBT(final Capability<IPrepChunkManager> capability, final IPrepChunkManager instance, final EnumFacing side, final NBTBase nbt)
		{

		}
	}
}
