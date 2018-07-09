package com.gildedgames.orbis_api.core.tree;

public interface INodeTreeListener<DATA, LINK>
{

	void onSetData(INode<DATA, LINK> node, DATA data, int id);

	void onPut(INode<DATA, LINK> node, int id);

	void onRemove(INode<DATA, LINK> node, int id);

}
