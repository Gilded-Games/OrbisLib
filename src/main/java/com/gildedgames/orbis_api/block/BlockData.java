package com.gildedgames.orbis_api.block;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.util.mc.NBT;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Rotation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.annotation.Nullable;

@Deprecated
public class BlockData implements NBT
{
	private Block block;

	private IBlockState blockState;

	private NBTTagCompound tileEntity;

	public BlockData()
	{

	}

	public BlockData(final Block block)
	{
		this();
		this.block = block;
	}

	public BlockData(final IBlockState blockState)
	{
		this();
		this.blockState = blockState;
	}

	public BlockData(final IBlockState blockState, final NBTTagCompound tileEntity)
	{
		this(blockState);
		this.tileEntity = tileEntity;
	}

	public BlockData(final BlockData block)
	{
		this.block = block.block;
		this.blockState = block.blockState;
		this.tileEntity = block.tileEntity;
	}

	@Nullable
	public Block getBlock()
	{
		return this.block;
	}

	public IBlockState getBlockState()
	{
		if (this.block != null && this.blockState == null)
		{
			return this.block.getDefaultState();
		}

		return this.blockState;
	}

	public NBTTagCompound getTileEntity()
	{
		return this.tileEntity;
	}

	public IBlockState getRotatedBlockState(final Rotation rotation)
	{
		return this.getBlockState().withRotation(rotation);
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		tag.setBoolean("noState", this.blockState == null);
		tag.setInteger("id", OrbisAPI.services().registrar().getStateId(this.getBlockState()));

		final boolean hasTileEntity = this.tileEntity != null;
		tag.setBoolean("hasTileEntity", hasTileEntity);

		if (hasTileEntity)
		{
			tag.setTag("tileEntity", this.tileEntity);
		}
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final boolean noState = tag.getBoolean("noState");

		if (noState)
		{
			this.block = OrbisAPI.services().registrar().getStateFromId(tag.getInteger("id")).getBlock();
		}
		else
		{
			this.blockState = OrbisAPI.services().registrar().getStateFromId(tag.getInteger("id"));
		}

		final boolean hasTileEntity = tag.getBoolean("hasTileEntity");

		if (hasTileEntity)
		{
			this.tileEntity = tag.getCompoundTag("tileEntity");
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

		builder.append(OrbisAPI.services().registrar().getStateId(this.getBlockState()));

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

			builder.append(this.block, b.block);
			builder.append(this.blockState, b.blockState);
			builder.append(this.tileEntity, b.tileEntity);

			return builder.isEquals();
		}

		return false;
	}

}
