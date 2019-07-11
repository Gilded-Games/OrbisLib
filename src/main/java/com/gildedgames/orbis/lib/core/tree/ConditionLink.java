package com.gildedgames.orbis.lib.core.tree;

import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.nbt.CompoundNBT;

public class ConditionLink implements NBT
{
	public static final ConditionLink OR = new ConditionLink(Property.OR);

	public static final ConditionLink AND = new ConditionLink(Property.AND);

	private Property property;

	private ConditionLink()
	{

	}

	public ConditionLink(Property property)
	{
		this.property = property;
	}

	@Override
	public void write(CompoundNBT tag)
	{
		tag.putString("property", this.property.name());
	}

	@Override
	public void read(CompoundNBT tag)
	{
		this.property = Property.valueOf(tag.getString("property"));
	}

	public Property getProperty()
	{
		return this.property;
	}

	@Override
	public int hashCode()
	{
		return this.property.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}

		if (obj instanceof ConditionLink)
		{
			ConditionLink conditionLink = (ConditionLink) obj;

			return conditionLink.property.equals(this.property);
		}

		return false;
	}

	public enum Property
	{
		OR, AND
	}

}
