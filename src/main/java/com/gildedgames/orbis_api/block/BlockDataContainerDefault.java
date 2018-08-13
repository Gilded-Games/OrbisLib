package com.gildedgames.orbis_api.block;

import com.gildedgames.orbis_api.data.region.IRegion;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class BlockDataContainerDefault extends BlockDataContainer
{
	private IBlockState DEFAULT_STATE = Blocks.AIR.getDefaultState();

	private BlockDataContainerDefault()
	{
		super();
	}

	public BlockDataContainerDefault(int width, int height, int length)
	{
		super(width, height, length);
	}

	public BlockDataContainerDefault(IRegion region)
	{
		super(region);
	}

	public BlockDataContainerDefault(IBlockState defaultState, final int width, final int height, final int length)
	{
		super(width, height, length);

		this.DEFAULT_STATE = defaultState;
	}

	@Override
	public IBlockState defaultBlock()
	{
		return this.DEFAULT_STATE;
	}

	@Override
	public BlockDataContainer createNewContainer()
	{
		return new BlockDataContainerDefault();
	}
}
