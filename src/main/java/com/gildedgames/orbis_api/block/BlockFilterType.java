package com.gildedgames.orbis_api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
					RayTraceResult dummyResult = new RayTraceResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), EnumFacing.SOUTH, pos);

					for (final BlockDataWithConditions block : blackListedBlocks)
					{
						ItemStack toFilterPick = blockToFilter.getBlock().getPickBlock(blockToFilter, dummyResult, world, pos, player);
						ItemStack blockPick = block.getBlockState().getBlock().getPickBlock(block.getBlockState(), dummyResult, world, pos, player);

						if (block.getBlockState() == blockToFilter || block.getBlock() == blockToFilter.getBlock() || ItemStack
								.areItemsEqualIgnoreDurability(toFilterPick, blockPick))
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
					RayTraceResult dummyResult = new RayTraceResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), EnumFacing.SOUTH, pos);

					for (final BlockDataWithConditions block : requiredBlocks)
					{
						ItemStack toFilterPick = blockToFilter.getBlock().getPickBlock(blockToFilter, dummyResult, world, pos, player);
						ItemStack blockPick = block.getBlockState().getBlock().getPickBlock(block.getBlockState(), dummyResult, world, pos, player);

						if ((block.getBlock() == blockToFilter.getBlock() || block.getBlockState() == blockToFilter || ItemStack
								.areItemsEqualIgnoreDurability(toFilterPick, blockPick)) && block.getRequiredCondition()
								.isMet(random, world))
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
