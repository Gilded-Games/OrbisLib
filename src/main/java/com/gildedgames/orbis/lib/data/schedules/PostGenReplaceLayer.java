package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.block.BlockFilter;
import com.gildedgames.orbis.lib.block.BlockFilterHelper;
import com.gildedgames.orbis.lib.block.BlockFilterLayer;
import com.gildedgames.orbis.lib.block.BlockFilterType;
import com.gildedgames.orbis.lib.data.IDataChild;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PostGenReplaceLayer implements NBT, IDataChild<BlueprintData>
{
	private BlockFilterLayer layer;

	private BlockFilter filter;

	private ItemStack required = ItemStack.EMPTY, replaced = ItemStack.EMPTY;

	private int layerId;

	private BlueprintData dataParent;

	private IFilterOptions options;

	private PostGenReplaceLayer()
	{
		this.layer = new BlockFilterLayer();

		this.layer.setFilterType(BlockFilterType.ONLY);

		this.filter = new BlockFilter(this.layer);
	}

	public PostGenReplaceLayer(ItemStack required, ItemStack replaced)
	{
		this();

		this.required = required;
		this.replaced = replaced;
		this.options = new FilterOptions();

		this.options.getChoosesPerBlockVar().setData(false);

		this.layer.setRequiredBlocks(BlockFilterHelper.getBlocksFromStack(this.required));
		this.layer.setReplacementBlocks(BlockFilterHelper.getBlocksFromStack(this.replaced));
	}

	public BlockFilterLayer getFilterLayer()
	{
		return this.layer;
	}

	public BlockFilter getFilter()
	{
		return this.filter;
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

		this.dataParent.markDirty();

		this.layer.setRequiredBlocks(BlockFilterHelper.getBlocksFromStack(this.required));
	}

	public ItemStack getReplaced()
	{
		return this.replaced;
	}

	public void setReplaced(ItemStack stack)
	{
		this.replaced = stack;

		this.dataParent.markDirty();

		this.layer.setReplacementBlocks(BlockFilterHelper.getBlocksFromStack(this.replaced));
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

		this.layer.setRequiredBlocks(BlockFilterHelper.getBlocksFromStack(this.required));
		this.layer.setReplacementBlocks(BlockFilterHelper.getBlocksFromStack(this.replaced));

		this.filter.clear();

		this.filter.add(this.layer);

		funnel.set("filter", this.filter);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.required = funnel.getStack("required");
		this.replaced = funnel.getStack("replaced");
		this.layerId = tag.getInteger("layerId");
		this.options = funnel.get("options");
		this.filter = funnel.getWithDefault("filter", () -> this.filter);

		if (this.filter != null)
		{
			this.layer = this.filter.getByIndex(0);
		}
	}

	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(this.layerId);

		return builder.toHashCode();
	}

	@Override
	public Class<? extends BlueprintData> getDataClass()
	{
		return BlueprintData.class;
	}

	@Override
	public BlueprintData getDataParent()
	{
		return this.dataParent;
	}

	@Override
	public void setDataParent(BlueprintData blueprintData)
	{
		this.dataParent = blueprintData;
	}
}
