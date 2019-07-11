package com.gildedgames.orbis.lib.util.mc;

import net.minecraft.nbt.CompoundNBT;

/**
 * Helper interface for NBT-serializable objects.
 */
public interface NBT
{
	/**
	 * Writes this object's state to a {@link CompoundNBT}
	 * @param tag The tag to write to
	 */
	void write(CompoundNBT tag);

	/**
	 * Reads this object's state from a {@link CompoundNBT}
	 * @param tag The tag to write to
	 */
	void read(CompoundNBT tag);
}
