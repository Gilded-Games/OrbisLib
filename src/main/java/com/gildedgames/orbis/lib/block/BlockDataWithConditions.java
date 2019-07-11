package com.gildedgames.orbis.lib.block;

import com.gildedgames.orbis.lib.data.DataCondition;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class BlockDataWithConditions extends BlockData
{
	private DataCondition replaceCondition = new DataCondition();

	private DataCondition requiredCondition = new DataCondition();

	protected BlockDataWithConditions()
	{
		super();
	}

	public BlockDataWithConditions(final Block block, final float weight)
	{
		super(block);

		this.replaceCondition.setWeight(weight);
	}

	public BlockDataWithConditions(final BlockState state, final float weight)
	{
		super(state);

		this.replaceCondition.setWeight(weight);
	}

	public DataCondition getReplaceCondition()
	{
		return this.replaceCondition;
	}

	public void setReplaceCondition(final DataCondition replaceCondition)
	{
		this.replaceCondition = replaceCondition;
	}

	public DataCondition getRequiredCondition()
	{
		return this.requiredCondition;
	}

	public void setRequiredCondition(final DataCondition requiredCondition)
	{
		this.requiredCondition = requiredCondition;
	}

	public void setReplacementChance(final float chance)
	{
		this.replaceCondition.setWeight(chance);
	}

	@Override
	public void write(final CompoundNBT tag)
	{
		super.write(tag);

		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("replaceCondition", this.replaceCondition);
		funnel.set("requiredCondition", this.requiredCondition);
	}

	@Override
	public void read(final CompoundNBT tag)
	{
		super.read(tag);

		final NBTFunnel funnel = new NBTFunnel(tag);

		this.replaceCondition = funnel.get("replaceCondition");
		this.requiredCondition = funnel.get("requiredCondition");
	}

	@Override
	public int hashCode()
	{
		final HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(super.hashCode());
		builder.append(this.replaceCondition);
		builder.append(this.requiredCondition);

		return builder.toHashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (!(obj instanceof BlockDataWithConditions))
		{
			return false;
		}

		return super.equals(obj);
	}

}
