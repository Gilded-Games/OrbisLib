package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.preparation.IPrepSector;
import com.gildedgames.orbis_api.preparation.IPrepSectorData;
import gnu.trove.set.TIntSet;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TIntHashSet;
import gnu.trove.set.hash.TLongHashSet;
import net.minecraft.util.math.ChunkPos;

public class PrepSector implements IPrepSector
{
	private final TLongSet watchingChunks = new TLongHashSet();

	private final TIntSet watchingPlayers = new TIntHashSet();

	private IPrepSectorData data;

	public PrepSector(IPrepSectorData data)
	{
		this.data = data;
	}

	@Override
	public IPrepSectorData getData()
	{
		return this.data;
	}

	@Override
	public boolean addWatchingChunk(final int chunkX, final int chunkZ)
	{
		return this.watchingChunks.add(ChunkPos.asLong(chunkX, chunkZ));
	}

	@Override
	public boolean removeWatchingChunk(final int chunkX, final int chunkZ)
	{
		return this.watchingChunks.remove(ChunkPos.asLong(chunkX, chunkZ));
	}

	@Override
	public void addWatchingPlayer(int entityId)
	{
		this.watchingPlayers.add(entityId);
	}

	@Override
	public void removeWatchingPlayer(int entityId)
	{
		this.watchingPlayers.remove(entityId);
	}

	@Override
	public boolean hasWatchers()
	{
		return !this.watchingChunks.isEmpty() && !this.watchingPlayers.isEmpty();
	}
}
