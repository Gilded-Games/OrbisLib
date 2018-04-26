package com.gildedgames.orbis_api.preparation.impl.util;

import com.gildedgames.orbis_api.util.mc.ChunkDecorator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;

public class ChunkPrep extends ChunkDecorator
{
	public ChunkPrep(Chunk chunk)
	{
		super(chunk);
	}

	@Override
	public void generateSkylightMap()
	{
		//NO-OP
	}

	@Override
	public void setLightFor(EnumSkyBlock type, BlockPos pos, int value)
	{
		//NO-OP
	}

	@Override
	public void addEntity(Entity entityIn)
	{
		//NO-OP
	}

	@Override
	public void removeEntity(Entity entityIn)
	{
		//NO-OP
	}

	@Override
	public void removeEntityAtIndex(Entity entityIn, int index)
	{
		//NO-OP
	}

	@Override
	public void onTick(boolean skipRecheckGaps)
	{
		//NO-OP
	}

	@Override
	public void checkLight()
	{
		//NO-OP
	}

	@Override
	public void resetRelightChecks()
	{
		//NO-OP
	}

	@Override
	public void enqueueRelightChecks()
	{
		//NO-OP
	}
}
