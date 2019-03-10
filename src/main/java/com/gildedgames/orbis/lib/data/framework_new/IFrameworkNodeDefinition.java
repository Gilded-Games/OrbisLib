package com.gildedgames.orbis.lib.data.framework_new;

import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;

import java.util.List;
import java.util.Random;

public interface IFrameworkNodeDefinition
{
	/**
	 * Returns the possible values this data can take. The data
	 * is ordered in a random way using the Random object given.
	 */
	List<BlueprintData> possibleValues(Random random);

	/**
	 * Returns the maximum amount of edges that can connect to
	 * this node.
	 */
	int maxEdges();
}
