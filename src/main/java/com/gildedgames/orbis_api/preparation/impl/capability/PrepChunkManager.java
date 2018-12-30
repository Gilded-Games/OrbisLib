package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.preparation.IChunkMaskTransformer;
import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import com.gildedgames.orbis_api.preparation.IPrepRegistryEntry;
import com.gildedgames.orbis_api.preparation.IPrepSectorData;
import com.gildedgames.orbis_api.preparation.impl.ChunkMask;
import com.gildedgames.orbis_api.util.ChunkMap;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PrepChunkManager<T> implements IPrepChunkManager<T>
{
	private World world;

	private IPrepRegistryEntry<T> registryEntry;

	private final ChunkMap<ChunkMask> chunkCache = new ChunkMap<>();

	private final ChunkMap<T> columnCache = new ChunkMap<T>();

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
		synchronized (this.chunkCache)
		{
			if (this.chunkCache.containsKey(chunkX, chunkZ))
			{
				return this.chunkCache.get(chunkX, chunkZ);
			}
		}

		T info = this.getChunkColumnMetadata(sectorData, chunkX, chunkZ);

		Biome[] biomes = new Biome[256];
		biomes = PrepChunkManager.this.world.getBiomeProvider().getBiomes(biomes, chunkX * 16, chunkZ * 16, 16, 16);

		ChunkMask mask = new ChunkMask();

		this.registryEntry
				.threadSafeGenerateMask(info, this.world, sectorData, biomes, mask, chunkX, chunkZ);

		this.chunkCache.put(chunkX, chunkZ, mask);

		return mask;
	}

	@Override
	public T getChunkColumnMetadata(IPrepSectorData sectorData, int chunkX, int chunkZ)
	{
		if (this.columnCache.containsKey(chunkX, chunkZ))
		{
			return this.columnCache.get(chunkX, chunkZ);
		}

		Biome[] biomes = new Biome[256];
		biomes = this.world.getBiomeProvider().getBiomes(biomes, chunkX * 16, chunkZ * 16, 16, 16);

		T info = this.registryEntry.generateChunkColumnInfo(PrepChunkManager.this.world, sectorData, biomes, chunkX, chunkZ);

		this.columnCache.put(chunkX, chunkZ, info);

		return info;
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
