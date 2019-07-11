package com.gildedgames.orbis.lib.inventory;

import com.gildedgames.orbis.lib.data.IDataChild;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class InventorySpawnEggs implements IInventory, NBT, IDataChild<BlueprintData>
{

	private final NonNullList<ItemStack> inventory = NonNullList.withSize(16, ItemStack.EMPTY);

	private BlueprintData dataParent;

	public InventorySpawnEggs()
	{

	}

	@Override
	public int getSizeInventory()
	{
		return this.inventory.size();
	}

	@Override
	public boolean isEmpty()
	{
		for (final ItemStack itemstack : this.inventory)
		{
			if (!itemstack.isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getStackInSlot(final int index)
	{
		return index >= 0 && index < this.inventory.size() ? this.inventory.get(index) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(final int index, final int count)
	{
		return ItemStackHelper.getAndRemove(this.inventory, index);
	}

	@Override
	public ItemStack removeStackFromSlot(final int index)
	{
		final ItemStack itemstack = this.inventory.get(index);

		if (itemstack.isEmpty())
		{
			return ItemStack.EMPTY;
		}
		else
		{
			this.inventory.set(index, ItemStack.EMPTY);

			return itemstack;
		}
	}

	@Override
	public void setInventorySlotContents(final int index, final ItemStack stack)
	{
		this.inventory.set(index, stack);

		this.markDirty();
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void markDirty()
	{
		if (this.dataParent != null)
		{
			this.dataParent.markDirty();
		}
	}

	@Override
	public boolean isUsableByPlayer(final PlayerEntity player)
	{
		return true;
	}

	@Override
	public void openInventory(final PlayerEntity player)
	{
	}

	@Override
	public void closeInventory(@Nonnull final PlayerEntity player)
	{
	}

	@Override
	public boolean isItemValidForSlot(final int index, @Nonnull final ItemStack stack)
	{
		return stack.getItem() instanceof SpawnEggItem;
	}

	@Override
	public void clear()
	{
		this.inventory.clear();
	}

	@Override
	public void write(final CompoundNBT output)
	{
		final ListNBT list = new ListNBT();

		for (int i = 0; i < this.inventory.size(); ++i)
		{
			final ItemStack stack = this.inventory.get(i);

			if (!stack.isEmpty())
			{
				final CompoundNBT stackCompound = new CompoundNBT();
				stackCompound.putByte("Slot", (byte) i);

				stack.write(stackCompound);

				list.add(stackCompound);
			}
		}

		output.put("Items", list);
	}

	@Override
	public void read(final CompoundNBT input)
	{
		final ListNBT list = input.getList("Items", 10);

		for (int i = 0; i < list.size(); i++)
		{
			final CompoundNBT compound = list.getCompound(i);

			final int slot = compound.getByte("Slot") & 255;

			this.inventory.set(slot, ItemStack.read(compound));
		}
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
