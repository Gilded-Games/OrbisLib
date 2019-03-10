package com.gildedgames.orbis.lib.block;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.annotation.Nullable;

@Deprecated
public class BlockData implements NBT
{
	private IBlockState blockState;

	private NBTTagCompound tileEntity;

	public BlockData()
	{

	}

	public BlockData(final Block block)
	{
		this();
		this.blockState = block.getDefaultState();
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
		this.blockState = block.blockState;
		this.tileEntity = block.tileEntity;
	}

	@Nullable
	public Block getBlock()
	{
		return this.blockState.getBlock();
	}

	public IBlockState getBlockState()
	{
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

		Block block = this.blockState.getBlock();

		final ResourceLocation identifier = OrbisLib.services().registrar().getIdentifierFor(block);
		short meta = (short) (this.blockState == null ? 0 : block.getMetaFromState(this.blockState));

		tag.setString("mod", identifier.getNamespace());
		tag.setString("name", identifier.getPath());
		tag.setShort("meta", meta);

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

		String mod = tag.getString("mod");
		String name = tag.getString("name");

		final Block block = OrbisLib.services().registrar().findBlock(new ResourceLocation(mod, name));

		int meta = tag.getShort("meta");

		if (noState)
		{
			this.blockState = block.getDefaultState();
		}
		else
		{
			if (block != null)
			{
				this.blockState = block.getStateFromMeta(meta);
			}
			else
			{
				this.blockState = Blocks.AIR.getDefaultState();
			}
		}

		if (this.blockState == null)
		{
			this.blockState = Blocks.AIR.getDefaultState();
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
