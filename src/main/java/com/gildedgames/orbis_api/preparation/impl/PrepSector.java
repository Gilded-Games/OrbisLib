package com.gildedgames.orbis_api.preparation.impl;

import com.gildedgames.orbis_api.preparation.IPrepSector;
import com.gildedgames.orbis_api.preparation.IPrepSectorData;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import net.minecraft.util.math.ChunkPos;

public class PrepSector implements IPrepSector
{
	private final TLongSet watching = new TLongHashSet();

	private IPrepSectorData data;

	private boolean dirty;

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
	public boolean isDirty()
	{
		return this.dirty;
	}

	@Override
	public void markDirty()
	{
		this.dirty = true;
	}

	@Override
	public void markClean()
	{
		this.dirty = false;
	}

	@Override
	public void addWatchingChunk(final int chunkX, final int chunkZ)
	{
		this.watching.add(ChunkPos.asLong(chunkX, chunkZ));
	}

	@Override
	public void removeWatchingChunk(final int chunkX, final int chunkZ)
	{
		this.watching.remove(ChunkPos.asLong(chunkX, chunkZ));
	}

	@Override
	public boolean hasWatchers()
	{
		return this.watching.isEmpty();
	}
}
