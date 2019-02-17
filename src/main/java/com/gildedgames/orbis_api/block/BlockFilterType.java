package com.gildedgames.orbis_api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public enum BlockFilterType
{

	ALL
			{
				@Override
				public boolean filter(EntityPlayer player, BlockPos pos, final IBlockState blockToFilter, final List<BlockDataWithConditions> requiredBlocks,
						final World world,
						final Random random)
				{
					return true;
				}

			},
	ALL_EXCEPT
			{
				@Override
				public boolean filter(EntityPlayer player, BlockPos pos, final IBlockState blockToFilter, final List<BlockDataWithConditions> blackListedBlocks,
						final World world,
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
						final World world,
						final Random random)
				{
					for (final BlockDataWithConditions block : requiredBlocks)
					{
						if ((block.getBlock() == blockToFilter.getBlock() && block.getRequiredCondition().isMet(random, world)))
						{
							return true;
						}
					}

					return false;
				}

			};

	public abstract boolean filter(EntityPlayer player, BlockPos pos, IBlockState blockToFilter, List<BlockDataWithConditions> originalBlocks, World world,
			Random random);

}
