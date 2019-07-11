package com.gildedgames.orbis.lib.block;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Rotation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.annotation.Nullable;

@Deprecated
public class BlockData implements NBT
{
	private BlockState blockState;

	private CompoundNBT tileEntity;

	public BlockData()
	{

	}

	public BlockData(final Block block)
	{
		this();
		this.blockState = block.getDefaultState();
	}

	public BlockData(final BlockState blockState)
	{
		this();
		this.blockState = blockState;
	}

	public BlockData(final BlockState blockState, final CompoundNBT tileEntity)
	{
		this(blockState);
		this.tileEntity = tileEntity;
	}

	public BlockData(final BlockData block)
	{
		this.blockState = block.blockState;
		this.tileEntity = block.tileEntity;
	}

	@Nullable
	public Block getBlock()
	{
		return this.blockState.getBlock();
	}

	public BlockState getBlockState()
	{
		return this.blockState;
	}

	public CompoundNBT getTileEntity()
	{
		return this.tileEntity;
	}

	public BlockState getRotatedBlockState(final Rotation rotation)
	{
		return this.getBlockState().rotate(rotation);
	}

	@Override
	public void write(final CompoundNBT tag)
	{
		tag.put("block", NBTUtil.writeBlockState(this.blockState));

		final boolean hasTileEntity = this.tileEntity != null;
		tag.putBoolean("hasTileEntity", hasTileEntity);

		if (hasTileEntity)
		{
			tag.put("tileEntity", this.tileEntity);
		}
	}

	@Override
	public void read(final CompoundNBT tag)
	{
		this.blockState = NBTUtil.readBlockState(tag.getCompound("block"));

		final boolean hasTileEntity = tag.getBoolean("hasTileEntity");

		if (hasTileEntity)
		{
			this.tileEntity = tag.getCompound("tileEntity");
		}
	}

	public boolean isVoid()
	{
		return this.getBlockState().getBlock() == Blocks.STRUCTURE_VOID;
	}

	public boolean isAir()
	{
		return this.getBlockState().getBlock().getMaterial(this.getBlockState()) == Material.AIR;
	}

	@Override
	public int hashCode()
	{
		final HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(OrbisLib.services().registrar().getStateId(this.getBlockState()));

		return builder.toHashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (super.equals(obj))
		{
			return true;
		}

		if (obj instanceof BlockData)
		{
			final EqualsBuilder builder = new EqualsBuilder();

			final BlockData b = (BlockData) obj;

			builder.append(this.blockState, b.blockState);
			builder.append(this.tileEntity, b.tileEntity);

			return builder.isEquals();
		}

		return false;
	}

}
