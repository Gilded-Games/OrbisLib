package com.gildedgames.orbis.lib.core.baking;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BlueprintNetworkNode
{
	private BakedBlueprint baked;

	private int depth, childrenNodeCount;

	private List<PotentialEntrance> usedEntrances = Lists.newArrayList();

	private Iterator<PotentialEntrance> entrancesToConnect;

	public BlueprintNetworkNode(BakedBlueprint baked, int depth, Random rand)
	{
		this.baked = baked;
		this.depth = depth;

		List<PotentialEntrance> entrances = baked.getScheduleLayers().getPotentialEntrances();

		Collections.shuffle(entrances, rand);
		this.entrancesToConnect = entrances.iterator();
	}

	public int getChildrenNodeCount()
	{
		return this.childrenNodeCount;
	}

	public void addChildrenNodeCount(int number)
	{
		this.childrenNodeCount += number;
	}

	public Iterator<PotentialEntrance> getEntrancesToConnect()
	{
		return this.entrancesToConnect;
	}

	public int getDepth()
	{
		return this.depth;
	}

	public BakedBlueprint getBakedData()
	{
		return this.baked;
	}

	public void addUsedEntrance(PotentialEntrance entrance)
	{
		if (this.usedEntrances.contains(entrance))
		{
			throw new IllegalStateException("Entrance has already been used!");
		}

		this.usedEntrances.add(entrance);
	}

	public List<PotentialEntrance> getUsedEntrances()
	{
		return this.usedEntrances;
	}
}
