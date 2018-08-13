package com.gildedgames.orbis_api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class BlockDataContainerDefaultVoid extends BlockDataContainer
{
	private static final IBlockState VOID_BLOCK = Blocks.STRUCTURE_VOID.getDefaultState();

	private BlockDataContainerDefaultVoid()
	{
		super();
	}

	public BlockDataContainerDefaultVoid(final int width, final int height, final int length)
	{
		super(width, height, length);
	}

	@Override
	public IBlockState getDefaultBlock()
	{
		return VOID_BLOCK;
	}

	@Override
	public BlockDataContainer createNewContainer()
	{
		return new BlockDataContainerDefaultVoid();
	}
}
