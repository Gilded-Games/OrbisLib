package com.gildedgames.orbis_api.data.framework.interfaces;

import com.gildedgames.orbis_api.data.IDataChild;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.framework.FrameworkData;
import com.gildedgames.orbis_api.data.pathway.PathwayData;
import com.gildedgames.orbis_api.data.region.IMutableRegion;
import com.gildedgames.orbis_api.util.mc.NBT;

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Represents data that can be used to randomly choose other data.
 * This is used in FrameworkNode Framework tree.
 * Some implementations are <tt>ScheduleData</tt> and <tt>
 * FrameworkData</tt>.
 * @see FrameworkData
 * @author Emile
 *
 */
public interface IFrameworkNode extends NBT, IDataChild<FrameworkData>
{

	IMutableRegion getBounds();

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

	/**
	 * Returns all different kinds of pathways that are used in
	 * Entrances in the data of this node.
	 */
	Collection<PathwayData> pathways();
}
