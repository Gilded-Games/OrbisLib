package com.gildedgames.orbis.lib.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Random;

public enum BlockFilterType
{

	ALL
			{
				@Override
				public boolean filter(EntityPlayer player, BlockPos pos, final IBlockState blockToFilter, final List<BlockDataWithConditions> requiredBlocks,
						final Random random)
				{
					return true;
				}

			},
	ALL_EXCEPT
			{
				@Override
				public boolean filter(EntityPlayer player, BlockPos pos, final IBlockState blockToFilter, final List<BlockDataWithConditions> blackListedBlocks,
						final Random random)
				{
					for (final BlockDataWithConditions block : blackListedBlocks)
					{
						if (block.getBlock() == blockToFilter.getBlock())
						{
							return false;
						}
					}

					return true;
				}

			},
	ONLY
			{
				@Override
				public boolean filter(EntityPlayer player, BlockPos pos, final IBlockState blockToFilter, final List<BlockDataWithConditions> requiredBlocks,
						final Random random)
				{
					for (final BlockDataWithConditions block : requiredBlocks)
					{
						if ((block.getBlock() == blockToFilter.getBlock() && block.getRequiredCondition().isMet(random)))
						{
							return true;
						}
					}

					return false;
				}

			};

	public abstract boolean filter(EntityPlayer player, BlockPos pos, IBlockState blockToFilter, List<BlockDataWithConditions> originalBlocks, Random random);

}
