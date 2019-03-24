package com.gildedgames.orbis.lib.util.mc;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class BlockPosDimension extends BlockPos.MutableBlockPos implements NBT
{

	private DimensionType dimension;

	@SuppressWarnings("unused")
	private BlockPosDimension()
	{

	}

	public BlockPosDimension(final BlockPos pos, final DimensionType dimension)
	{
		this(pos.getX(), pos.getY(), pos.getZ(), dimension);
	}

	public BlockPosDimension(final int x, final int y, final int z, final DimensionType dimension)
	{
		super(x, y, z);

		this.dimension = dimension;
	}

	public DimensionType getDimension()
	{
		return this.dimension;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object == this)
		{
			return true;
		}

		if (object instanceof BlockPosDimension)
		{
			final BlockPosDimension dungeonPosition = (BlockPosDimension) object;
			return dungeonPosition.getX() == this.getX() && dungeonPosition.getY() == this.getY() && dungeonPosition.getZ() == this.getZ()
					&& dungeonPosition.dimension == this.dimension;
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		final HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(this.getX());
		builder.append(this.getY());
		builder.append(this.getZ());
		builder.append(this.dimension);

		return builder.toHashCode();
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		tag.putInt("x", this.getX());
		tag.putInt("y", this.getY());
		tag.putInt("z", this.getZ());
		tag.putInt("d", this.dimension.getId());
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		this.x = tag.getInt("x");
		this.y = tag.getInt("y");
		this.z = tag.getInt("z");
		this.dimension = DimensionType.getById(tag.getInt("d"));
	}
}
