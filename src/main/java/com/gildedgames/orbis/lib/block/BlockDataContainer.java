package com.gildedgames.orbis.lib.block;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.core.exceptions.OrbisMissingModsException;
import com.gildedgames.orbis.lib.data.management.IData;
import com.gildedgames.orbis.lib.data.management.IDataMetadata;
import com.gildedgames.orbis.lib.data.management.impl.DataMetadata;
import com.gildedgames.orbis.lib.data.region.IDimensions;
import com.gildedgames.orbis.lib.data.region.IRegion;
import com.gildedgames.orbis.lib.data.region.IShape;
import com.gildedgames.orbis.lib.util.mc.NBT;
import com.gildedgames.orbis.lib.world.IWorldObject;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockDataContainer implements NBT, IDimensions, IData
{
	private int[] blocks;

	private Int2ObjectOpenHashMap<TileEntityEntry> entities = new Int2ObjectOpenHashMap<>();

	private Int2ObjectOpenHashMap<IBlockState> idToState = new Int2ObjectOpenHashMap<>();

	private Object2IntOpenCustomHashMap<IBlockState> stateToId =
			new Object2IntOpenCustomHashMap<>(new Hash.Strategy<IBlockState>()
			{
				@Override
				public int hashCode(IBlockState o)
				{
					return System.identityHashCode(o);
				}

				@Override
				public boolean equals(IBlockState a, IBlockState b)
				{
					return a == b;
				}
			});

	private int width, height, length;

	private IDataMetadata metadata;

	private IBlockState defaultBlock;

	private int nextID;

	public BlockDataContainer(BlockDataContainer other, int width, int height, int length)
	{
		this.entities = new Int2ObjectOpenHashMap<>(this.entities);

		this.defaultBlock = other.defaultBlock;

		this.idToState = new Int2ObjectOpenHashMap<>(other.idToState);
		this.stateToId = new Object2IntOpenCustomHashMap<>(other.stateToId, other.stateToId.strategy());

		this.width = width;
		this.height = height;
		this.length = length;

		this.blocks = new int[this.getVolume()];

		this.nextID = other.nextID;
		this.metadata = other.metadata;

		Arrays.fill(this.blocks, -1);
	}

	public BlockDataContainer()
	{
		this(Blocks.AIR.getDefaultState());
	}

	public BlockDataContainer(IBlockState defaultBlock)
	{
		if (defaultBlock == null)
		{
			throw new IllegalArgumentException("Default block cannot be null");
		}

		this.metadata = new DataMetadata();

		this.defaultBlock = defaultBlock;

		this.idToState.put(-1, this.defaultBlock);
		this.stateToId.put(this.defaultBlock, -1);

		this.stateToId.defaultReturnValue(Integer.MIN_VALUE);
		this.idToState.defaultReturnValue(null);
	}

	/**
	 * @param width
	 * @param height Maximum height possible is 256
	 * @param length
	 */
	public BlockDataContainer(final IBlockState defaultBlock, final int width, final int height, final int length)
	{
		this(defaultBlock);

		this.width = width;
		this.height = Math.min(256, height);
		this.length = length;

		this.blocks = new int[this.getVolume()];

		Arrays.fill(this.blocks, -1);
	}

	public BlockDataContainer(IBlockState defaultBlock, final IRegion region)
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
			final IBlockState state = world.getBlockState(pos);

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

	public IBlockState getBlockState(final BlockPos pos)
	{
		return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
	}

	public IBlockState getBlockState(final int x, final int y, final int z)
	{
		return this.getBlockState(this.getIndex(x, y, z));
	}

	private IBlockState getBlockState(final int index)
	{
		return this.idToState.get(this.blocks[index]);
	}

	public void setBlockState(final IBlockState state, final int x, final int y, final int z) throws ArrayIndexOutOfBoundsException
	{
		this.setBlockState(state, this.getIndex(x, y, z));
	}

	private void setBlockState(final IBlockState state, final int index)
	{
		int value = this.stateToId.getInt(state);

		if (value == this.stateToId.defaultReturnValue())
		{
			value = this.nextID++;

			this.stateToId.put(state, value);
			this.idToState.put(value, state);
		}

		this.blocks[index] = value;
	}

	public void setBlockState(final IBlockState state, final BlockPos pos) throws ArrayIndexOutOfBoundsException
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
			IBlockState state = data.idToState.get(block);
			IBlockState stateRotated = state.rotate(rotation);

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

	public IBlockState getDefaultBlock()
	{
		return this.defaultBlock;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		int nextLocalId = 0;

		Int2IntOpenHashMap blockToLocalId = new Int2IntOpenHashMap();

		tag.putInt("width", this.getWidth());
		tag.putInt("height", this.getHeight());
		tag.putInt("length", this.getLength());

		final byte[] blocks = new byte[this.getVolume()];
		byte[] addBlocks = null;

		final byte[] metadata = new byte[this.getVolume()];

		/**
		 * Create maps to compress data written
		 * Instead of writing entire ResourceLocations for each block, we'll
		 * be writing an integer and the map will point us to the appropriate
		 * ResourceLocation when reading back
		 */
		final Int2ObjectOpenHashMap<ResourceLocation> identifiers = new Int2ObjectOpenHashMap<>();
		final Int2ObjectOpenHashMap<NBTTagCompound> tileEntities = new Int2ObjectOpenHashMap<>();

		for (int i = 0; i < this.blocks.length; i++)
		{
			IBlockState state;

			int blockId;
			int meta;

			if (this.blocks[i] >= 0)
			{
				int blockRaw = this.blocks[i];

				if (!blockToLocalId.containsKey(blockRaw))
				{
					int id = nextLocalId++;

					blockToLocalId.put(blockRaw, id);
				}

				state = this.getBlockState(i);
				blockId = blockToLocalId.get(blockRaw);
				meta = state.getBlock().getMetaFromState(state);

				if (!identifiers.containsKey(blockId))
				{
					ResourceLocation identifier = OrbisLib.services().registrar().getIdentifierFor(state.getBlock());

					identifiers.put(blockId, identifier);
				}
			}
			else
			{
				state = this.defaultBlock;

				blockId = -1;
				meta = this.getDefaultBlock().getBlock().getMetaFromState(this.getDefaultBlock());
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
			metadata[i] = (byte) meta;

			if (state.getBlock().hasTileEntity(state))
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
		final NBTTagList identifierList = new NBTTagList();

		for (final Map.Entry<Integer, ResourceLocation> entry : identifiers.entrySet())
		{
			final NBTTagCompound data = new NBTTagCompound();

			final ResourceLocation identifier = entry.getValue();

			data.putString("mod", identifier.getNamespace());
			data.putString("name", identifier.getPath());
			data.putInt("id", entry.getKey());

			identifierList.add(data);
		}

		tag.put("identifiers", identifierList);

		/**
		 * Saving tile entity data
		 */
		final NBTTagList tileEntityList = new NBTTagList();

		for (final Map.Entry<Integer, NBTTagCompound> entry : tileEntities.entrySet())
		{
			final NBTTagCompound data = new NBTTagCompound();

			data.put("tileEnt", entry.getValue());
			data.putInt("orbisTEIndex", entry.getKey());

			tileEntityList.add(data);
		}

		tag.put("tileEntities", tileEntityList);

		tag.putByteArray("blocks", blocks);
		tag.putByteArray("metadata", metadata);

		tag.putBoolean("addBlocks_null", addBlocks == null);

		if (addBlocks != null)
		{
			tag.putByteArray("addBlocks", addBlocks);
		}
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		Int2ObjectOpenHashMap<Block> localIdToBlock = new Int2ObjectOpenHashMap<>();
		localIdToBlock.put(-1, this.defaultBlock.getBlock());

		this.width = tag.getInt("width");
		this.height = tag.getInt("height");
		this.length = tag.getInt("length");

		/** Read back identifier list so we can figure out which
		 * ids belong to what blocks (as well as their parent mods)
		 */
		final NBTTagList identifierList = tag.getList("identifiers", 10);
		final Set<String> missingMods = new HashSet<>();

		for (int i = 0; i < identifierList.size(); i++)
		{
			final NBTTagCompound data = identifierList.getCompound(i);

			final String modname = data.getString("mod");
			final String blockname = data.getString("name");

			final Block block = OrbisLib.services().registrar().findBlock(new ResourceLocation(modname, blockname));

			/**
			 * Add to missing mods list if we can't find the block with our registrar
			 */
			if (block == null)
			{
				data.getInt("id");
				missingMods.add(modname);
			}
			else
			{
				int id = data.getInt("id");

				localIdToBlock.put(id, block);
			}
		}

		if (!missingMods.isEmpty())
		{
			// TODO: Add modId missing parameter, currently empty
			throw new OrbisMissingModsException("Failed loading BlockDataContainer", missingMods, "");
		}

		/**
		 * Read back tile entities
		 */
		final Int2ObjectOpenHashMap<NBTTagCompound> tileEntities = new Int2ObjectOpenHashMap<>();
		final NBTTagList tileEntityList = tag.getList("tileEntities", 10);

		for (int i = 0; i < tileEntityList.size(); i++)
		{
			final NBTTagCompound data = tileEntityList.getCompound(i);

			final NBTTagCompound tileEntData = data.getCompound("tileEnt");
			tileEntities.put(data.getInt("orbisTEIndex"), tileEntData);
		}

		final byte[] blockComp = tag.getByteArray("blocks");
		final byte[] metadata = tag.getByteArray("metadata");
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

			Block block = localIdToBlock.get(finalId);
			IBlockState state = block.getStateFromMeta(metadata[i]);

			this.setBlockState(state, i);

			if (block.hasTileEntity(state))
			{
				NBTTagCompound entity = tileEntities.get(i);

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

		data.idToState = new Int2ObjectOpenHashMap<>(this.idToState);
		data.stateToId = new Object2IntOpenCustomHashMap<>(this.stateToId, this.stateToId.strategy());

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
		this.setTileEntity(tileEntity.write(new NBTTagCompound()), pos);
	}

	public void setTileEntity(NBTTagCompound tileEntity, BlockPos pos)
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
		public final NBTTagCompound data;

		public final BlockPos pos;

		public TileEntityEntry(NBTTagCompound data, BlockPos pos)
		{
			this.data = data;
			this.pos = pos;
		}
	}
}
