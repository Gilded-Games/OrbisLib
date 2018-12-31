package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.preparation.*;
import com.gildedgames.orbis_api.preparation.impl.ChunkMask;
import com.gildedgames.orbis_api.util.ChunkMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
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
	public ChunkMask getChunk(IPrepSectorData sectorData, int chunkX, int chunkY, int chunkZ)
	{
		long key = this.getChunkKey(chunkX, chunkY, chunkZ);

		if (this.chunkCache.containsKey(key))
		{
			return this.chunkCache.get(key);
		}

		T info = this.getChunkColumnInfo(sectorData, chunkX, chunkZ);

		Biome[] biomes = new Biome[256];
		biomes = this.world.getBiomeProvider().getBiomes(biomes, chunkX * 16, chunkZ * 16, 16, 16);

		ChunkMask mask = new ChunkMask(chunkX, chunkY, chunkZ);

		this.registryEntry.threadSafeGenerateMask(info, this.world, sectorData, biomes, mask, chunkX, chunkY, chunkZ);

		this.chunkCache.put(key, mask);

		return mask;
	}

	@Override
	public T getChunkColumnInfo(IPrepSectorData sectorData, int chunkX, int chunkZ)
	{
		if (this.columnCache.containsKey(chunkX, chunkZ))
		{
			return this.columnCache.get(chunkX, chunkZ);
		}

		Biome[] biomes = new Biome[256];
		biomes = this.world.getBiomeProvider().getBiomes(biomes, chunkX * 16, chunkZ * 16, 16, 16);

		T info = this.registryEntry.generateChunkColumnInfo(this.world, sectorData, biomes, chunkX, chunkZ);

		this.columnCache.put(chunkX, chunkZ, info);

		return info;
	}

	private long getChunkKey(int x, int y, int z)
	{
		long key = (x & 0xFFFFL) << 48;
		key |= (z & 0xFFFFL) << 32;
		key |= (y & 0xFFFFL) << 16;

		return key;
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
