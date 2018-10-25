package com.gildedgames.orbis_api.data.blueprint;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.data.IDataHolder;
import com.gildedgames.orbis_api.data.management.IData;
import com.gildedgames.orbis_api.data.management.IDataIdentifier;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import net.minecraft.nbt.NBTTagCompound;
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
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("id", this.data.getMetadata().getIdentifier());
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		final IDataIdentifier id = funnel.get("id");
		Optional<IData> data = OrbisAPI.services().getProjectManager().findData(id);

		if (data.isPresent())
		{
			this.data = (BlueprintData) data.get();
		}
		else
		{
			OrbisAPI.LOGGER.error("Missing in " + this.getClass().getName() + " : ", id);
		}
	}
}
