package com.gildedgames.orbis_api.data.blueprint;

import com.gildedgames.orbis_api.block.BlockDataContainer;
import com.gildedgames.orbis_api.block.BlockFilter;
import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.PlacedEntity;
import com.gildedgames.orbis_api.core.tree.*;
import com.gildedgames.orbis_api.data.IDataChild;
import com.gildedgames.orbis_api.data.IDataHolder;
import com.gildedgames.orbis_api.data.management.IData;
import com.gildedgames.orbis_api.data.management.IDataMetadata;
import com.gildedgames.orbis_api.data.management.impl.DataMetadata;
import com.gildedgames.orbis_api.data.pathway.Entrance;
import com.gildedgames.orbis_api.data.region.IDimensions;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.data.region.IShape;
import com.gildedgames.orbis_api.data.schedules.*;
import com.gildedgames.orbis_api.processing.DataPrimer;
import com.gildedgames.orbis_api.util.BlueprintHelper;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.gildedgames.orbis_api.world.IWorldObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
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
		implements IDimensions, IData, IPositionRecordListener<BlockFilter>, IDataHolder<BlueprintData>,
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

	private List<Entrance> entrances = Lists.newArrayList();

	private Pos2D scheduleTreeGuiPos, variableTreeGuiPos = Pos2D.ORIGIN;

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
		this.getScheduleLayerTree().add(new NodeMultiParented<>(new ScheduleLayer("Default Layer", this), false));
	}

	public BlueprintData(final BlockDataContainer container)
	{
		this();

		this.dataContainer = container;
		this.getScheduleLayerTree().add(new NodeMultiParented<>(new ScheduleLayer("Default Layer", this), false));
	}

	public static void spawnEntities(DataPrimer primer, BlueprintData data, BlockPos pos)
	{
		for (INode<IScheduleLayer, LayerLink> node : data.getScheduleLayerTree().getNodes())
		{
			IScheduleLayer layer = node.getData();

			for (ScheduleRegion s : layer.getScheduleRecord().getSchedules(ScheduleRegion.class))
			{
				for (int i = 0; i < s.getSpawnEggsInventory().getSizeInventory(); i++)
				{
					ItemStack stack = s.getSpawnEggsInventory().getStackInSlot(i);

					if (stack.getItem() instanceof ItemMonsterPlacer)
					{
						BlockPos p = pos.add(s.getBounds().getMin())
								.add(primer.getWorld().rand.nextInt(s.getBounds().getWidth()), 0, primer.getWorld().rand.nextInt(s.getBounds().getHeight()));

						PlacedEntity placedEntity = new PlacedEntity(stack, p);

						placedEntity.spawn(primer);
					}
				}
			}
		}
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

	public int getEntranceId(Entrance entrance)
	{
		int i = 0;
		for (Entrance e : this.entrances)
		{
			if (e == entrance)
			{
				return i;
			}

			i++;
		}

		return -1;
	}

	public void listen(final IBlueprintDataListener listener)
	{
		if (!this.listeners.contains(listener))
		{
			this.listeners.add(listener);
		}
	}

	public void addEntrance(Entrance entrance)
	{
		final Lock w = this.lock.writeLock();
		w.lock();

		try
		{
			BlockPos ePos = entrance.getBounds().getMin();
			boolean properEntrance = ePos.getX() == 0 || ePos.getX() == this.getWidth() - 1 ||
					ePos.getY() == 0 || ePos.getY() == this.getHeight() - 1 ||
					ePos.getZ() == 0 || ePos.getZ() == this.getLength() - 1;
			if (!properEntrance)
			{
				throw new IllegalArgumentException("Entrance can only be placed on the edges of blueprints");
			}

			entrance.setDataParent(this);
			this.entrances.add(entrance);

			this.listeners.forEach(o -> o.onAddEntrance(entrance));
		}
		finally
		{
			w.unlock();
		}
	}

	public boolean removeEntrance(int id)
	{
		final Lock w = this.lock.writeLock();
		w.lock();

		try
		{
			Entrance entrance = this.entrances.remove(id);

			if (entrance != null)
			{
				this.listeners.forEach(o -> o.onRemoveEntrance(entrance));
			}

			return entrance != null;
		}
		finally
		{
			w.unlock();
		}
	}

	public boolean removeEntrance(Entrance entrance)
	{
		final Lock w = this.lock.writeLock();
		w.lock();

		try
		{
			boolean flag = this.entrances.remove(entrance);

			if (flag)
			{
				this.listeners.forEach(o -> o.onRemoveEntrance(entrance));
			}

			return flag;
		}
		finally
		{
			w.unlock();
		}
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

	public List<Entrance> entrances()
	{
		return this.entrances;
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

		funnel.set("metadata", this.metadata);
		funnel.set("dataContainer", this.dataContainer);
		funnel.set("scheduleLayerTree", this.scheduleLayerTree);
		funnel.set("variableTree", this.variableTree);
		funnel.setIntMap("postGenReplaceLayers", this.postGenReplaceLayers);
		funnel.setList("entrances", this.entrances);
		funnel.set("scheduleTreeGuiPos", this.scheduleTreeGuiPos, NBTFunnel.POS2D_SETTER);
		funnel.set("variableTreeGuiPos", this.variableTreeGuiPos, NBTFunnel.POS2D_SETTER);
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.metadata = funnel.get("metadata");
		this.dataContainer = funnel.get("dataContainer");
		this.scheduleLayerTree = funnel.getWithDefault("scheduleLayerTree", () -> this.scheduleLayerTree);
		this.variableTree = funnel.getWithDefault("variableTree", () -> this.variableTree);
		this.postGenReplaceLayers = Maps.newLinkedHashMap(funnel.getIntMap("postGenReplaceLayers"));

		this.scheduleLayerTree.listen(this);

		this.scheduleLayerTree.getNodes().forEach(l -> l.getData().setDimensions(this));
		this.scheduleLayerTree.getNodes().forEach(l -> l.getData().getFilterRecord().listen(this));
		this.scheduleLayerTree.getNodes().forEach(l -> l.getData().setNodeParent(l));

		this.entrances = funnel.getList("entrances");

		this.scheduleTreeGuiPos = funnel.get("scheduleTreeGuiPos", NBTFunnel.POS2D_GETTER);
		this.variableTreeGuiPos = funnel.getWithDefault("variableTreeGuiPos", NBTFunnel.POS2D_GETTER, () -> this.variableTreeGuiPos);

		this.scheduleLayerTree.getNodes().forEach(
				(s) ->
				{
					s.getData().getConditionNodeTree().getNodes().stream().filter((n) -> n.getData() instanceof IDataChild)
							.forEach((n) ->
							{
								IDataChild user = (IDataChild) n.getData();

								if (user.getDataClass().equals("blueprintVariables"))
								{
									user.setDataParent(this.getVariableTree());
								}
							});

					s.getData().getPostResolveActionNodeTree().getNodes().stream().filter((n) -> n.getData() instanceof IDataChild)
							.forEach((n) ->
							{
								IDataChild user = (IDataChild) n.getData();

								if (user.getDataClass().equals("blueprintVariables"))
								{
									user.setDataParent(this.getVariableTree());
								}
							});
				});

		this.entrances.forEach(e -> e.setDataParent(this));
		this.postGenReplaceLayers.values().forEach(l -> l.setDataParent(this));
		this.scheduleLayerTree.setDataParent(this);
		this.variableTree.setDataParent(this);
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
	public IData clone()
	{
		final BlueprintData data = new BlueprintData();

		final NBTTagCompound tag = new NBTTagCompound();

		this.write(tag);

		data.read(tag);

		return data;
	}

	@Override
	public void onMarkPos(final BlockFilter filter, final int x, final int y, final int z)
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
	public void readMetadataOnly(NBTTagCompound tag)
	{
		final NBTFunnel funnel = new NBTFunnel(tag);

		this.metadata = funnel.get("metadata");
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
