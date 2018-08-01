package com.gildedgames.orbis_api.data.schedules.processors;

import com.gildedgames.orbis_api.core.PlacedEntity;
import com.gildedgames.orbis_api.core.baking.IBakedPosAction;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.data.schedules.IScheduleProcessor;
import com.gildedgames.orbis_api.inventory.InventorySpawnEggs;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Random;

public class ScheduleProcessorSpawnEntities implements IScheduleProcessor
{
	private InventorySpawnEggs spawnEggsInv;

	private BlueprintData dataParent;

	public ScheduleProcessorSpawnEntities()
	{
		this.spawnEggsInv = new InventorySpawnEggs();
	}

	public InventorySpawnEggs getSpawnEggsInventory()
	{
		return this.spawnEggsInv;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("spawnEggsInv", this.spawnEggsInv);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.spawnEggsInv = funnel.get("spawnEggsInv");
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

		this.spawnEggsInv.setDataParent(this.dataParent);
	}

	@Override
	public List<IBakedPosAction> bakeActions(IRegion bounds, Random rand)
	{
		List<IBakedPosAction> actions = Lists.newArrayList();

		for (int i = 0; i < this.getSpawnEggsInventory().getSizeInventory(); i++)
		{
			ItemStack stack = this.getSpawnEggsInventory().getStackInSlot(i);

			if (stack.getItem() instanceof ItemMonsterPlacer)
			{
				BlockPos pos = bounds.getMin();

				pos.add(rand.nextInt(bounds.getWidth()), 0,
						rand.nextInt(bounds.getHeight()));

				PlacedEntity placedEntity = new PlacedEntity(stack, pos);

				actions.add(placedEntity);
			}
		}

		return actions;
	}
}
