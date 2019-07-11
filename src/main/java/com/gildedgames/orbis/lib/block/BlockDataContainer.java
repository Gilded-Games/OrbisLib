package com.gildedgames.orbis.lib.block;

import com.gildedgames.orbis.lib.data.management.IData;
import com.gildedgames.orbis.lib.data.management.IDataMetadata;
import com.gildedgames.orbis.lib.data.management.impl.DataMetadata;
import com.gildedgames.orbis.lib.data.region.IDimensions;
import com.gildedgames.orbis.lib.data.region.IRegion;
import com.gildedgames.orbis.lib.data.region.IShape;
import com.gildedgames.orbis.lib.util.mc.NBT;
import com.gildedgames.orbis.lib.world.IWorldObject;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Map;

public class BlockDataContainer implements NBT, IDimensions, IData
{
	private int[] blocks;

	private Int2ObjectOpenHashMap<TileEntityEntry> entities = new Int2ObjectOpenHashMap<>();

	private Int2ObjectOpenHashMap<BlockState> paletteKeys = new Int2ObjectOpenHashMap<>();

	private Object2IntOpenCustomHashMap<BlockState> paletteValues =
			new Object2IntOpenCustomHashMap<>(Util.identityHashStrategy());

	private int width, height, length;

	private IDataMetadata metadata;

	private BlockState defaultBlock;

	private int nextID;

	public BlockDataContainer(BlockDataContainer other, int width, int height, int length)
	{
		this.entities = new Int2ObjectOpenHashMap<>(this.entities);

		this.defaultBlock = other.defaultBlock;

		this.paletteKeys = new Int2ObjectOpenHashMap<>(other.paletteKeys);
		this.paletteValues = new Object2IntOpenCustomHashMap<>(other.paletteValues, other.paletteValues.strategy());

		this.width = width;
		this.height = height;
		this.length = length;

		this.blocks = new int[this.getVolume()];

		this.nextID = other.nextID;
		this.metadata = other.metadata;
	}

	public BlockDataContainer()
	{
		this(Blocks.AIR.getDefaultState());
	}

	public BlockDataContainer(BlockState defaultBlock)
	{
		if (defaultBlock == null)
		{
			throw new IllegalArgumentException("Default block cannot be null");
		}

		this.metadata = new DataMetadata();

		this.defaultBlock = defaultBlock;

		this.paletteKeys.put(0, this.defaultBlock);
		this.paletteValues.put(this.defaultBlock, 0);

		this.paletteValues.defaultReturnValue(Integer.MIN_VALUE);
		this.paletteKeys.defaultReturnValue(null);
	}

	/**
	 * @param width
	 * @param height Maximum height possible is 256
	 * @param length
	 */
	public BlockDataContainer(final BlockState defaultBlock, final int width, final int height, final int length)
	{
		this(defaultBlock);

		this.width = width;
		this.height = Math.min(256, height);
		this.length = length;

		this.blocks = new int[this.getVolume()];
	}

	public BlockDataContainer(BlockState defaultBlock, final IRegion region)
	{
		this(defaultBlock, region.getWidth(), region.getHeight(), region.getLength());
	}

	public BlockDataContainer(final int width, final int height, final int length)
	{
		this(Blocks.AIR.getDefaultState(), width, height, length);
	}

	public BlockDataContainer(final IRegion region)
	{
		this(Blocks.AIR.getDefaultState(), region);
	}

	public static BlockDataContainer fromShape(final World world, final IShape shape)
	{
		final IRegion bounding = shape.getBoundingBox();
		final int minx = bounding.getMin().getX();
		final int miny = bounding.getMin().getY();
		final int minz = bounding.getMin().getZ();
		final BlockDataContainer container = new BlockDataContainer(bounding.getWidth(), bounding.getHeight(), bounding.getLength());
		for (final BlockPos pos : shape.getShapeData())
		{
			final BlockState state = world.getBlockState(pos);

			final BlockPos tr = pos.add(-minx, -miny, -minz);
			container.setBlockState(state, tr);
			container.setTileEntity(world.getTileEntity(pos), tr);
		}
		return container;
	}

	public int getVolume()
	{
		return this.width * this.height * this.length;
	}

	private int getIndex(BlockPos pos)
	{
		return this.getIndex(pos.getX(), pos.getY(), pos.getZ());
	}

	private int getIndex(final int x, final int y, final int z)
	{
		return z + y * this.length + x * this.height * this.length;
	}

	private BlockPos fromIndex(int idx)
	{
		final int x = idx / (this.height * this.length);
		idx -= (x * this.height * this.length);

		final int y = idx / this.length;
		final int z = idx % this.length;

		return new BlockPos(x, y, z);
	}

	public boolean isOutsideOfContainer(BlockPos pos)
	{
		int index = this.getIndex(pos.getX(), pos.getY(), pos.getZ());

		return index >= this.blocks.length || index < 0;
	}

	public BlockState getBlockState(final BlockPos pos)
	{
		return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
	}

	public BlockState getBlockState(final int x, final int y, final int z)
	{
		return this.getBlockState(this.getIndex(x, y, z));
	}

	private BlockState getBlockState(final int index)
	{
		return this.paletteKeys.get(this.blocks[index]);
	}

	public void setBlockState(final BlockState state, final int x, final int y, final int z) throws ArrayIndexOutOfBoundsException
	{
		this.setBlockState(state, this.getIndex(x, y, z));
	}

	private void setBlockState(final BlockState state, final int index)
	{
		int value = this.paletteValues.getInt(state);

		if (value == this.paletteValues.defaultReturnValue())
		{
			value = this.nextID++;

			this.paletteValues.put(state, value);
			this.paletteKeys.put(value, state);
		}

		this.blocks[index] = value;
	}

	public void setBlockState(final BlockState state, final BlockPos pos) throws ArrayIndexOutOfBoundsException
	{
		this.setBlockState(state, pos.getX(), pos.getY(), pos.getZ());
	}

	public void copyBlockStateWithRotation(BlockDataContainer data, int otherX, int otherY, int otherZ, int thisX, int thisY, int thisZ, Rotation rotation)
	{
		int block = data.blocks[data.getIndex(otherX, otherY, otherZ)];

		if (block == 0)
		{
			this.blocks[this.getIndex(thisX, thisY, thisZ)] = block;
		}
		else
		{
			BlockState state = data.paletteKeys.get(block);
			BlockState stateRotated = state.rotate(rotation);

			// Avoid expensive map operations if the block hasn't changed... we know the state is already mapped
			if (state == stateRotated)
			{
				this.blocks[this.getIndex(thisX, thisY, thisZ)] = data.blocks[data.getIndex(otherX, otherY, otherZ)];
			}
			else
			{
				this.setBlockState(stateRotated, thisX, thisY, thisZ);
			}
		}
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

	public BlockState getDefaultBlock()
	{
		return this.defaultBlock;
	}

	@Override
	public void write(final CompoundNBT tag)
	{
		tag.putInt("width", this.getWidth());
		tag.putInt("height", this.getHeight());
		tag.putInt("length", this.getLength());

		final byte[] blocks = new byte[this.getVolume()];
		byte[] addBlocks = null;

		Int2IntOpenHashMap paletteRemapper = new Int2IntOpenHashMap();
		paletteRemapper.put(0, 0); // The default block is never remapped

		/**
		 * Create maps to compress data written
		 * Instead of writing entire ResourceLocations for each block, we'll
		 * be writing an integer and the map will point us to the appropriate
		 * ResourceLocation when reading back
		 */
		final ArrayList<BlockState> identifiers = new ArrayList<>();
		identifiers.add(this.defaultBlock);

		final Int2ObjectOpenHashMap<CompoundNBT> tileEntities = new Int2ObjectOpenHashMap<>();

		for (int i = 0; i < this.blocks.length; i++)
		{
			BlockState state = this.getBlockState(i);

			int paletteId = this.blocks[i];

			int blockId;

			if (paletteId >= 0)
			{
				if (!paletteRemapper.containsKey(paletteId))
				{
					blockId = identifiers.size();

					paletteRemapper.put(paletteId, blockId);
					identifiers.add(state);
				}
				else
				{
					blockId = paletteRemapper.get(paletteId);
				}
			}
			else
			{
				state = this.defaultBlock;

				blockId = 0;
			}

			if (blockId > 255)
			{
				if (addBlocks == null)
				{
					addBlocks = new byte[(blocks.length >> 1) + 1];
				}

				final int addBlocksIndex = i >> 1;

				if ((i & 1) == 0)
				{
					final byte result = (byte) (addBlocks[addBlocksIndex] & 0xF0 | blockId >> 8 & 0xF);
					addBlocks[addBlocksIndex] = result;
				}
				else
				{
					final byte result = (byte) (addBlocks[addBlocksIndex] & 0xF | (blockId >> 8 & 0xF) << 4);
					addBlocks[addBlocksIndex] = result;
				}
			}

			blocks[i] = (byte) blockId;

			if (state.hasTileEntity())
			{
				final TileEntityEntry tileEntity = this.entities.get(i);

				if (tileEntity != null)
				{
					tileEntities.put(i, tileEntity.data);
				}
			}
		}

		/**
		 * Saving the identifier map for later reference
		 */
		final ListNBT identifiersNbt = new ListNBT();

		for (BlockState state : identifiers)
		{
			// Don't serialize the default key
			if (state == this.defaultBlock)
			{
				continue;
			}

			identifiersNbt.add(NBTUtil.writeBlockState(state));
		}

		tag.put("identifiers", identifiersNbt);

		/**
		 * Saving tile entity data
		 */
		final ListNBT tileEntityList = new ListNBT();

		for (final Map.Entry<Integer, CompoundNBT> entry : tileEntities.entrySet())
		{
			final CompoundNBT data = new CompoundNBT();

			data.put("tileEnt", entry.getValue());
			data.putInt("orbisTEIndex", entry.getKey());

			tileEntityList.add(data);
		}

		tag.put("tileEntities", tileEntityList);

		tag.putByteArray("blocks", blocks);

		tag.putBoolean("addBlocks_null", addBlocks == null);

		if (addBlocks != null)
		{
			tag.putByteArray("addBlocks", addBlocks);
		}
	}

	@Override
	public void read(final CompoundNBT tag)
	{
		Int2ObjectOpenHashMap<BlockState> localIdToBlock = new Int2ObjectOpenHashMap<>();
		localIdToBlock.put(0, this.defaultBlock);

		this.width = tag.getInt("width");
		this.height = tag.getInt("height");
		this.length = tag.getInt("length");

		/** Read back identifier list so we can figure out which
		 * ids belong to what blocks (as well as their parent mods)
		 */
		final ListNBT identifierList = tag.getList("identifiers", 10);

		int start = localIdToBlock.size();

		for (int i = 0; i < identifierList.size(); i++)
		{
			final BlockState state = NBTUtil.readBlockState(identifierList.getCompound(i));

			localIdToBlock.put(start + i, state);
		}

		/**
		 * Read back tile entities
		 */
		final Int2ObjectOpenHashMap<CompoundNBT> tileEntities = new Int2ObjectOpenHashMap<>();
		final ListNBT tileEntityList = tag.getList("tileEntities", 10);

		for (int i = 0; i < tileEntityList.size(); i++)
		{
			final CompoundNBT data = tileEntityList.getCompound(i);

			final CompoundNBT tileEntData = data.getCompound("tileEnt");
			tileEntities.put(data.getInt("orbisTEIndex"), tileEntData);
		}

		final byte[] blockComp = tag.getByteArray("blocks");
		final byte[] addBlocks = tag.getBoolean("addBlocks_null") ? null : tag.getByteArray("addBlocks");

		if (blockComp.length != this.getVolume())
		{
			throw new IllegalStateException("Size of data mismatched dimensions given");
		}

		this.blocks = new int[blockComp.length];

		this.entities = new Int2ObjectOpenHashMap<>();

		for (int i = 0; i < blockComp.length; i++)
		{
			final int finalId;

			int blockCompValue = blockComp[i];

			if (blockCompValue == -1)
			{
				finalId = -1;
			}
			else if (addBlocks == null || i >> 1 >= addBlocks.length)
			{
				finalId = blockComp[i] & 0xFF;
			}
			else
			{
				if ((i & 1) == 0)
				{
					finalId = ((addBlocks[i >> 1] & 0x0F) << 8) + (blockComp[i] & 0xFF);
				}
				else
				{
					finalId = ((addBlocks[i >> 1] & 0xF0) << 4) + (blockComp[i] & 0xFF);
				}
			}

			BlockState state = localIdToBlock.get(finalId);

			this.setBlockState(state, i);

			if (state.hasTileEntity())
			{
				CompoundNBT entity = tileEntities.get(i);

				if (entity != null)
				{
					BlockPos pos = this.fromIndex(i);

					this.entities.put(i, new TileEntityEntry(entity, pos));
				}
			}
		}
	}

	@Override
	public void preSaveToDisk(final IWorldObject object)
	{

	}

	@Override
	public BlockDataContainer clone()
	{
		final BlockDataContainer data = new BlockDataContainer();

		data.blocks = new int[this.blocks.length];

		System.arraycopy(this.blocks, 0, data.blocks, 0, this.blocks.length);

		data.paletteKeys = new Int2ObjectOpenHashMap<>(this.paletteKeys);
		data.paletteValues = new Object2IntOpenCustomHashMap<>(this.paletteValues, this.paletteValues.strategy());

		data.nextID = this.nextID;
		data.metadata = this.metadata;

		data.entities.clear();

		for (int i : this.entities.keySet())
		{
			TileEntityEntry e = this.entities.get(i);

			data.entities.put(i, new TileEntityEntry(e.data.copy(), e.pos));
		}

		data.width = this.width;
		data.height = this.height;
		data.length = this.length;

		return data;
	}

	@Override
	public String getFileExtension()
	{
		return "nbt";
	}

	@Override
	public IDataMetadata getMetadata()
	{
		return this.metadata;
	}

	@Override
	public void setMetadata(IDataMetadata metadata)
	{
		this.metadata = metadata;
	}

	public TileEntityEntry getTileEntity(int x, int y, int z)
	{
		return this.entities.get(this.getIndex(x, y, z));
	}

	public void setTileEntity(TileEntity tileEntity, BlockPos pos)
	{
		this.setTileEntity(tileEntity.write(new CompoundNBT()), pos);
	}

	public void setTileEntity(CompoundNBT tileEntity, BlockPos pos)
	{
		if (tileEntity == null)
		{
			this.entities.remove(this.getIndex(pos));
		}
		else
		{
			this.entities.put(this.getIndex(pos), new TileEntityEntry(tileEntity, pos));
		}
	}


	public Iterable<TileEntityEntry> getTileEntityEntries()
	{
		return this.entities.values();
	}

	public class TileEntityEntry
	{
		public final CompoundNBT data;

		public final BlockPos pos;

		public TileEntityEntry(CompoundNBT data, BlockPos pos)
		{
			this.data = data;
			this.pos = pos;
		}
	}
}
