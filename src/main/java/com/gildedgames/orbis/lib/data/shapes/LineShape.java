package com.gildedgames.orbis.lib.data.shapes;

import com.gildedgames.orbis.lib.data.region.IRegion;
import com.gildedgames.orbis.lib.data.region.IShape;
import com.gildedgames.orbis.lib.data.region.Region;
import com.gildedgames.orbis.lib.util.LineHelp;
import com.gildedgames.orbis.lib.util.RotationHelp;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LineShape implements IShape
{
	private World world;

	private BlockPos start;

	private BlockPos end;

	private int lineRadius;

	private Iterable<BlockPos> data;

	private LineShape(final World world)
	{
		this.world = world;
	}

	public LineShape(final BlockPos start, final BlockPos end)
	{
		this(start, end, 2);
	}

	public LineShape(final BlockPos start, final BlockPos end, final int lineRadius)
	{
		this.start = start;
		this.end = end;

		this.lineRadius = lineRadius;
	}

	@Override
	public Iterable<BlockPos> createShapeData()
	{
		return LineHelp.createLinePositions(this.lineRadius, this.start, this.end);
	}

	@Override
	public void write(final CompoundNBT tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.setPos("viableStarts", this.start);
		funnel.setPos("end", this.end);

		tag.putInt("lineRadius", this.lineRadius);
	}

	@Override
	public void read(final CompoundNBT tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.start = funnel.getPos("viableStarts");
		this.end = funnel.getPos("end");

		this.lineRadius = tag.getInt("lineRadius");
	}

	@Override
	public IShape rotate(final Rotation rotation, final IRegion regionIn)
	{
		final BlockPos newStart = RotationHelp.rotate(this.start, regionIn, rotation);
		final BlockPos newEnd = RotationHelp.rotate(this.end, regionIn, rotation);

		return new LineShape(newStart, newEnd, this.lineRadius);
	}

	@Override
	public IShape translate(final int x, final int y, final int z)
	{
		return new LineShape(this.start.add(x, y, z), this.end.add(x, y, z), this.lineRadius);
	}

	@Override
	public IShape translate(final BlockPos pos)
	{
		return this.translate(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public IRegion getBoundingBox()
	{
		return new Region(this.start, this.end);
	}

	@Override
	public boolean contains(final int x, final int y, final int z)
	{
		boolean flag = false;

		for (final BlockPos pos : this.getShapeData())
		{
			if (pos.getX() == x && pos.getY() == y && pos.getZ() == z)
			{
				flag = true;
				break;
			}
		}

		return flag;
	}

	@Override
	public boolean contains(final BlockPos pos)
	{
		return this.contains(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public Iterable<BlockPos> getShapeData()
	{
		if (this.data == null)
		{
			this.data = this.createShapeData();
		}

		return this.data;
	}

}
