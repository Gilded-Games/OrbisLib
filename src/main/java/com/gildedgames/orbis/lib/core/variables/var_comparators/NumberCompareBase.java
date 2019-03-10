package com.gildedgames.orbis.lib.core.variables.var_comparators;

import com.gildedgames.orbis.lib.core.variables.IGuiVar;
import com.gildedgames.orbis.lib.core.variables.IGuiVarCompareExpression;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public abstract class NumberCompareBase<NUMBER extends Number & Comparable<NUMBER>> implements IGuiVarCompareExpression<NUMBER>
{
	protected IGuiVar<NUMBER, ?> value;

	private List<IGuiVar<NUMBER, ?>> inputs = Lists.newArrayList();

	protected NumberCompareBase()
	{
		
	}

	public NumberCompareBase(IGuiVar<NUMBER, ?> valueVar)
	{
		this.value = valueVar;
		this.inputs.add(this.value);
	}

	@Override
	public List<IGuiVar<NUMBER, ?>> getInputs()
	{
		return this.inputs;
	}

	@Override
	public void transferData(List<IGuiVar<NUMBER, ?>> prevInputs)
	{
		if (prevInputs.size() >= 1)
		{
			this.value.setData(prevInputs.get(0).getData());
		}
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("value", this.value);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.value = funnel.get("value");

		this.inputs.clear();

		this.inputs.add(this.value);
	}
}