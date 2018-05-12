package com.gildedgames.orbis_api.world.instances;

import com.gildedgames.orbis_api.util.mc.BlockPosDimension;
import com.gildedgames.orbis_api.util.mc.NBTHelper;
import net.minecraft.nbt.NBTBase;
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
		public NBTBase writeNBT(final Capability<IPlayerInstances> capability, final IPlayerInstances instance, final EnumFacing side)
		{
			final NBTTagCompound compound = new NBTTagCompound();

			compound.setTag("outside", NBTHelper.write(instance.getOutside()));
			compound.setTag("activeInstances", NBTHelper.write(instance.getInstance()));

			return compound;
		}

		@Override
		public void readNBT(final Capability<IPlayerInstances> capability, final IPlayerInstances instance, final EnumFacing side, final NBTBase nbt)
		{
			final NBTTagCompound compound = (NBTTagCompound) nbt;

			instance.setReturnPosition(NBTHelper.read(compound.getCompoundTag("outside")));
			instance.setInstance(NBTHelper.read(compound.getCompoundTag("activeInstances")));
		}
	}

}
