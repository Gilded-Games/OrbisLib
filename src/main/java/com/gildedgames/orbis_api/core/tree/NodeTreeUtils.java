package com.gildedgames.orbis_api.core.tree;

import java.util.List;

public class NodeTreeUtils
{
	public static <DATA, LINK> void fetchAllChildren(INode<DATA, LINK> root, List<INode<DATA, LINK>> addTo)
	{
		if (root == null)
		{
			return;
		}

		if (addTo.contains(root))
		{
			return;
		}

		addTo.add(root);

		for (INode<DATA, LINK> child : root.getTree().get(root.getChildrenIds()))
		{
			fetchAllChildren(child, addTo);
		}
	}
}
