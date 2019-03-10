package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.data.region.IRegion;
import com.gildedgames.orbis.lib.data.region.IShape;
import com.gildedgames.orbis.lib.data.region.Region;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BlockStateRecord implements IPositionRecord<IBlockState>
{
	private final List<IPositionRecordListener<IBlockState>> listeners = Lists.newArrayList();

	private IBlockState[] states;

	private int[] markedPositions;

	private int width, height, length, volume;

	private IRegion boundingBox;

	private Iterable<BlockPos.MutableBlockPos> data;

	private BlockStateRecord()
	{

	}

	public BlockStateRecord(final int width, final int height, final int length)
	{
		this.width = width;
		this.height = height;
		this.length = length;

		this.boundingBox = new Region(BlockPos.ORIGIN, new BlockPos(this.width - 1, this.height - 1, this.length - 1));

		this.createMarkedPositions();
	}

	private int getInternalStateId(final IBlockState state)
	{
		int id = -1;

		for (int i = 0; i < this.states.length; i++)
		{
			final IBlockState f = this.states[i];

			if (f != null && state == f)
			{
				id = i;
				break;
			}
		}

		return id;
	}

	private void checkForFilterAndAdd(final IBlockState state)
	{
		if (this.states == null)
		{
			this.states = new IBlockState[0];
		}

		boolean hasState = false;

		for (final IBlockState s : this.states)
		{
			if (s != null && state == s)
			{
				hasState = true;
				break;
			}
		}

		if (!hasState)
		{
			this.states = Arrays.copyOf(this.states, this.states.length + 1);

			this.states[this.states.length - 1] = state;
		}
	}

	private void createMarkedPositions()
	{
		this.volume = this.width * this.height * this.length;

		this.markedPositions = new int[this.getVolume()];
		Arrays.fill(this.markedPositions, -1);
	}

	@Override
	public int getVolume()
	{
		return this.volume;
	}

	private int getIndex(final int x, final int y, final int z, final boolean throwException)
	{
		final int index = z + y * this.length + x * this.height * this.length;

		if (index < this.getVolume() && index >= 0)
		{
			return index;
		}

		if (throwException)
		{
			throw new ArrayIndexOutOfBoundsException("Tried to access position that's not in this FilterRecord: " + x + ", " + y + ", " + z);
		}

		return -1;
	}

	public int getZ(final int index)
	{
		return index / (this.width * this.length);
	}

	public int getY(int index)
	{
		final int z = this.getZ(index);
		index -= (z * this.width * this.length);

		return index / this.width;
	}

	public int getX(int index)
	{
		final int z = this.getZ(index);
		index -= (z * this.width * this.length);

		return index % this.width;
	}

	@Override
	public void listen(final IPositionRecordListener<IBlockState> listener)
	{
		if (!this.listeners.contains(listener))
		{
			this.listeners.add(listener);
		}
	}

	@Override
	public boolean unlisten(final IPositionRecordListener<IBlockState> listener)
	{
		return this.listeners.remove(listener);
	}

	@Override
	public boolean contains(final int index)
	{
		return this.markedPositions[index] != -1;
	}

	@Override
	public IBlockState[] getData()
	{
		if (this.states == null)
		{
			this.states = new IBlockState[0];
		}

		return this.states;
	}

	@Override
	public IBlockState get(final int index)
	{
		final int id = this.markedPositions[index];

		if (id == -1)
		{
			return null;
		}

		return this.states[id];
	}

	@Override
	public IBlockState get(final int x, final int y, final int z)
	{
		final int index = this.getIndex(x, y, z, true);

		return this.get(index);
	}

	@Override
	public void markPos(final IBlockState state, final int x, final int y, final int z)
	{
		this.checkForFilterAndAdd(state);

		final int index = this.getIndex(x, y, z, true);

		this.markedPositions[index] = this.getInternalStateId(state);

		this.listeners.forEach(l -> l.onMarkPos(state, x, y, z));
	}

	@Override
	public void unmarkPos(final int x, final int y, final int z)
	{
		final int index = this.getIndex(x, y, z, true);

		this.markedPositions[index] = -1;

		this.listeners.forEach(l -> l.onUnmarkPos(x, y, z));
	}

	@Override
	public IRegion getRegion()
	{
		return this.boundingBox;
	}

	@Override
	public Iterable<BlockPos.MutableBlockPos> createShapeData()
	{
		return new Iterable<BlockPos.MutableBlockPos>()
		{

			@Override
			public Iterator<BlockPos.MutableBlockPos> iterator()
			{
				final Iterator<BlockPos.MutableBlockPos> iter = BlockStateRecord.this.getBoundingBox().createShapeData().iterator();

				return new AbstractIterator<BlockPos.MutableBlockPos>()
				{

					@Override
					protected BlockPos.MutableBlockPos computeNext()
					{
						while (iter.hasNext())
						{
							final BlockPos.MutableBlockPos next = iter.next();

							if (BlockStateRecord.this.contains(next.getX(), next.getY(), next.getZ()))
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
	public Iterable<BlockPos.MutableBlockPos> getShapeData()
	{
		if (this.data == null)
		{
			this.data = this.createShapeData();
		}

		return this.data;
	}

	@Override
	public IShape rotate(final Rotation rotation, final IRegion in)
	{
		return this;
	}

	@Override
	public IShape translate(final int x, final int y, final int z)
	{
		return new BlockStateRecord(this.width, this.height, this.length);
	}

	@Override
	public IShape translate(final BlockPos pos)
	{
		return this.translate(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public IRegion getBoundingBox()
	{
		return this.boundingBox;
	}

	@Override
	public boolean contains(final int x, final int y, final int z)
	{
		final int index = this.getIndex(x, y, z, false);

		if (index == -1)
		{
			return false;
		}

		return this.markedPositions[index] != -1;
	}

	@Override
	public boolean contains(final BlockPos pos)
	{
		return this.contains(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		tag.setInteger("width", this.width);
		tag.setInteger("height", this.height);
		tag.setInteger("length", this.length);

		tag.setInteger("volume", this.volume);

		funnel.set("boundingBox", this.boundingBox);

		funnel.setArray("states", this.states, NBTFunnel.BLOCKSTATE_SETTER);

		funnel.setIntArray("markedPositions", this.markedPositions);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.width = tag.getInteger("width");
		this.height = tag.getInteger("height");
		this.length = tag.getInteger("length");

		this.volume = tag.getInteger("volume");

		this.boundingBox = funnel.get("boundingBox");

		this.states = funnel.getArray("states", IBlockState.class, NBTFunnel.BLOCKSTATE_GETTER);
		this.markedPositions = funnel.getIntArray("markedPositions");
	}

	@Override
	public int getWidth()
	{
		return this.width;
	}

	@Override
	public int getHeight()
	{
		return this.height;
	}

	@Override
	public int getLength()
	{
		return this.length;
	}
}
