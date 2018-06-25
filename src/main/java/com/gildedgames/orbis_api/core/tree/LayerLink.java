package com.gildedgames.orbis_api.core.tree;

import com.gildedgames.orbis_api.util.mc.NBT;
import net.minecraft.nbt.NBTTagCompound;

public class LayerLink implements NBT
{
	public static final LayerLink DEFAULT = new LayerLink(Property.DEFAULT);

	private Property property;

	private LayerLink()
	{

	}

	public LayerLink(Property property)
	{
		this.property = property;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		tag.setString("property", this.property.name());
	}

	@Override
	public void read(NBTTagCompound tag)
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

		if (obj instanceof LayerLink)
		{
			LayerLink layerLink = (LayerLink) obj;

			return layerLink.property.equals(this.property);
		}

		return false;
	}

	public enum Property
	{
		DEFAULT
	}

}
