package com.gildedgames.orbis_api.core.tree;

import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.gildedgames.orbis_api.world.IWorldObject;
import com.gildedgames.orbis_api.world.IWorldObjectChild;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public class NodeTree<DATA, LINK> implements NBT, IWorldObjectChild, INodeListener<DATA, LINK>
{
	private LinkedHashMap<Integer, INode<DATA, LINK>> nodes = Maps.newLinkedHashMap();

	private Integer prominentRoot = 0;

	private Set<INodeTreeListener<DATA, LINK>> listeners = Sets.newHashSet();

	private IWorldObject worldObjectParent;

	public NodeTree()
	{

	}

	public NodeTree<DATA, LINK> deepClone()
	{
		NodeTree<DATA, LINK> clone = new NodeTree<>();

		for (Map.Entry<Integer, INode<DATA, LINK>> entry : this.nodes.entrySet())
		{
			Integer id = entry.getKey();
			INode<DATA, LINK> node = entry.getValue();

			clone.nodes.put(id, node.deepClone());
		}

		clone.prominentRoot = this.prominentRoot;
		clone.worldObjectParent = this.worldObjectParent;

		clone.listeners = Sets.newHashSet(this.listeners);

		return clone;
	}

	public void listen(INodeTreeListener<DATA, LINK> listener)
	{
		this.listeners.add(listener);
	}

	public boolean unlisten(INodeTreeListener<DATA, LINK> listener)
	{
		return this.listeners.remove(listener);
	}

	public LinkedHashMap<Integer, INode<DATA, LINK>> getInternalMap()
	{
		return this.nodes;
	}

	public boolean isEmpty()
	{
		return this.nodes.isEmpty();
	}

	public int size()
	{
		return this.nodes.size();
	}

	public INode<DATA, LINK> getProminentRoot()
	{
		return this.nodes.get(this.prominentRoot);
	}

	public void setProminentRoot(Integer prominentRoot)
	{
		this.prominentRoot = prominentRoot;
	}

	public Collection<INode<DATA, LINK>> getNodes()
	{
		return this.nodes.values();
	}

	public boolean containsId(int id)
	{
		return this.nodes.containsKey(id);
	}

	public void put(int id, INode<DATA, LINK> node)
	{
		node.setTree(this);
		node.listen(this);

		node.setNodeId(id);
		node.setWorldObjectParent(this.worldObjectParent);

		this.nodes.put(id, node);

		this.listeners.forEach((l) -> l.onPut(node, id));
	}

	public int add(INode<DATA, LINK> node)
	{
		int id = this.findNextAvailableId();

		this.put(id, node);

		return id;
	}

	public int findNextAvailableId()
	{
		int i = 0;

		while (this.nodes.containsKey(i))
		{
			i++;
		}

		return i;
	}

	public INode<DATA, LINK> remove(int id)
	{
		INode<DATA, LINK> node = this.nodes.remove(id);

		node.setTree(null);
		node.unlisten(this);

		this.listeners.forEach((l) -> l.onRemove(node, id));

		node.setWorldObjectParent(null);

		return node;
	}

	public INode<DATA, LINK> get(int id)
	{
		return this.nodes.get(id);
	}

	public Iterable<INode<DATA, LINK>> get(Collection<Integer> ids)
	{
		return new NodeIterable<>(this.nodes, ids);
	}

	/**
	 * @param node The node you're trying to find an id for.
	 * @return Returns -1 if the node doesn't exist in this tree.
	 */
	public int get(INode<DATA, LINK> node)
	{
		for (Map.Entry<Integer, INode<DATA, LINK>> entry : this.nodes.entrySet())
		{
			int id = entry.getKey();
			INode<DATA, LINK> n = entry.getValue();

			if (n != null && n.equals(node))
			{
				return id;
			}
		}

		return -1;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.setIntMap("nodes", this.nodes);
		tag.setInteger("prominentRoot", this.prominentRoot);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.nodes = Maps.newLinkedHashMap(funnel.getIntMap("nodes"));
		this.prominentRoot = tag.getInteger("prominentRoot");

		this.nodes.values().forEach((n) -> n.setTree(this));
		this.nodes.values().forEach((n) -> n.listen(this));
		this.nodes.values().forEach((n) -> NodeTree.this.listeners.forEach((l) -> l.onPut(n, n.getNodeId())));
	}

	@Override
	public IWorldObject getWorldObjectParent()
	{
		return this.worldObjectParent;
	}

	@Override
	public void setWorldObjectParent(IWorldObject parent)
	{
		this.worldObjectParent = parent;

		this.nodes.values().forEach((n) -> n.setWorldObjectParent(parent));
	}

	@Override
	public void onSetData(INode<DATA, LINK> node, DATA data)
	{
		this.listeners.forEach((l) -> l.onSetData(node, data, node.getNodeId()));
	}

	public static class NodeIterable<DATA, LINK> implements Iterable<INode<DATA, LINK>>
	{
		private Map<Integer, INode<DATA, LINK>> map;

		private Collection<Integer> ids;

		public NodeIterable(Map<Integer, INode<DATA, LINK>> map, Collection<Integer> ids)
		{
			this.map = map;
			this.ids = ids;
		}

		@Override
		public Iterator<INode<DATA, LINK>> iterator()
		{
			return new NodeIterator<>(this.map, this.ids);
		}
	}

	public static class NodeIterator<DATA, LINK> implements Iterator<INode<DATA, LINK>>
	{
		private int i;

		private Map<Integer, INode<DATA, LINK>> map;

		private Collection<Integer> ids;

		public NodeIterator(Map<Integer, INode<DATA, LINK>> map, Collection<Integer> ids)
		{
			this.map = map;
			this.ids = ids;
		}

		@Override
		public boolean hasNext()
		{
			return this.i < this.ids.size();
		}

		@Override
		public INode<DATA, LINK> next()
		{
			INode<DATA, LINK> node = null;

			Optional<Integer> nodeId = this.ids.stream().skip(this.i).findFirst();

			if (nodeId.isPresent())
			{
				node = this.map.get(nodeId.get());
			}

			this.i++;

			return node;
		}
	}
}
