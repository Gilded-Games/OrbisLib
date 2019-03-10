package com.gildedgames.orbis.lib.core;

import com.gildedgames.orbis.lib.block.BlockDataContainer;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;

public class BlockDataChunk implements NBT
{

	private ChunkPos pos;

	private BlockDataContainer container;

	private BlockDataChunk()
	{

	}

	public BlockDataChunk(final ChunkPos pos, final BlockDataContainer container)
	{
		this.pos = pos;
		this.container = container;
	}

	public ChunkPos getPos()
	{
		return this.pos;
	}

	public BlockDataContainer getContainer()
	{
		return this.container;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("container", this.container);
		funnel.set("pos", this.pos, NBTFunnel.CHUNK_POS_SETTER);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.container = funnel.get("container");
		this.pos = funnel.get("pos", NBTFunnel.CHUNK_POS_GETTER);
	}
}
