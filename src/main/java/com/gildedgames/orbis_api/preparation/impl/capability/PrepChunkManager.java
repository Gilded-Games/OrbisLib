package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import com.gildedgames.orbis_api.preparation.IPrepRegistryEntry;
import com.gildedgames.orbis_api.preparation.IPrepSectorData;
import com.gildedgames.orbis_api.util.PointSerializer;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PrepChunkManager implements IPrepChunkManager
{

	private World world;

	private IPrepRegistryEntry registryEntry;

	private ThreadLocal<IPrepSectorData> currentSectorData = new ThreadLocal<>();

	//TODO: Need to save these, since they often get modified by prep registry sources
	private LoadingCache<Long, ChunkPrimer> chunkCache = CacheBuilder.newBuilder()
			.maximumSize(40)
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.build(new CacheLoader<Long, ChunkPrimer>()
				   {
					   @Override
					   public ChunkPrimer load(Long key)
					   {
						   int x = PointSerializer.x(key);
						   int y = PointSerializer.y(key);

						   Biome[] biomes = new Biome[256];

						   biomes = PrepChunkManager.this.world.getBiomeProvider().getBiomes(biomes, x * 16, y * 16, 16, 16);

						   ChunkPrimer primer = new ChunkPrimer();

						   PrepChunkManager.this.registryEntry
								   .threadSafeGenerateChunk(PrepChunkManager.this.world, PrepChunkManager.this.currentSectorData.get(), biomes, primer, x, y);

						   return primer;
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

	private LoadingCache<Long, ChunkPrimer> getChunkCache()
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
	public ChunkPrimer getChunk(IPrepSectorData sectorData, int chunkX, int chunkY)
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
	public IBlockState getPreparedState(IPrepSectorData sectorData, int x, int y, int z)
	{
		this.currentSectorData.set(sectorData);

		int chunkX = x >> 4;
		int chunkY = z >> 4;

		ChunkPrimer chunk = this.getChunk(sectorData, chunkX, chunkY);

		int xDif = x % 16;
		int zDif = z % 16;

		if (xDif < 0)
		{
			xDif = 16 - Math.abs(xDif);
		}

		if (zDif < 0)
		{
			zDif = 16 - Math.abs(zDif);
		}

		return chunk.getBlockState(xDif, y, zDif);
	}

	@Override
	public boolean setPreparedState(IPrepSectorData sectorData, int x, int y, int z, IBlockState state)
	{
		this.currentSectorData.set(sectorData);

		int chunkX = x >> 4;
		int chunkY = z >> 4;

		ChunkPrimer chunk = this.getChunk(sectorData, chunkX, chunkY);

		if (chunk == null)
		{
			return false;
		}

		int xDif = x % 16;
		int zDif = z % 16;

		if (xDif < 0)
		{
			xDif = 16 - Math.abs(xDif);
		}

		if (zDif < 0)
		{
			zDif = 16 - Math.abs(zDif);
		}

		chunk.setBlockState(xDif, y, zDif, state);

		return true;
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
