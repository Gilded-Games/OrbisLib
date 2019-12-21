package com.gildedgames.orbis.lib.core.baking;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.core.tree.ConditionLink;
import com.gildedgames.orbis.lib.core.tree.INode;
import com.gildedgames.orbis.lib.core.tree.LayerLink;
import com.gildedgames.orbis.lib.core.tree.NodeTree;
import com.gildedgames.orbis.lib.core.variables.conditions.IGuiCondition;
import com.gildedgames.orbis.lib.core.variables.post_resolve_actions.IPostResolveAction;
import com.gildedgames.orbis.lib.data.IDataUser;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintVariable;
import com.gildedgames.orbis.lib.data.management.IDataIdentifier;
import com.gildedgames.orbis.lib.data.region.Region;
import com.gildedgames.orbis.lib.data.schedules.IScheduleLayer;
import com.gildedgames.orbis.lib.data.schedules.ScheduleEntranceHolder;
import com.gildedgames.orbis.lib.util.RotationHelp;
import com.gildedgames.orbis.lib.util.mc.NBT;
import com.gildedgames.orbis.lib.util.mc.NBTHelper;
import com.google.common.collect.Lists;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import static com.gildedgames.orbis.lib.util.RotationHelp.transformedBlockPos;

public class BakedScheduleLayers
{
	private List<PotentialEntrance> potentialEntrances;

	private LinkedList<INode<IScheduleLayer, LayerLink>> bakedScheduleLayerNodes = Lists.newLinkedList();

	private NodeTree<BlueprintVariable, NBT> bakedBlueprintVariables;

	private Random rand;

	private BlueprintData blueprintData;

	public BakedScheduleLayers(BlueprintData data, Random rand)
	{
		this.blueprintData = data;
		this.rand = rand;

		this.bake();
	}

	public void bake()
	{
		this.bakedBlueprintVariables = this.blueprintData.getVariableTree().deepClone();
		this.bakedScheduleLayerNodes.clear();

		this.fetchValidLayers(this.blueprintData.getScheduleLayerTree().getRootNode(), this.bakedScheduleLayerNodes, Lists.newArrayList());
	}

	protected boolean resolveChildrenConditions(INode<IGuiCondition, ConditionLink> parent)
	{
		IGuiCondition condition = parent.getData();

		if (condition instanceof IDataUser)
		{
			IDataUser user = (IDataUser) condition;

			if (user.getDataIdentifier().equals("blueprintVariables"))
			{
				user.setUsedData(this.bakedBlueprintVariables);
			}
		}

		boolean resolved = condition.resolve(this.rand);

		boolean result = false;

		if (resolved)
		{
			result = true;
		}

		for (INode<IGuiCondition, ConditionLink> child : parent.getTree().get(parent.getChildrenIds()))
		{
			//TODO: Implement actual condition link logic - AND and OR logic. Currently just goes through all checks if all are resolved.
			if (this.resolveChildrenConditions(child))
			{
				result = true;
			}
		}

		return result;
	}

	private void fetchValidLayers(INode<IScheduleLayer, LayerLink> root, List<INode<IScheduleLayer, LayerLink>> addValidTo,
			List<INode<IScheduleLayer, LayerLink>> visited)
	{
		if (root == null)
		{
			return;
		}

		if (visited.contains(root))
		{
			return;
		}

		visited.add(root);

		for (INode<IScheduleLayer, LayerLink> parent : root.getTree().get(root.getParentsIds()))
		{
			if (!visited.contains(parent))
			{
				this.fetchValidLayers(parent, addValidTo, visited);
			}

			if (!addValidTo.contains(parent))
			{
				return;
			}
		}

		IScheduleLayer layer = root.getData();

		if (layer.getConditionNodeTree().isEmpty())
		{
			addValidTo.add(root);

			for (INode<IPostResolveAction, NBT> action : layer.getPostResolveActionNodeTree().getNodes())
			{
				if (action.getData() instanceof IDataUser)
				{
					IDataUser user = (IDataUser) action.getData();

					if (user.getDataIdentifier().equals("blueprintVariables"))
					{
						user.setUsedData(this.bakedBlueprintVariables);
					}
				}

				action.getData().resolve(this.rand);
			}
		}
		else if (layer.getConditionNodeTree().getRootNode() != null)
		{
			if (!this.resolveChildrenConditions(layer.getConditionNodeTree().getRootNode()))
			{
				return;
			}

			addValidTo.add(root);

			for (INode<IPostResolveAction, NBT> action : layer.getPostResolveActionNodeTree().getNodes())
			{
				if (action.getData() instanceof IDataUser)
				{
					IDataUser user = (IDataUser) action.getData();

					if (user.getDataIdentifier().equals("blueprintVariables"))
					{
						user.setUsedData(this.bakedBlueprintVariables);
					}
				}

				action.getData().resolve(this.rand);
			}
		}

		List<INode<IScheduleLayer, LayerLink>> children = Lists.newArrayList(root.getTree().get(root.getChildrenIds()));

		Collections.shuffle(children, this.rand);

		for (INode<IScheduleLayer, LayerLink> child : children)
		{
			this.fetchValidLayers(child, addValidTo, visited);
		}
	}

	public void bakePotentialEntrances(Rotation rotation)
	{
		this.potentialEntrances = Lists.newArrayList();

		BlockPos dimensions = new BlockPos(this.blueprintData.getWidth() - 1, this.blueprintData.getHeight() - 1, this.blueprintData.getLength() - 1);
		Region bakedRegion = new Region(BlockPos.ORIGIN, transformedBlockPos(dimensions, rotation));

		for (INode<IScheduleLayer, LayerLink> node : this.bakedScheduleLayerNodes)
		{
			IScheduleLayer layer = node.getData();

			for (ScheduleEntranceHolder e : layer.getScheduleRecord().getSchedules(ScheduleEntranceHolder.class))
			{
				ScheduleEntranceHolder entranceHolder = NBTHelper.clone(e);
				IDataIdentifier id = entranceHolder.getEntranceHolder();

				Optional<BlueprintData> data = OrbisLib.services().getProjectManager().findData(id);

				if (data.isPresent())
				{
					RotationHelp.rotateNew(entranceHolder.getBounds(), rotation);
					BlockPos min = entranceHolder.getBounds().getMin();
					BlockPos max = entranceHolder.getBounds().getMax();

					entranceHolder.getBounds().setBounds(min.subtract(bakedRegion.getMin()), max.subtract(bakedRegion.getMin()));

					Rotation newRotation = entranceHolder.getRotation().add(rotation);
					entranceHolder.setRotation(newRotation);

					this.potentialEntrances.add(new PotentialEntrance(data.get(), entranceHolder));
				}
				else
				{
					OrbisLib.LOGGER.error("Entrance not found in blueprint. Entrance id: {}, Blueprint: {}!", id, this.blueprintData);
				}
			}
		}
	}

	public List<PotentialEntrance> getPotentialEntrances()
	{
		if (this.potentialEntrances == null)
		{
			this.bakePotentialEntrances(Rotation.NONE);
		}

		return this.potentialEntrances;
	}

	public LinkedList<INode<IScheduleLayer, LayerLink>> getScheduleLayerNodes()
	{
		return this.bakedScheduleLayerNodes;
	}
}
