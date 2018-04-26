package com.gildedgames.orbis_api.preparation.impl.util;

import com.gildedgames.orbis_api.util.mc.ChunkDecorator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;

public class ChunkPrep extends ChunkDecorator
{
	public ChunkPrep(Chunk chunk)
	{
		super(chunk);
	}

	@Nullable
	@Override
	public IBlockState setBlockState(BlockPos pos, IBlockState state)
	{
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		int l = k << 4 | i;

		int[] precipitationHeightMap = ObfuscationReflectionHelper.getPrivateValue(Chunk.class, this.c, "precipitationHeightMap");

		if (j >= precipitationHeightMap[l] - 1)
		{
			precipitationHeightMap[l] = -999;
		}

		IBlockState iblockstate = this.getBlockState(pos);

		if (iblockstate == state)
		{
			return null;
		}
		else
		{
			Block block = state.getBlock();

			ExtendedBlockStorage extendedblockstorage = this.c.getBlockStorageArray()[j >> 4];

			if (extendedblockstorage == NULL_BLOCK_STORAGE)
			{
				if (block == Blocks.AIR)
				{
					return null;
				}

				extendedblockstorage = new ExtendedBlockStorage(j >> 4 << 4, this.c.getWorld().provider.hasSkyLight());
				this.c.getBlockStorageArray()[j >> 4] = extendedblockstorage;
			}

			extendedblockstorage.set(i, j & 15, k, state);
		}

		return null;
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
