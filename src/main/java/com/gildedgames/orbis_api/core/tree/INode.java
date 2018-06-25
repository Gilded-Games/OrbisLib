package com.gildedgames.orbis_api.core.tree;

import com.gildedgames.orbis_api.util.mc.NBT;
import com.gildedgames.orbis_api.world.IWorldObjectChild;

import java.util.Collection;
import java.util.List;

public interface INode<DATA, LINK> extends NBT, IWorldObjectChild
{

	NodeTree<DATA, LINK> getTree();

	void setTree(NodeTree<DATA, LINK> tree);

	void addChild(int nodeId, LINK link);

	boolean removeChild(int nodeId);

	Collection<Integer> getChildrenIds();

	boolean hasChild(int nodeId);

	DATA getData();

	void setData(DATA data);

	LINK getLinkToChild(int nodeId);

	Collection<Integer> getParentsIds();

	void addParent(int nodeId);

	void removeParent(int nodeId);

	void fetchRoots(List<INode<DATA, LINK>> addTo, List<INode<DATA, LINK>> visitedNodes);

	void fetchAllChildren(List<INode<DATA, LINK>> addTo);

	int getNodeId();

	void setNodeId(int nodeId);

	boolean isDirectionless();

}
