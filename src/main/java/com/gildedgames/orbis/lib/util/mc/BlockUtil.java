package com.gildedgames.orbis.lib.util.mc;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockUtil
{
	public static IBlockState getBlockState(final ItemStack stack)
	{
		if (stack.getItem() instanceof ItemBlock)
		{
			return ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getItemDamage());
		}

		return null;
	}

	public static boolean isSolid(final IBlockState state, final IBlockReader world, final BlockPos pos)
	{
		return !(state.getMaterial() == Material.AIR) && state.getBlockFaceShape(world, pos, EnumFacing.DOWN) == BlockFaceShape.SOLID
				&& state.getMaterial().isOpaque();
	}

}
