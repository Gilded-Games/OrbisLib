package com.gildedgames.orbis.lib.core.baking;

import com.google.common.collect.Lists;

import java.util.List;

public class BakedBlueprintNetwork
{
	private List<BlueprintNetworkNode> nodes = Lists.newArrayList();

	public BakedBlueprintNetwork()
	{
	}

	public void addBakedNode(BlueprintNetworkNode node)
	{
		this.nodes.add(node);
	}

	public List<BlueprintNetworkNode> getNodes()
	{
		return this.nodes;
	}
}
