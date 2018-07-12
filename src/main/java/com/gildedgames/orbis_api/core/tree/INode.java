package com.gildedgames.orbis_api.core.tree;

import com.gildedgames.orbis_api.data.IDataChild;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.util.mc.NBT;

import java.util.Collection;
import java.util.List;

public interface INode<DATA, LINK> extends NBT, IDataChild<BlueprintData>
{
	/**
	 * Clears the local links of this node ONLY. Will not go to other nodes and remove
	 * their relationship between that node and this node.
	 *
	 * Links == lists of children ids and parent ids.
	 */
	void clearLocalLinks();

	void listen(INodeListener<DATA, LINK> listener);

	boolean unlisten(INodeListener<DATA, LINK> listener);

	NodeTree<DATA, LINK> getTree();

	void setTree(NodeTree<DATA, LINK> tree);

	boolean addChild(int nodeId, LINK link);

	boolean removeChild(int nodeId);

	Collection<Integer> getChildrenIds();

	boolean hasChild(int nodeId);

	DATA getData();

	void setData(DATA data);

	LINK getLinkToChild(int nodeId);

	Collection<Integer> getParentsIds();

	boolean addParent(int nodeId);

	void removeParent(int nodeId);

	void fetchRoots(List<INode<DATA, LINK>> addTo, List<INode<DATA, LINK>> visitedNodes);

	void fetchAllChildren(List<INode<DATA, LINK>> addTo);

	int getNodeId();

	void setNodeId(int nodeId);

	boolean isDirectionless();

	boolean canLink();

	INode<DATA, LINK> deepClone();
}
