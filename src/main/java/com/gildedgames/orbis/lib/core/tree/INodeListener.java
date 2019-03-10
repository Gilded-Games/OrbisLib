package com.gildedgames.orbis.lib.core.tree;

public interface INodeListener<DATA, LINK>
{
	void onSetData(INode<DATA, LINK> node, DATA data);
}
