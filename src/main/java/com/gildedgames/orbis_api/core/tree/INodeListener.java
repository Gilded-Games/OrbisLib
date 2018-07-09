package com.gildedgames.orbis_api.core.tree;

public interface INodeListener<DATA, LINK>
{
	void onSetData(INode<DATA, LINK> node, DATA data);
}
