package com.gildedgames.orbis.lib.data.blueprint;

import com.gildedgames.orbis.lib.block.BlockDataContainer;
import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.core.tree.*;
import com.gildedgames.orbis.lib.data.IDataHolder;
import com.gildedgames.orbis.lib.data.IDataUser;
import com.gildedgames.orbis.lib.data.management.IData;
import com.gildedgames.orbis.lib.data.management.IDataMetadata;
import com.gildedgames.orbis.lib.data.management.impl.DataMetadata;
import com.gildedgames.orbis.lib.data.pathway.IEntrance;
import com.gildedgames.orbis.lib.data.region.IDimensions;
import com.gildedgames.orbis.lib.data.region.IRegion;
import com.gildedgames.orbis.lib.data.region.IShape;
import com.gildedgames.orbis.lib.data.schedules.IPositionRecordListener;
import com.gildedgames.orbis.lib.data.schedules.IScheduleLayer;
import com.gildedgames.orbis.lib.data.schedules.PostGenReplaceLayer;
import com.gildedgames.orbis.lib.data.schedules.ScheduleLayer;
import com.gildedgames.orbis.lib.util.BlueprintHelper;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.util.mc.NBT;
import com.gildedgames.orbis.lib.world.IWorldObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BlueprintData
		implements IDimensions, IData, IPositionRecordListener<IBlockState>, IDataHolder<BlueprintData>,
		INodeTreeListener<IScheduleLayer, LayerLink>
{
	public static final String EXTENSION = "blueprint";

	private final List<IBlueprintDataListener> listeners = Lists.newArrayList();

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private IDataMetadata metadata;

	private BlockDataContainer dataContainer;

	private NodeTree<IScheduleLayer, LayerLink> scheduleLayerTree = new NodeTree<>();

	private NodeTree<BlueprintVariable, NBT> variableTree = new NodeTree<>();

	private LinkedHashMap<Integer, PostGenReplaceLayer> postGenReplaceLayers = Maps.newLinkedHashMap();

	private IEntrance entrance;

	private Pos2D scheduleTreeGuiPos, variableTreeGuiPos = Pos2D.ORIGIN;

	private IBlueprintMetadata blueprintMetadata = new BlueprintMetadata();

	private BlueprintData()
	{
		this.metadata = new DataMetadata();
		this.getScheduleLayerTree().listen(this);
		this.getVariableTree().listen(new INodeTreeListener<BlueprintVariable, NBT>()
		{
			@Override
			public void onSetData(INode<BlueprintVariable, NBT> node, BlueprintVariable variable, int id)
			{
				node.getData().setDataParent(BlueprintData.this);
			}

			@Override
			public void onPut(INode<BlueprintVariable, NBT> node, int id)
			{
				node.getData().setDataParent(BlueprintData.this);
			}

			@Override
			public void onRemove(INode<BlueprintVariable, NBT> node, int id)
			{
				node.getData().setDataParent(null);
			}
		});
	}

	public BlueprintData(final IRegion region)
	{
		this();

		this.dataContainer = new BlockDataContainer(region);
		this.getScheduleLayerTree().add(new NodeMultiParented<>(new ScheduleLayer("Root Layer", this), false));
	}

	public BlueprintData(final BlockDataContainer container)
	{
		this();

		this.dataContainer = container;
		this.getScheduleLayerTree().add(new NodeMultiParented<>(new ScheduleLayer("Root Layer", this), false));
	}

	public IBlueprintMetadata getBlueprintMetadata()
	{
		return this.blueprintMetadata;
	}

	public Pos2D getScheduleTreeGuiPos()
	{
		return this.scheduleTreeGuiPos;
	}

	public void setScheduleTreeGuiPos(Pos2D treeGuiPos)
	{
		this.scheduleTreeGuiPos = treeGuiPos;

		this.markDirty();
	}

	public Pos2D getVariableTreeGuiPos()
	{
		return this.variableTreeGuiPos;
	}

	public void setVariableTreeGuiPos(Pos2D treeGuiPos)
	{
		this.variableTreeGuiPos = treeGuiPos;

		this.markDirty();
	}

	public void listen(final IBlueprintDataListener listener)
	{
		if (!this.listeners.contains(listener))
		{
			this.listeners.add(listener);
		}
	}

	public void setEntrance(IEntrance entrance)
	{
		final Lock w = this.lock.writeLock();
		w.lock();

		try
		{
			this.clearEntrance();

			entrance.setDataParent(this);
			this.entrance = entrance;

			this.listeners.forEach(o -> o.onAddEntrance(entrance));
		}
		finally
		{
			w.unlock();
		}
	}

	public boolean clearEntrance()
	{
		final Lock w = this.lock.writeLock();
		w.lock();

		try
		{
			boolean avail = this.entrance != null;

			if (avail)
			{
				this.listeners.forEach(o -> o.onRemoveEntrance(this.entrance));
				this.entrance = null;
			}

			return avail;
		}
		finally
		{
			w.unlock();
		}
	}

	public IEntrance getEntrance()
	{
		return this.entrance;
	}

	public BlockDataContainer getBlockDataContainer()
	{
		return this.dataContainer;
	}

	public NodeTree<IScheduleLayer, LayerLink> getScheduleLayerTree()
	{
		return this.scheduleLayerTree;
	}

	public NodeTree<BlueprintVariable, NBT> getVariableTree()
	{
		return this.variableTree;
	}

	@Override
	public int getWidth()
	{
		return this.dataContainer.getWidth();
	}

	@Override
	public int getHeight()
	{
		return this.dataContainer.getHeight();
	}

	@Override
	public int getLength()
	{
		return this.dataContainer.getLength();
	}

	@Override
	public int hashCode()
	{
		final HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(this.metadata.getIdentifier());

		return builder.toHashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj instanceof BlueprintData)
		{
			final BlueprintData o = (BlueprintData) obj;

			final EqualsBuilder builder = new EqualsBuilder();

			builder.append(this.metadata.getIdentifier(), o.metadata.getIdentifier());

			return builder.isEquals();
		}

		return false;
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("dataContainer", this.dataContainer);
		funnel.set("scheduleLayerTree", this.scheduleLayerTree);
		funnel.set("variableTree", this.variableTree);
		funnel.setIntMap("postGenReplaceLayers", this.postGenReplaceLayers);
		funnel.set("entrance", this.entrance);
		funnel.set("scheduleTreeGuiPos", this.scheduleTreeGuiPos, NBTFunnel.POS2D_SETTER);
		funnel.set("variableTreeGuiPos", this.variableTreeGuiPos, NBTFunnel.POS2D_SETTER);
		funnel.set("blueprintMetadata", this.blueprintMetadata);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.dataContainer = funnel.get("dataContainer");
		this.scheduleLayerTree = funnel.getWithDefault("scheduleLayerTree", () -> this.scheduleLayerTree);
		this.variableTree = funnel.getWithDefault("variableTree", () -> this.variableTree);
		this.postGenReplaceLayers = Maps.newLinkedHashMap(funnel.getIntMap("postGenReplaceLayers"));

		this.scheduleLayerTree.listen(this);

		this.scheduleLayerTree.getNodes().forEach(l -> l.getData().setDimensions(this));
		this.scheduleLayerTree.getNodes().forEach(l -> l.getData().getStateRecord().listen(this));
		this.scheduleLayerTree.getNodes().forEach(l -> l.getData().setNodeParent(l));

		this.entrance = funnel.get("entrance");

		this.scheduleTreeGuiPos = funnel.get("scheduleTreeGuiPos", NBTFunnel.POS2D_GETTER);
		this.variableTreeGuiPos = funnel.getWithDefault("variableTreeGuiPos", NBTFunnel.POS2D_GETTER, () -> this.variableTreeGuiPos);

		this.scheduleLayerTree.getNodes().forEach(
				(s) ->
				{
					s.getData().getConditionNodeTree().getNodes().stream().filter((n) -> n.getData() instanceof IDataUser)
							.forEach((n) ->
							{
								IDataUser user = (IDataUser) n.getData();

								if (user.getDataIdentifier().equals("blueprintVariables"))
								{
									user.setUsedData(this.getVariableTree());
								}
							});

					s.getData().getPostResolveActionNodeTree().getNodes().stream().filter((n) -> n.getData() instanceof IDataUser)
							.forEach((n) ->
							{
								IDataUser user = (IDataUser) n.getData();

								if (user.getDataIdentifier().equals("blueprintVariables"))
								{
									user.setUsedData(this.getVariableTree());
								}
							});
				});

		if (this.entrance != null)
		{
			this.entrance.setDataParent(this);
		}
		this.postGenReplaceLayers.values().forEach(l -> l.setDataParent(this));
		this.scheduleLayerTree.setDataParent(this);
		this.variableTree.setDataParent(this);

		this.blueprintMetadata = funnel.getWithDefault("blueprintMetadata", () -> this.blueprintMetadata);
	}

	@Override
	public void preSaveToDisk(final IWorldObject object)
	{
		if (object instanceof IShape)
		{
			final IShape shape = (IShape) object;

			this.dataContainer = BlueprintHelper.fetchBlocksInside(shape, object.getWorld());
		}
	}

	@Override
	public String getFileExtension()
	{
		return BlueprintData.EXTENSION;
	}

	@Override
	public IDataMetadata getMetadata()
	{
		return this.metadata;
	}

	@Override
	public void setMetadata(IDataMetadata metadata)
	{
		this.metadata = metadata;
	}

	@Override
	public IData clone()
	{
		final BlueprintData data = new BlueprintData();

		final NBTTagCompound tag = new NBTTagCompound();

		this.write(tag);

		data.read(tag);

		return data;
	}

	@Override
	public void onMarkPos(final IBlockState state, final int x, final int y, final int z)
	{
		this.listeners.forEach(IBlueprintDataListener::onDataChanged);
	}

	@Override
	public void onUnmarkPos(final int x, final int y, final int z)
	{
		this.listeners.forEach(IBlueprintDataListener::onDataChanged);
	}

	public void markDirty()
	{
		this.listeners.forEach(IBlueprintDataListener::onDataChanged);
	}

	@Override
	public BlueprintData get(World world, Random random)
	{
		return this;
	}

	@Override
	public int getLargestHeight()
	{
		return this.getHeight();
	}

	@Override
	public int getLargestWidth()
	{
		return this.getWidth();
	}

	@Override
	public int getLargestLength()
	{
		return this.getLength();
	}

	public LinkedHashMap<Integer, PostGenReplaceLayer> getPostGenReplaceLayers()
	{
		return this.postGenReplaceLayers;
	}

	public void setPostGenReplaceLayer(final int index, final PostGenReplaceLayer layer)
	{
		layer.setLayerId(index);

		layer.setDataParent(this);
		this.postGenReplaceLayers.put(index, layer);
	}

	public int findNextAvailablePostGenId()
	{
		int i = 0;

		while (this.postGenReplaceLayers.containsKey(i))
		{
			i++;
		}

		return i;
	}

	public int addPostGenReplaceLayer(final PostGenReplaceLayer layer)
	{
		int id = this.findNextAvailablePostGenId();

		this.setPostGenReplaceLayer(id, layer);

		return id;
	}

	public boolean removePostGenReplaceLayer(final int index)
	{
		final boolean removed = this.postGenReplaceLayers.get(index) != null;

		final PostGenReplaceLayer layer = this.postGenReplaceLayers.remove(index);

		return removed;
	}

	public PostGenReplaceLayer getPostGenReplaceLayer(int id)
	{
		return this.postGenReplaceLayers.get(id);
	}

	public int getPostGenReplaceLayerId(final PostGenReplaceLayer layer)
	{
		for (Map.Entry<Integer, PostGenReplaceLayer> entry : this.postGenReplaceLayers.entrySet())
		{
			int i = entry.getKey();
			final PostGenReplaceLayer s = entry.getValue();

			if (layer.equals(s))
			{
				return i;
			}
		}

		return -1;
	}

	@Override
	public void onSetData(INode<IScheduleLayer, LayerLink> node, IScheduleLayer iScheduleLayer, int id)
	{
		IScheduleLayer layer = node.getData();

		layer.setDataParent(this);
		layer.setNodeParent(node);
	}

	@Override
	public void onPut(INode<IScheduleLayer, LayerLink> node, int id)
	{
		IScheduleLayer layer = node.getData();

		layer.setDataParent(this);
		layer.setNodeParent(node);
	}

	@Override
	public void onRemove(INode<IScheduleLayer, LayerLink> node, int id)
	{
		node.getData().setNodeParent(null);
	}
}
