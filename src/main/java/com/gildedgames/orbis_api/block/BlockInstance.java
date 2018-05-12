package com.gildedgames.orbis_api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class BlockInstance
{

	private final IBlockState blockData;

	private final NBTTagCompound entity;

	private final BlockPos pos;

	public BlockInstance(final IBlockState blockData, final NBTTagCompound entity, final BlockPos pos)
	{
		this.blockData = blockData;
		this.pos = pos;
		this.entity = entity;
	}

	public IBlockState getBlockState()
	{
		return this.blockData;
	}

	public BlockPos getPos()
	{
		return this.pos;
	}

	public NBTTagCompound getEntity()
	{
		return this.entity;
	}

	@Override
	public boolean equals(final Object obj)
	{
		boolean flag = false;

		if (obj == this)
		{
			flag = true;
		}
		else if (obj instanceof BlockInstance)
		{
			final BlockInstance o = (BlockInstance) obj;
			final EqualsBuilder builder = new EqualsBuilder();

			builder.append(this.blockData, o.blockData);
			builder.append(this.pos, o.blockData);

			flag = builder.isEquals();
		}

		return flag;
	}

	@Override
	public String toString()
	{
		return "BlockInstance - POS: " + this.pos.toString() + ", STATE: " + this.blockData.toString();
	}

}
