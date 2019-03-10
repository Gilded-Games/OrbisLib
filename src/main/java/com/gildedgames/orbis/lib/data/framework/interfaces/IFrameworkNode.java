package com.gildedgames.orbis.lib.data.framework.interfaces;

import com.gildedgames.orbis.lib.data.IDataChild;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.framework.FrameworkData;
import com.gildedgames.orbis.lib.data.pathway.PathwayData;
import com.gildedgames.orbis.lib.data.region.IRegionHolder;
import com.gildedgames.orbis.lib.util.mc.NBT;

import java.util.Collection;

/**
 * Represents data that can be used to randomly choose other data.
 * This is used in FrameworkNode Framework tree.
 * Some implementations are <tt>ScheduleData</tt> and <tt>
 * FrameworkData</tt>.
 * @see FrameworkData
 * @author Emile
 *
 */
public interface IFrameworkNode extends NBT, IDataChild<FrameworkData>, IRegionHolder
{

	BlueprintData getBlueprintData();

	int getMaxEdges();

	/**
	 * Returns all different kinds of pathways that are used in
	 * Entrances in the data of this node.
	 */
	Collection<PathwayData> pathways();
}
