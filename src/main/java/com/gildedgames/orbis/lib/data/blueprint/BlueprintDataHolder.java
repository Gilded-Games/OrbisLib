package com.gildedgames.orbis.lib.data.blueprint;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.data.IDataHolder;
import com.gildedgames.orbis.lib.data.management.IData;
import com.gildedgames.orbis.lib.data.management.IDataIdentifier;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Random;

public class BlueprintDataHolder implements IDataHolder<BlueprintData>
{
	private BlueprintData data;

	private BlueprintDataHolder()
	{

	}

	public BlueprintDataHolder(BlueprintData data)
	{
		this.data = data;
	}

	@Override
	public BlueprintData get(World world, Random random)
	{
		return this.data;
	}

	@Override
	public int getLargestHeight()
	{
		return this.data.getHeight();
	}

	@Override
	public int getLargestWidth()
	{
		return this.data.getWidth();
	}

	@Override
	public int getLargestLength()
	{
		return this.data.getLength();
	}

	@Override
	public void write(CompoundNBT tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("id", this.data.getMetadata().getIdentifier());
	}

	@Override
	public void read(CompoundNBT tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		final IDataIdentifier id = funnel.get("id");
		Optional<IData> data = OrbisLib.services().getProjectManager().findData(id);

		if (data.isPresent())
		{
			this.data = (BlueprintData) data.get();
		}
		else
		{
			OrbisLib.LOGGER.error("Missing in " + this.getClass().getName() + " : ", id);
		}
	}
}
