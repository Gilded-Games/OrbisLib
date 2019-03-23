package com.gildedgames.orbis.lib.world.instances;

import com.gildedgames.orbis.lib.util.mc.BlockPosDimension;
import com.gildedgames.orbis.lib.util.mc.NBTHelper;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class PlayerInstances implements IPlayerInstances
{

	private IInstance activeInstance;

	private BlockPosDimension outside;

	public PlayerInstances()
	{
	}

	@Override
	public IInstance getInstance()
	{
		return this.activeInstance;
	}

	@Override
	public void setInstance(final IInstance instance)
	{
		this.activeInstance = instance;
	}

	@Override
	public BlockPosDimension getOutside()
	{
		return this.outside;
	}

	@Override
	public void setReturnPosition(final BlockPosDimension pos)
	{
		this.outside = pos;
	}

	public static class Storage implements Capability.IStorage<IPlayerInstances>
	{

		@Override
		public INBTBase writeNBT(final Capability<IPlayerInstances> capability, final IPlayerInstances instance, final EnumFacing side)
		{
			final NBTTagCompound compound = new NBTTagCompound();

			compound.put("outside", NBTHelper.write(instance.getOutside()));
			compound.put("activeInstances", NBTHelper.write(instance.getInstance()));

			return compound;
		}

		@Override
		public void readNBT(final Capability<IPlayerInstances> capability, final IPlayerInstances instance, final EnumFacing side, final INBTBase nbt)
		{
			final NBTTagCompound compound = (NBTTagCompound) nbt;

			instance.setReturnPosition(NBTHelper.read(compound.getCompound("outside")));
			instance.setInstance(NBTHelper.read(compound.getCompound("activeInstances")));
		}
	}

}
