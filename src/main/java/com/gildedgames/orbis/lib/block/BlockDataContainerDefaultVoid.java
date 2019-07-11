package com.gildedgames.orbis.lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BlockDataContainerDefaultVoid extends BlockDataContainer
{
	private static final BlockState VOID_BLOCK = Blocks.STRUCTURE_VOID.getDefaultState();

	private BlockDataContainerDefaultVoid()
	{
		super();
	}

	public BlockDataContainerDefaultVoid(final int width, final int height, final int length)
	{
		super(width, height, length);
	}

	@Override
	public BlockState getDefaultBlock()
	{
		return VOID_BLOCK;
	}

}
