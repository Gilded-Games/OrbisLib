package com.gildedgames.orbis_api.core.util;

import com.gildedgames.orbis_api.core.PlacementCondition;
import com.gildedgames.orbis_api.processing.IBlockAccessExtended;
import com.gildedgames.orbis_api.util.mc.BlockUtil;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class PlacementConditions
{

	public static PlacementCondition onSpecificBlock(final int floorHeight, final Block... blocks)
	{
		return (world, placedAt, block, pos) -> {
			if (pos.getY() == placedAt.getY() + floorHeight && block.getBlock() != Blocks.AIR
					&& block.getBlock() != Blocks.STRUCTURE_VOID)
			{
				final BlockPos down = pos.down();

				if (!world.canAccess(down))
				{
					return false;
				}

				final Block blockDown = world.getBlockState(down).getBlock();

				for (final Block s : blocks)
				{
					if (s == blockDown)
					{
						return true;
					}
				}

				return false;
			}

			return true;
		};
	}

	public static PlacementCondition replaceableGround()
	{
		return replaceable(true, Material.GROUND, Material.GRASS, Material.AIR, Material.SNOW);
	}

	public static PlacementCondition replaceable(final Material... acceptedMaterials)
	{
		return replaceable(true, acceptedMaterials);
	}

	public static PlacementCondition replaceable(final boolean isCriticalWithCheck, final Material... acceptedMaterials)
	{
		return new PlacementCondition()
		{
			List<Material> materials = Lists.newArrayList(acceptedMaterials);

			@Override
			public boolean canPlace(final IBlockAccessExtended world, final BlockPos placedAt, final IBlockState block,
					final BlockPos pos)
			{
				if (block.getBlock() != Blocks.STRUCTURE_VOID)
				{
					final IBlockState state = world.getBlockState(pos);

					if ((BlockUtil.isSolid(block) || block.getMaterial() == Material.PORTAL
							|| block == Blocks.AIR.getDefaultState()) && (BlueprintUtil.isReplaceable(world, pos)
							|| this.materials.contains(state.getMaterial())))
					{
						return true;
					}

					if ((isCriticalWithCheck ? block == state : block.getBlock() == state.getBlock())
							|| this.materials.contains(state.getMaterial()))
					{
						return true;
					}

					return world.isAirBlock(pos);
				}

				return true;
			}
		};
	}

	public static PlacementCondition insideGround(final Block inside)
	{
		return (world, placedAt, block, pos) -> {
			if (pos.getY() == placedAt.getY() + 1 && block.getBlock() != Blocks.AIR
					&& block.getBlock() != Blocks.STRUCTURE_VOID)
			{
				final BlockPos down = pos.down();

				if (!world.canAccess(down))
				{
					return false;
				}

				final IBlockState state = world.getBlockState(down);

				return state.getBlock() == inside;
			}

			return true;
		};
	}

	public static PlacementCondition flatGround()
	{
		return (world, placedAt, block, pos) -> {
			if (pos.getY() == placedAt.getY() && block.getBlock() != Blocks.AIR
					&& block.getBlock() != Blocks.STRUCTURE_VOID)
			{
				final BlockPos down = pos.down();

				if (!world.canAccess(down))
				{
					return false;
				}

				final IBlockState state = world.getBlockState(down);

				return BlockUtil.isSolid(state, world, down);
			}

			return true;
		};
	}

	public static PlacementCondition ignoreBlock(final int floorHeight, final IBlockState s)
	{
		return (world, placedAt, block, pos) -> {
			if (pos.getY() == placedAt.getY() + floorHeight && block.getBlock() != Blocks.AIR
					&& block.getBlock() != Blocks.STRUCTURE_VOID)
			{
				final BlockPos down = pos.down();

				if (!world.canAccess(down))
				{
					return false;
				}

				final IBlockState state = world.getBlockState(down);

				return s != state;
			}

			return true;
		};
	}

	public static PlacementCondition insideGroundAtSource(final Block block)
	{
		return new PlacementCondition()
		{
			@Override
			public boolean canPlace(final IBlockAccessExtended world, final BlockPos placedAt, final IBlockState block,
					final BlockPos pos)
			{
				if (pos.getY() == placedAt.getY() && block.getBlock() != Blocks.AIR && block.getBlock() != Blocks.STRUCTURE_VOID)
				{
					final BlockPos down = pos.down();

					if (!world.canAccess(down))
					{
						return false;
					}

					IBlockState state = world.getBlockState(down);

					return BlockUtil.isSolid(state);
				}

				return true;
			}

			@Override
			public boolean prePlacementResolve(IBlockAccessExtended world, BlockPos placedAt)
			{
				if (!world.canAccess(placedAt))
				{
					return false;
				}

				IBlockState state = world.getBlockState(placedAt);

				return state.getBlock() == block;
			}
		};
	}

}
