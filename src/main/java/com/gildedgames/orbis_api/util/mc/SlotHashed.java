package com.gildedgames.orbis_api.util.mc;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SlotHashed extends Slot
{

	public int oldX, oldY;

	public SlotHashed(final IInventory inventory, final int index, final int xPosition, final int yPosition)
	{
		super(inventory, index, xPosition, yPosition);

		this.oldX = xPosition;
		this.oldY = yPosition;
	}

	public void resetPos()
	{
		this.xPos = this.oldX;
		this.yPos = this.oldY;
	}

	@Override
	public boolean isItemValid(final ItemStack stack)
	{
		return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
	}

	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(this.getSlotIndex());

		return builder.toHashCode();
	}
}
