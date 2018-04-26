package com.gildedgames.orbis_api.preparation.impl.capability;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.preparation.IPrepChunkManager;
import com.gildedgames.orbis_api.preparation.impl.util.ChunkPrep;
import com.gildedgames.orbis_api.util.PointSerializer;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PrepChunkManager implements IPrepChunkManager
{

	private World world;

	//TODO: Need to save these, since they often get modified by prep registry sources
	private LoadingCache<Long, Chunk> chunkCache;

	public PrepChunkManager()
	{

	}

	public PrepChunkManager(World world)
	{
		this.world = world;
	}

	private synchronized LoadingCache<Long, Chunk> getChunkCache()
	{
		if (this.chunkCache == null)
		{
			this.chunkCache = CacheBuilder.newBuilder()
					.maximumSize(40)
					.expireAfterWrite(10, TimeUnit.MINUTES)
					.build(new CacheLoader<Long, Chunk>()
						   {
							   @Override
							   public Chunk load(Long key)
							   {
								   int x = PointSerializer.x(key);
								   int y = PointSerializer.y(key);

								   Chunk chunk = PrepChunkManager.this.world.provider.createChunkGenerator().generateChunk(x, y);

								   return new ChunkPrep(chunk);
							   }
						   }
					);
		}

		return this.chunkCache;
	}

	@Override
	public World getWorld()
	{
		return this.world;
	}

	@Nullable
	@Override
	public Chunk getChunk(int chunkX, int chunkY)
	{
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
	public IBlockState getPreparedState(int x, int y, int z)
	{
		int chunkX = x >> 4;
		int chunkY = z >> 4;

		Chunk chunk = this.getChunk(chunkX, chunkY);

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
	public boolean setPreparedState(int x, int y, int z, IBlockState state)
	{
		int chunkX = x >> 4;
		int chunkY = z >> 4;

		Chunk chunk = this.getChunk(chunkX, chunkY);

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

		chunk.setBlockState(new BlockPos(xDif, y, zDif), state);

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
