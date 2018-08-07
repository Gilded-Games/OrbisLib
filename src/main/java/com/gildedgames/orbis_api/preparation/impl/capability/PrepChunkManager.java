package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.preparation.IChunkMaskTransformer;
import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import com.gildedgames.orbis_api.preparation.IPrepRegistryEntry;
import com.gildedgames.orbis_api.preparation.IPrepSectorData;
import com.gildedgames.orbis_api.preparation.impl.ChunkMask;
import com.gildedgames.orbis_api.util.PointSerializer;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PrepChunkManager implements IPrepChunkManager
{

	private World world;

	private IPrepRegistryEntry registryEntry;

	private ThreadLocal<IPrepSectorData> currentSectorData = new ThreadLocal<>();

	private final LoadingCache<Long, ChunkMask> chunkCache = CacheBuilder.newBuilder()
			.maximumSize(512)
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.build(new CacheLoader<Long, ChunkMask>()
				   {
					   @Override
					   public ChunkMask load(Long key)
					   {
						   int x = PointSerializer.x(key);
						   int y = PointSerializer.y(key);

						   Biome[] biomes = new Biome[256];
						   biomes = PrepChunkManager.this.world.getBiomeProvider().getBiomes(biomes, x * 16, y * 16, 16, 16);

						   ChunkMask mask = new ChunkMask();

						   PrepChunkManager.this.registryEntry
								   .threadSafeGenerateMask(PrepChunkManager.this.world, PrepChunkManager.this.currentSectorData.get(), biomes, mask, x, y);

						   return mask;
					   }
				   }
			);

	public PrepChunkManager()
	{

	}

	public PrepChunkManager(World world, IPrepRegistryEntry registryEntry)
	{
		this.world = world;
		this.registryEntry = registryEntry;
	}

	private LoadingCache<Long, ChunkMask> getChunkCache()
	{
		return this.chunkCache;
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
		this.currentSectorData.set(sectorData);

		long hash = PointSerializer.toLong(chunkX, chunkY);

		try
		{
			return this.getChunkCache().get(hash);
		}
		catch (ExecutionException e)
		{
			OrbisAPI.LOGGER.info("Couldn't find prep chunk at: (x, " + chunkX + ". y, " + chunkY);
		}

		return null;
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
