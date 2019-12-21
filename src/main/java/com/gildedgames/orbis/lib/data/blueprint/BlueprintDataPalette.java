package com.gildedgames.orbis.lib.data.blueprint;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.data.DataCondition;
import com.gildedgames.orbis.lib.data.IDataHolder;
import com.gildedgames.orbis.lib.data.management.IDataIdentifier;
import com.gildedgames.orbis.lib.data.region.IDimensions;
import com.gildedgames.orbis.lib.data.region.Region;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.util.mc.NBT;
import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;

/**
 * TODO: MOVE THIS INTO API PACKAGE AND HAVE A UNIVERSAL PROJECT MANAGER. VERY IMPORTANT.
 *
 * Right now project management is only a part of the Orbis interface itself
 * (the tool), instead of the API. This means that palettes and other objects
 * from the API package cannot attempt to find state.
 */
public class BlueprintDataPalette implements NBT, IDataHolder<BlueprintData>
{

	private final Map<IDataIdentifier, BlueprintData> data = Maps.newHashMap();

	private LinkedHashMap<IDataIdentifier, DataCondition> idToConditions = Maps.newLinkedHashMap();

	private IDimensions largestDim;

	private int minEntrances, maxEntrances;

	public BlueprintDataPalette()
	{

	}

	public Collection<IDataIdentifier> getIDs()
	{
		return this.idToConditions.keySet();
	}

	public Map<IDataIdentifier, DataCondition> getIDToConditions()
	{
		return this.idToConditions;
	}

	public BlueprintData fetchRandom(final World world, final Random rand)
	{
		final float randomValue = rand.nextFloat() * this.totalChance();
		float chanceSum = 0.0f;

		for (final Map.Entry<IDataIdentifier, DataCondition> pair : this.idToConditions.entrySet())
		{
			final DataCondition condition = pair.getValue();

			if (condition.isMet(randomValue, chanceSum, rand, world))
			{
				return this.data.get(pair.getKey());
			}

			chanceSum += condition.getWeight();
		}

		return null;
	}

	public float totalChance()
	{
		float total = 0f;

		for (final Map.Entry<IDataIdentifier, DataCondition> pair : this.idToConditions.entrySet())
		{
			final DataCondition condition = pair.getValue();

			total += condition.getWeight();
		}

		return total;
	}

	public Collection<BlueprintData> getData()
	{
		return this.data.values();
	}

	public IDimensions getLargestDim()
	{
		return this.largestDim;
	}

	public int getMinimumEntrances()
	{
		return this.minEntrances;
	}

	public int getMaximumEntrances()
	{
		return this.maxEntrances;
	}

	private void evaluateEntrances()
	{
		//		int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
		//
		//		for (final BlueprintData blueprint : this.data.values())
		//		{
		//			if (blueprint.getEntrance().size() > max) TODO: Entrances
		//			{
		//				max = blueprint.getEntrance().size();
		//			}
		//
		//			if (blueprint.getEntrance().size() < min)
		//			{
		//				min = blueprint.getEntrance().size();
		//			}
		//		}
		//
		//		this.minEntrances = min;
		//		this.maxEntrances = max;
	}

	private void evaluateLargestInArea()
	{
		int width = Integer.MIN_VALUE;
		int height = Integer.MIN_VALUE;
		int length = Integer.MIN_VALUE;

		for (final BlueprintData blueprint : this.data.values())
		{
			width = Math.max(width, blueprint.getWidth());
			height = Math.max(height, blueprint.getHeight());
			length = Math.max(length, blueprint.getLength());
		}

		this.largestDim = new Region(BlockPos.ORIGIN, new BlockPos(width - 1, height - 1, length - 1));
	}

	public void add(final BlueprintData data, final DataCondition condition)
	{
		this.idToConditions.put(data.getMetadata().getIdentifier(), condition);
		this.data.put(data.getMetadata().getIdentifier(), data);

		this.evaluateLargestInArea();
		this.evaluateEntrances();
	}

	public void remove(final BlueprintData data)
	{
		IDataIdentifier toRemove = null;

		for (final Map.Entry<IDataIdentifier, DataCondition> pair : this.idToConditions.entrySet())
		{
			if (pair.getKey().equals(data.getMetadata().getIdentifier()))
			{
				toRemove = pair.getKey();
				break;
			}
		}

		if (toRemove == null)
		{
			return;
		}

		this.data.remove(toRemove);
		this.idToConditions.remove(toRemove);

		this.evaluateLargestInArea();
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.setMap("idToConditions", this.idToConditions);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.idToConditions = Maps.newLinkedHashMap(funnel.getMap("idToConditions"));

		for (final Map.Entry<IDataIdentifier, DataCondition> pair : this.idToConditions.entrySet())
		{
			final IDataIdentifier id = pair.getKey();
			final Optional<BlueprintData> data = OrbisLib.services().getProjectManager().findData(id);

			if (data.isPresent())
			{
				this.data.put(id, data.get());
			}
			else
			{
				OrbisLib.LOGGER.error("Missing in " + this.getClass().getName() + " : ", id);
			}
		}

		this.evaluateLargestInArea();
		this.evaluateEntrances();
	}

	@Override
	public int hashCode()
	{
		final HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(this.idToConditions);

		return builder.toHashCode();
	}

	@Override
	public BlueprintData get(World world, Random random)
	{
		return this.fetchRandom(world, random);
	}

	@Override
	public int getLargestHeight()
	{
		return this.largestDim.getHeight();
	}

	@Override
	public int getLargestWidth()
	{
		return this.largestDim.getWidth();
	}

	@Override
	public int getLargestLength()
	{
		return this.largestDim.getLength();
	}
}
