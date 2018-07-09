package com.gildedgames.orbis_api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class BlockDataContainerDefault extends BlockDataContainer
{
	private IBlockState DEFAULT_STATE = Blocks.AIR.getDefaultState();

	private BlockDataContainerDefault()
	{
		super();
	}

	public BlockDataContainerDefault(IBlockState defaultState, final int width, final int height, final int length)
	{
		super(width, height, length);
		
		this.DEFAULT_STATE = defaultState;
	}

	@Override
	public IBlockState getBlockState(int i)
	{
		final IBlockState block = super.getBlockState(i);

		if (block == null)
		{
			return this.DEFAULT_STATE;
		}

		return block;
	}

	@Override
	public IBlockState defaultBlock()
	{
		return this.DEFAULT_STATE;
	}
}
