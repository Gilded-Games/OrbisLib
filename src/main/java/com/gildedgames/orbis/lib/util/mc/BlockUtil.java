package com.gildedgames.orbis.lib.util.mc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockUtil
{
	public static BlockState getBlockState(final ItemStack stack)
	{
		if (stack.getItem() instanceof BlockItem)
		{
			// TODO: Probably not adequate...
			return ((BlockItem) stack.getItem()).getBlock().getDefaultState();
		}

		return null;
	}

	public static boolean isSolid(final BlockState state, final IBlockReader world, final BlockPos pos)
	{
		return !(state.getMaterial() == Material.AIR) && Block.doesSideFillSquare(state.getShape(world, pos), Direction.DOWN)
				&& state.getMaterial().isOpaque();
	}

}
