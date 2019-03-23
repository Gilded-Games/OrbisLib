package com.gildedgames.orbis.lib.inventory;

import com.gildedgames.orbis.lib.data.IDataChild;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
	public boolean isUsableByPlayer(final EntityPlayer player)
	{
		return true;
	}

	@Override
	public void openInventory(final EntityPlayer player)
	{
	}

	@Override
	public void closeInventory(@Nonnull final EntityPlayer player)
	{
	}

	@Override
	public boolean isItemValidForSlot(final int index, @Nonnull final ItemStack stack)
	{
		return stack.getItem() instanceof ItemSpawnEgg;
	}

	@Override
	public int getField(final int id)
	{
		return 0;
	}

	@Override
	public void setField(final int id, final int value)
	{
	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		this.inventory.clear();
	}

	@Override
	public ITextComponent getName()
	{
		return new TextComponentTranslation("inventory.forge");
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Nullable
	@Override
	public ITextComponent getCustomName()
	{
		return null;
	}

	@Override
	public void write(final NBTTagCompound output)
	{
		final NBTTagList list = new NBTTagList();

		for (int i = 0; i < this.inventory.size(); ++i)
		{
			final ItemStack stack = this.inventory.get(i);

			if (!stack.isEmpty())
			{
				final NBTTagCompound stackCompound = new NBTTagCompound();
				stackCompound.putByte("Slot", (byte) i);

				stack.write(stackCompound);

				list.add(stackCompound);
			}
		}

		output.put("Items", list);
	}

	@Override
	public void read(final NBTTagCompound input)
	{
		final NBTTagList list = input.getList("Items", 10);

		for (int i = 0; i < list.size(); i++)
		{
			final NBTTagCompound compound = list.getCompound(i);

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
