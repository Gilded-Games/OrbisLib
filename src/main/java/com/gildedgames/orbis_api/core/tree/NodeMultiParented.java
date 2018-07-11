package com.gildedgames.orbis_api.core.tree;

import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.gildedgames.orbis_api.world.IWorldObject;
import com.gildedgames.orbis_api.world.IWorldObjectChild;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class NodeMultiParented<DATA extends NBT, LINK extends NBT> implements INode<DATA, LINK>
{
	private LinkedHashMap<Integer, LINK> children = Maps.newLinkedHashMap();

	private Set<Integer> parents = Sets.newHashSet();

	private Set<INodeListener<DATA, LINK>> listeners;

	private int nodeId;

	private DATA data;

	private boolean isDirectionless;

	private NodeTree<DATA, LINK> tree;

	private IWorldObject worldObjectParent;

	private boolean canLink;

	private NodeMultiParented()
	{

	}

	public NodeMultiParented(DATA data, boolean isDirectionless)
	{
		this(data, isDirectionless, true);
	}

	public NodeMultiParented(DATA data, boolean isDirectionless, boolean canLink)
	{
		this.data = data;
		this.isDirectionless = isDirectionless;
		this.canLink = canLink;
	}

	@Override
	public void listen(INodeListener<DATA, LINK> listener)
	{
		if (this.listeners == null)
		{
			this.listeners = Sets.newHashSet();
		}

		this.listeners.add(listener);
	}

	@Override
	public boolean unlisten(INodeListener<DATA, LINK> listener)
	{
		if (this.listeners == null)
		{
			return false;
		}

		return this.listeners.remove(listener);
	}

	@Override
	public NodeTree<DATA, LINK> getTree()
	{
		return this.tree;
	}

	@Override
	public void setTree(NodeTree<DATA, LINK> tree)
	{
		this.tree = tree;
	}

	@Override
	public void addChild(int nodeId, LINK link)
	{
		if (!this.canLink)
		{
			throw new RuntimeException("Tried to add a child to an INode that has canLink disabled. Should not be able to link children or parents.");
		}

		if (this.tree == null)
		{
			throw new RuntimeException("Tried to add a child to an INode that doesn't have a NodeTree setUsedData to it.");
		}

		INode<DATA, LINK> node = this.tree.get(nodeId);

		if (!node.hasChild(this.nodeId) && !this.children.containsKey(nodeId) && node != this)
		{
			if (this.isDirectionless && this.children.size() >= 1)
			{
				this.children.keySet().stream().findFirst().ifPresent((c) ->
				{
					INode<DATA, LINK> child = this.tree.get(c);

					if (child != null)
					{
						child.removeParent(this.nodeId);
					}
				});

				this.children.clear();
			}

			node.addParent(this.nodeId);

			this.children.put(nodeId, link);

			if (this.worldObjectParent != null)
			{
				this.worldObjectParent.markDirty();
			}
		}
	}

	@Override
	public boolean removeChild(int nodeId)
	{
		if (!this.canLink)
		{
			throw new RuntimeException("Tried to remove a child to an INode that has canLink disabled. Should not be able to link children or parents.");
		}

		if (this.tree == null)
		{
			throw new RuntimeException("Tried to remove a child from an INode that doesn't have a NodeTree setUsedData to it.");
		}

		if (!this.children.containsKey(nodeId) || !this.tree.containsId(nodeId))
		{
			return false;
		}

		INode<DATA, LINK> node = this.tree.get(nodeId);

		node.removeParent(this.nodeId);

		boolean removed = this.children.containsKey(nodeId);

		this.children.remove(nodeId);

		if (this.worldObjectParent != null)
		{
			this.worldObjectParent.markDirty();
		}

		return removed;
	}

	@Override
	public Collection<Integer> getChildrenIds()
	{
		return this.children.keySet();
	}

	@Override
	public boolean hasChild(int nodeId)
	{
		return this.children.containsKey(nodeId);
	}

	@Override
	public DATA getData()
	{
		return this.data;
	}

	@Override
	public void setData(DATA data)
	{
		this.data = data;

		if (this.worldObjectParent != null)
		{
			this.worldObjectParent.markDirty();
		}

		if (this.data instanceof IWorldObjectChild)
		{
			((IWorldObjectChild) this.data).setWorldObjectParent(this.worldObjectParent);
		}

		if (this.listeners != null)
		{
			this.listeners.forEach((l) -> l.onSetData(this, data));
		}
	}

	@Override
	public LINK getLinkToChild(int nodeId)
	{
		return this.children.get(nodeId);
	}

	@Override
	public Collection<Integer> getParentsIds()
	{
		return this.parents;
	}

	@Override
	public void addParent(int nodeId)
	{
		if (!this.canLink)
		{
			throw new RuntimeException("Tried to add a parent to an INode that has canLink disabled. Should not be able to link children or parents.");
		}

		if (this.tree == null)
		{
			throw new RuntimeException("Tried to add a parent to an INode that doesn't have a NodeTree setUsedData to it.");
		}

		if (this.isDirectionless)
		{
			if (this.parents.size() >= 1)
			{
				this.parents.stream().findFirst().ifPresent((c) ->
				{
					INode<DATA, LINK> child = this.tree.get(c);

					if (child != null)
					{
						child.removeChild(this.nodeId);
					}
				});

				this.parents.clear();
			}
			else
			{
				List<INode<DATA, LINK>> roots = Lists.newArrayList();

				this.fetchRoots(roots, Lists.newArrayList());

				if (roots.contains(this))
				{
					for (int childNodeId : this.getChildrenIds())
					{
						INode<DATA, LINK> child = this.tree.get(childNodeId);

						child.removeParent(this.nodeId);
					}

					this.getChildrenIds().clear();
				}
			}
		}

		this.parents.add(nodeId);

		if (this.worldObjectParent != null)
		{
			this.worldObjectParent.markDirty();
		}
	}

	@Override
	public void removeParent(int nodeId)
	{
		if (!this.canLink)
		{
			throw new RuntimeException("Tried to remove a parent to an INode that has canLink disabled. Should not be able to link children or parents.");
		}

		if (!this.parents.contains(nodeId))
		{
			return;
		}

		this.parents.remove(nodeId);

		if (this.worldObjectParent != null)
		{
			this.worldObjectParent.markDirty();
		}
	}

	@Override
	public void fetchRoots(List<INode<DATA, LINK>> addTo, List<INode<DATA, LINK>> visitedNodes)
	{
		if (this.tree == null)
		{
			throw new RuntimeException("Tried to fetch roots from a INode that doesn't have a NodeTree setUsedData to it.");
		}

		visitedNodes.add(this);

		if (this.parents.isEmpty())
		{
			addTo.add(this);

			return;
		}

		for (Integer parentNodeId : this.parents)
		{
			INode<DATA, LINK> parent = this.tree.get(parentNodeId);

			if (!visitedNodes.contains(parent))
			{
				parent.fetchRoots(addTo, visitedNodes);
			}
		}
	}

	@Override
	public void fetchAllChildren(List<INode<DATA, LINK>> addTo)
	{
		if (this.tree == null)
		{
			throw new RuntimeException("Tried to fetch all children from a INode that doesn't have a NodeTree setUsedData to it.");
		}

		for (Integer childNodeId : this.children.keySet())
		{
			INode<DATA, LINK> child = this.tree.get(childNodeId);

			addTo.add(child);
			child.fetchAllChildren(addTo);
		}
	}

	@Override
	public int getNodeId()
	{
		return this.nodeId;
	}

	@Override
	public void setNodeId(int nodeId)
	{
		this.nodeId = nodeId;
	}

	@Override
	public boolean isDirectionless()
	{
		return this.isDirectionless;
	}

	@Override
	public boolean canLink()
	{
		return this.canLink;
	}

	@Override
	public INode<DATA, LINK> deepClone()
	{
		NodeMultiParented<DATA, LINK> clone = new NodeMultiParented<>();

		NBTTagCompound tag = new NBTTagCompound();

		this.write(tag);
		clone.read(tag);

		return clone;
	}

	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(this.nodeId);

		return builder.toHashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}

		if (obj instanceof NodeMultiParented)
		{
			NodeMultiParented node = (NodeMultiParented) obj;

			EqualsBuilder builder = new EqualsBuilder();

			builder.append(this.nodeId, node.nodeId);

			return builder.isEquals();
		}

		return false;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		tag.setInteger("nodeId", this.nodeId);
		funnel.set("data", this.data);
		tag.setBoolean("canLink", this.canLink);

		if (this.canLink)
		{
			funnel.setMap("children", this.children, NBTFunnel.INTEGER_SETTER, NBTFunnel.setter());
			funnel.setSet("parents", this.parents, NBTFunnel.INTEGER_SETTER);
		}

		tag.setBoolean("directionless", this.isDirectionless);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.nodeId = tag.getInteger("nodeId");
		this.data = funnel.get("data");
		this.canLink = tag.getBoolean("canLink");

		if (this.canLink)
		{
			this.children = Maps.newLinkedHashMap(funnel.getMap("children", NBTFunnel.INTEGER_GETTER, NBTFunnel.getter()));
			this.parents = funnel.getSet("parents", NBTFunnel.INTEGER_GETTER);
		}

		this.isDirectionless = tag.getBoolean("directionless");
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

		if (this.data instanceof IWorldObjectChild)
		{
			((IWorldObjectChild) this.data).setWorldObjectParent(parent);
		}
	}
}
