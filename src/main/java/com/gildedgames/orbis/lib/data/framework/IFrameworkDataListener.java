package com.gildedgames.orbis.lib.data.framework;

import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.framework.interfaces.IFrameworkNode;
import com.gildedgames.orbis.lib.data.pathway.PathwayData;

public interface IFrameworkDataListener
{

	void onAddNode(IFrameworkNode node);

	void onRemoveNode(IFrameworkNode node);

	void onAddEdge(FrameworkNode n1, FrameworkNode n2);

	void onAddIntersection(PathwayData pathway1, PathwayData pathway2, BlueprintData blueprint);

}
