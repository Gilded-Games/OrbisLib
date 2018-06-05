package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.gildedgames.orbis_api.world.IWorldObject;
import com.gildedgames.orbis_api.world.IWorldObjectChild;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PostGenReplaceLayer implements NBT, IWorldObjectChild
{
	private ItemStack required = ItemStack.EMPTY, replaced = ItemStack.EMPTY;

	private int layerId;

	private IWorldObject worldObjectParent;

	private IFilterOptions options;

	private PostGenReplaceLayer()
	{

	}

	public PostGenReplaceLayer(ItemStack required, ItemStack replaced)
	{
		this.required = required;
		this.replaced = replaced;
		this.options = new FilterOptions();

		this.options.setChoosesPerBlock(false);
	}

	public int getLayerId()
	{
		return this.layerId;
	}

	public void setLayerId(int layerId)
	{
		this.layerId = layerId;
	}

	public ItemStack getRequired()
	{
		return this.required;
	}

	public void setRequired(ItemStack stack)
	{
		this.required = stack;

		this.worldObjectParent.markDirty();
	}

	public ItemStack getReplaced()
	{
		return this.replaced;
	}

	public void setReplaced(ItemStack stack)
	{
		this.replaced = stack;

		this.worldObjectParent.markDirty();
	}

	public IFilterOptions getOptions()
	{
		return this.options;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.setStack("required", this.required);
		funnel.setStack("replaced", this.replaced);
		tag.setInteger("layerId", this.layerId);
		funnel.set("options", this.options);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.required = funnel.getStack("required");
		this.replaced = funnel.getStack("replaced");
		this.layerId = tag.getInteger("layerId");
		this.options = funnel.get("options");
	}

	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(this.layerId);

		return builder.toHashCode();
	}

	@Override
	public IWorldObject getWorldObjectParent()
	{
		return this.worldObjectParent;
	}

	@Override
	public void setWorldObjectParent(IWorldObject parent)
	{
		this.worldObjectParent = parent;
	}
}
