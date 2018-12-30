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

public class PrepChunkManager implements IPrepChunkManager
{
	private World world;

	private IPrepRegistryEntry registryEntry;

	private final ChunkMap<ChunkMask> chunkCache = new ChunkMap<>();

	public PrepChunkManager()
	{

	}

	public PrepChunkManager(World world, IPrepRegistryEntry registryEntry)
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
	public ChunkMask getChunk(IPrepSectorData sectorData, int chunkX, int chunkY)
	{
		synchronized (this.chunkCache)
		{
			if (this.chunkCache.containsKey(chunkX, chunkY))
			{
				return this.chunkCache.get(chunkX, chunkY);
			}
		}

		Biome[] biomes = new Biome[256];
		biomes = PrepChunkManager.this.world.getBiomeProvider().getBiomes(biomes, chunkX * 16, chunkY * 16, 16, 16);

		ChunkMask mask = new ChunkMask();

		PrepChunkManager.this.registryEntry
				.threadSafeGenerateMask(PrepChunkManager.this.world, sectorData, biomes, mask, chunkX, chunkY);

		synchronized (this.chunkCache)
		{
			this.chunkCache.put(chunkX, chunkY, mask);
		}

		return mask;
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
