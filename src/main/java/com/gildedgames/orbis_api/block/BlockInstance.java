package com.gildedgames.orbis_api.block;

import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class BlockInstance implements NBT
{

	private BlockData data;

	private BlockPos pos;

	private BlockInstance()
	{

	}

	public BlockInstance(final BlockData data, final BlockPos pos)
	{
		this.data = data;
		this.pos = pos;
	}

	public BlockData getData()
	{
		return this.data;
	}

	public BlockPos getPos()
	{
		return this.pos;
	}

	@Override
	public boolean equals(final Object obj)
	{
		boolean flag = false;

		if (obj == this)
		{
			flag = true;
		}
		else if (obj instanceof BlockInstance)
		{
			final BlockInstance o = (BlockInstance) obj;
			final EqualsBuilder builder = new EqualsBuilder();

			builder.append(this.data, o.data);
			builder.append(this.pos, o.pos);

			flag = builder.isEquals();
		}

		return flag;
	}

	@Override
	public String toString()
	{
		return "BlockInstance - POS: " + this.pos.toString() + ", BLOCKDATA: " + this.data.toString();
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("d", this.data);
		funnel.setPos("p", this.pos);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.data = funnel.get("d");
		this.pos = funnel.getPos("p");
	}
}
