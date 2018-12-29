package com.gildedgames.orbis_api.block;

import com.gildedgames.orbis_api.OrbisLib;
import com.gildedgames.orbis_api.core.exceptions.OrbisMissingModsException;
import com.gildedgames.orbis_api.data.management.IData;
import com.gildedgames.orbis_api.data.management.IDataMetadata;
import com.gildedgames.orbis_api.data.management.impl.DataMetadata;
import com.gildedgames.orbis_api.data.region.IDimensions;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.data.region.IShape;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.gildedgames.orbis_api.world.IWorldObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockDataContainer implements NBT, IDimensions, IData
{

	private Int2ObjectOpenHashMap<Block> localIdToBlock;

	private Object2IntOpenHashMap<Block> blockToLocalId;

	private short[] blocks;

	private byte[] blocksMeta;

	private Int2ObjectOpenHashMap<NBTTagCompound> entities = new Int2ObjectOpenHashMap<>();

	private int width, height, length;

	private IDataMetadata metadata;

	private int nextLocalId;

	private IBlockState defaultBlock;

	public BlockDataContainer()
	{
		this(Blocks.AIR.getDefaultState());
	}

	public BlockDataContainer(IBlockState defaultBlock)
	{
		this.localIdToBlock = new Int2ObjectOpenHashMap<>();
		this.blockToLocalId = new Object2IntOpenHashMap<>();

		this.localIdToBlock.put(-1, defaultBlock.getBlock());
		this.blockToLocalId.put(defaultBlock.getBlock(), -1);

		this.metadata = new DataMetadata();

		this.defaultBlock = defaultBlock;
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

		this.blocks = new short[this.getVolume()];

		Arrays.fill(this.blocks, (short) -1);

		this.blocksMeta = new byte[this.getVolume()];
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
			container.setTileEntity(world.getTileEntity(pos), pos);
		}
		return container;
	}

	public int getVolume()
	{
		return this.width * this.height * this.length;
	}

	private int getIndex(final int x, final int y, final int z)
	{
		return z + y * this.length + x * this.height * this.length;
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

	public void copyBlockFrom(BlockDataContainer data, int otherX, int otherY, int otherZ, int thisX, int thisY, int thisZ)
			throws ArrayIndexOutOfBoundsException
	{
		int indexThis = this.getIndex(thisX, thisY, thisZ);
		int indexOther = data.getIndex(otherX, otherY, otherZ);

		Block otherBlock = data.localIdToBlock.get(data.blocks[indexOther]);

		if (!this.blockToLocalId.containsKey(otherBlock))
		{
			int id = this.nextLocalId++;

			this.localIdToBlock.put(id, otherBlock);
			this.blockToLocalId.put(otherBlock, id);
		}

		this.blocks[indexThis] = (short) this.blockToLocalId.getInt(otherBlock);
		this.blocksMeta[indexThis] = data.blocksMeta[indexOther];

		if (data.entities.get(indexOther) != null)
		{
			this.entities.put(indexThis, data.entities.get(indexOther).copy());
		}
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

	public IBlockState getBlockState(final int index)
	{
		int id = this.blocks[index];
		if (id < 0)
		{
			return this.getDefaultBlock();
		}
		return this.localIdToBlock.get(id).getStateFromMeta(this.blocksMeta[index]);
	}

	public void setBlockState(final IBlockState state, final int x, final int y, final int z) throws ArrayIndexOutOfBoundsException
	{
		final int index = this.getIndex(x, y, z);

		if (!this.blockToLocalId.containsKey(state.getBlock()))
		{
			int id = this.nextLocalId++;

			this.localIdToBlock.put(id, state.getBlock());
			this.blockToLocalId.put(state.getBlock(), id);
		}

		this.blocks[index] = (short) this.blockToLocalId.getInt(state.getBlock());
		this.blocksMeta[index] = (byte) state.getBlock().getMetaFromState(state);
	}

	public void setBlockState(final IBlockState state, final BlockPos pos) throws ArrayIndexOutOfBoundsException
	{
		this.setBlockState(state, pos.getX(), pos.getY(), pos.getZ());
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

	protected IBlockState getDefaultBlock()
	{
		return this.defaultBlock;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		tag.setInteger("nextLocalId", this.nextLocalId);

		tag.setInteger("width", this.getWidth());
		tag.setInteger("height", this.getHeight());
		tag.setInteger("length", this.getLength());

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
			int blockId;
			int meta;

			if (this.blocks[i] >= 0)
			{
				blockId = this.blocks[i];
				meta = this.blocksMeta[i];
			}
			else
			{
				blockId = -1;
				meta = this.getDefaultBlock().getBlock().getMetaFromState(this.getDefaultBlock());
			}

			final ResourceLocation identifier = OrbisLib.services().registrar().getIdentifierFor(this.localIdToBlock.get(blockId));

			if (!identifiers.containsKey(blockId))
			{
				identifiers.put(blockId, identifier);
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

			final NBTTagCompound tileEntity = this.entities.get(i);

			if (tileEntity != null)
			{
				tileEntities.put(i, tileEntity);
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

			data.setString("mod", identifier.getResourceDomain());
			data.setString("name", identifier.getResourcePath());
			data.setInteger("id", entry.getKey());

			identifierList.appendTag(data);
		}

		tag.setTag("identifiers", identifierList);

		/**
		 * Saving tile entity data
		 */
		final NBTTagList tileEntityList = new NBTTagList();

		for (final Map.Entry<Integer, NBTTagCompound> entry : tileEntities.entrySet())
		{
			final NBTTagCompound data = new NBTTagCompound();

			data.setTag("tileEnt", entry.getValue());
			data.setInteger("orbisTEIndex", entry.getKey());

			tileEntityList.appendTag(data);
		}

		tag.setTag("tileEntities", tileEntityList);

		tag.setByteArray("blocks", blocks);
		tag.setByteArray("metadata", metadata);

		tag.setBoolean("addBlocks_null", addBlocks == null);

		if (addBlocks != null)
		{
			tag.setByteArray("addBlocks", addBlocks);
		}
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		this.nextLocalId = tag.getInteger("nextLocalId");

		this.width = tag.getInteger("width");
		this.height = tag.getInteger("height");
		this.length = tag.getInteger("length");

		/** Read back identifier list so we can figure out which
		 * ids belong to what blocks (as well as their parent mods)
		 */
		final NBTTagList identifierList = tag.getTagList("identifiers", 10);
		final Set<String> missingMods = new HashSet<>();

		for (int i = 0; i < identifierList.tagCount(); i++)
		{
			final NBTTagCompound data = identifierList.getCompoundTagAt(i);

			final String modname = data.getString("mod");
			final String blockname = data.getString("name");

			final Block block = OrbisLib.services().registrar().findBlock(new ResourceLocation(modname, blockname));

			/**
			 * Add to missing mods list if we can't find the block with our registrar
			 */
			if (block == null)
			{
				data.getInteger("id");
				missingMods.add(modname);
			}
			else
			{
				int id = data.getInteger("id");

				this.localIdToBlock.put(id, block);
				this.blockToLocalId.put(block, id);
			}
		}

		if (!tag.hasKey("nextLocalId"))
		{
			this.localIdToBlock.keySet().stream().mapToInt(Integer::intValue).max().ifPresent((value) -> this.nextLocalId = value + 1);
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
		final NBTTagList tileEntityList = tag.getTagList("tileEntities", 10);

		for (int i = 0; i < tileEntityList.tagCount(); i++)
		{
			final NBTTagCompound data = tileEntityList.getCompoundTagAt(i);

			final NBTTagCompound tileEntData = data.getCompoundTag("tileEnt");
			tileEntities.put(data.getInteger("orbisTEIndex"), tileEntData);
		}

		final byte[] blockComp = tag.getByteArray("blocks");
		final byte[] metadata = tag.getByteArray("metadata");
		final byte[] addBlocks = tag.getBoolean("addBlocks_null") ? null : tag.getByteArray("addBlocks");

		if (blockComp.length != this.getVolume())
		{
			throw new IllegalStateException("Size of data mismatched dimensions given");
		}

		this.blocks = new short[blockComp.length];
		this.blocksMeta = new byte[blockComp.length];
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

			final Block block = this.localIdToBlock.get(finalId);

			if (block == null)
			{
				throw new NullPointerException("Wasn't able to load block with id " + finalId);
			}

			this.blocks[i] = (short) finalId;
			this.blocksMeta[i] = metadata[i];

			NBTTagCompound entity = tileEntities.get(i);

			if (entity != null)
			{
				this.entities.put(i, entity);
			}
		}
	}

	@Override
	public void preSaveToDisk(final IWorldObject object)
	{

	}

	public BlockDataContainer createNewContainer()
	{
		return new BlockDataContainer();
	}

	@Override
	public BlockDataContainer clone()
	{
		final BlockDataContainer data = new BlockDataContainer();

		data.nextLocalId = this.nextLocalId;

		data.blocks = new short[this.blocks.length];
		data.blocksMeta = new byte[this.blocksMeta.length];

		System.arraycopy(this.blocks, 0, data.blocks, 0, this.blocks.length);
		System.arraycopy(this.blocksMeta, 0, data.blocksMeta, 0, this.blocksMeta.length);

		data.localIdToBlock = new Int2ObjectOpenHashMap<>(this.localIdToBlock);
		data.blockToLocalId = new Object2IntOpenHashMap<>(this.blockToLocalId);

		NBTTagCompound tag;

		this.metadata.write(tag = new NBTTagCompound());

		data.metadata = new DataMetadata();
		data.metadata.read(tag);

		data.entities.clear();

		for (int i : this.entities.keySet())
		{
			data.entities.put(i, this.entities.get(i).copy());
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

	public NBTTagCompound getTileEntity(int x, int y, int z)
	{
		return this.entities.get(this.getIndex(x, y, z));
	}

	public void setTileEntity(NBTTagCompound tileEntity, int x, int y, int z)
	{
		if (tileEntity == null)
		{
			this.entities.remove(this.getIndex(x, y, z));
		}
		else
		{
			this.entities.put(this.getIndex(x, y, z), tileEntity.copy());
		}
	}

	public void setTileEntity(TileEntity entity, BlockPos translated)
	{
		if (entity == null)
		{
			this.entities.remove(this.getIndex(translated.getX(), translated.getY(), translated.getZ()));
		}
		else
		{
			this.entities.put(this.getIndex(translated.getX(), translated.getY(), translated.getZ()), entity.writeToNBT(new NBTTagCompound()));
		}
	}
}
