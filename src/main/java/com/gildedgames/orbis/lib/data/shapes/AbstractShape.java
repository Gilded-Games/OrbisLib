package com.gildedgames.orbis.lib.data.shapes;

import com.gildedgames.orbis.lib.data.region.IMutableRegion;
import com.gildedgames.orbis.lib.data.region.IRegion;
import com.gildedgames.orbis.lib.data.region.IShape;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.google.common.collect.AbstractIterator;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;

public abstract class AbstractShape implements IShape
{
	private boolean createFromCenter, uniform;

	private World world;

	private IMutableRegion boundingBox;

	public AbstractShape()
	{

	}

	protected AbstractShape(final World world)
	{
		this.world = world;
	}

	public AbstractShape(final IMutableRegion boundingBox)
	{
		this.boundingBox = boundingBox;
	}

	public void setCreateFromCenter(final boolean flag)
	{
		this.createFromCenter = flag;
	}

	public boolean createFromCenter()
	{
		return this.createFromCenter;
	}

	public boolean isUniform()
	{
		return this.uniform;
	}

	public void setUniform(final boolean flag)
	{
		this.uniform = flag;
	}

	public IMutableRegion getMutableBB()
	{
		return this.boundingBox;
	}

	@Override
	public final Iterable<BlockPos> createShapeData()
	{
		return new Iterable<BlockPos>()
		{
			@Override
			public Iterator<BlockPos> iterator()
			{
				final Iterator<BlockPos> iter = AbstractShape.this.getBoundingBox().createShapeData().iterator();
				return new AbstractIterator<BlockPos>()
				{

					@Override
					protected BlockPos computeNext()
					{
						while (iter.hasNext())
						{
							final BlockPos next = iter.next();

							if (AbstractShape.this.contains(next.getX(), next.getY(), next.getZ()))
							{
								return next;
							}
						}

						return this.endOfData();
					}
				};
			}
		};
	}

	@Override
	public IRegion getBoundingBox()
	{
		return this.boundingBox;
	}

	protected void setBoundingBox(final IMutableRegion bb)
	{
		this.boundingBox = bb;
	}

	@Override
	public final void write(final CompoundNBT tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		tag.putBoolean("createFromCenter", this.createFromCenter);
		tag.putBoolean("uniform", this.uniform);

		funnel.set("boundingBox", this.boundingBox);

		this.writeShape(tag);
	}

	public abstract void writeShape(CompoundNBT tag);

	@Override
	public final void read(final CompoundNBT tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.createFromCenter = tag.getBoolean("createFromCenter");
		this.uniform = tag.getBoolean("uniform");

		this.boundingBox = funnel.get("boundingBox");

		this.readShape(tag);
	}

	public abstract void readShape(CompoundNBT tag);

	public abstract BlockPos getRenderBoxMin();

	public abstract BlockPos getRenderBoxMax();

}
