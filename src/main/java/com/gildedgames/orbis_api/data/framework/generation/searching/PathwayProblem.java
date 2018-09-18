package com.gildedgames.orbis_api.data.framework.generation.searching;

import com.gildedgames.orbis_api.core.tree.ConditionLink;
import com.gildedgames.orbis_api.core.tree.INode;
import com.gildedgames.orbis_api.core.variables.conditions.IGuiConditionEntrance;
import com.gildedgames.orbis_api.core.world_objects.BlueprintRegion;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.framework.interfaces.EnumFacingMultiple;
import com.gildedgames.orbis_api.data.pathway.IEntrance;
import com.gildedgames.orbis_api.data.region.IMutableRegion;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.data.region.Region;
import com.gildedgames.orbis_api.util.RegionHelp;
import com.gildedgames.orbis_api.util.RotationHelp;
import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PathwayProblem implements ISearchProblem<PathwayNode>
{

	private static final float pathwaysBoundingBox = 8;

	protected final BlockPos start, end;

	protected final List<BlueprintData> pieces;

	private final Collection<BlueprintRegion> fragments;

	private final IRegion boundingBox;

	private final BlueprintRegion startFragment;

	private double maxLength;

	private boolean hasVerticalEntrances, checkedForVertical;

	//private Random rand;

	private World world;

	public PathwayProblem(World world, BlockPos start, BlueprintRegion startFragment, BlockPos end, List<BlueprintData> pieces,
			Collection<BlueprintRegion> fragments)
	{
		this.world = world;
		this.start = start;
		this.end = end;
		this.pieces = pieces;

		for (BlueprintData b : pieces)
		{
			this.maxLength = Math.max(this.maxLength, b.getWidth() * b.getWidth() + b.getHeight() + b.getLength() * b.getLength());
		}

		this.maxLength = Math.sqrt(this.maxLength);

		this.fragments = fragments;

		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

		for (BlueprintRegion fragment : fragments)
		{
			minX = Math.min(minX, fragment.getMin().getX());
			minY = Math.min(minY, fragment.getMin().getY());
			minZ = Math.min(minZ, fragment.getMin().getZ());

			maxX = Math.max(maxX, fragment.getMax().getX());
			maxY = Math.max(maxY, fragment.getMax().getY());
			maxZ = Math.max(maxZ, fragment.getMax().getZ());
		}

		if (fragments.size() <= 0)
		{
			minX = Math.min(minX, start.getX());
			minY = Math.min(minY, start.getY());
			minZ = Math.min(minZ, start.getZ());

			maxX = Math.max(maxX, end.getX());
			maxY = Math.max(maxY, end.getY());
			maxZ = Math.max(maxZ, end.getZ());
		}

		this.startFragment = new BlueprintRegion(startFragment.getMin(), startFragment.getRotation(), startFragment.getData());

		this.boundingBox = RegionHelp
				.expand(new Region(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)), (int) (this.maxLength * pathwaysBoundingBox));

		//this.rand = new Random();
		//this.rand.setSeed((long) viableStarts.getX() * 341873128712L + (long) viableStarts.getY() * 23289687541L + (long) viableStarts.getZ() * 132897987541L);
	}

	public IRegion getBoundingBox()
	{
		return this.boundingBox;
	}

	@Override
	public List<PathwayNode> successors(PathwayNode parentState)
	{
		List<PathwayNode> successors = new ArrayList<>();

		IRegion end = parentState.endConnection;

		//TODO: FIND ENTRANCE/SIDE CLOSEST TO END POSITION
		EnumFacingMultiple lastSide = parentState.sideOfConnection();

		EnumFacingMultiple toConnect = lastSide.getOpposite();

		IRegion adjacentEntrance = PathwayUtil.adjacent(end, lastSide);

		IEntrance lastEntrance = parentState.getNonRotatedEntrance();

		for (BlueprintData blueprint : this.pieces)
		{
			Region rect = new Region(new BlockPos(0, 0, 0),
					new BlockPos(blueprint.getWidth() - 1, blueprint.getHeight() - 1, blueprint.getLength() - 1));

			entrances:
			for (IEntrance entrance : blueprint.entrances())
			{
				for (INode<IGuiConditionEntrance, ConditionLink> node : lastEntrance.getConditionNodeTree().getNodes())
				{
					if (!node.getData().canConnectTo(entrance))
					{
						break entrances;
					}
				}

				EnumFacingMultiple sideOn = entrance.getFacing();

				Rotation rotation = Rotation.NONE;

				if (toConnect.canRotateToFaceEachother(sideOn))
				{
					rotation = RotationHelp.getRotationDifference(RotationHelp.fromFacing(toConnect), RotationHelp.fromFacing(sideOn));
				}
				else if (sideOn != toConnect)
				{
					continue;
				}

				IRegion trEntrance = RotationHelp.rotate(entrance.getBounds(), rect, rotation);

				if (!RegionHelp.sameDim(trEntrance, parentState.endConnection) && parentState.parent != null)
				{
					continue;
				}

				int dx = adjacentEntrance.getMin().getX() - trEntrance.getMin().getX();
				int dy = adjacentEntrance.getMin().getY() - trEntrance.getMin().getY();
				int dz = adjacentEntrance.getMin().getZ() - trEntrance.getMin().getZ();

				for (IEntrance exit : blueprint.entrances())
				{
					if (entrance == exit)
					{
						continue;
					}

					IRegion trExit = RotationHelp.rotate(exit.getBounds(), rect, rotation);

					IMutableRegion endConnection = new Region(trExit);

					RegionHelp.translate(endConnection, dx, dy, dz);

					int fx = dx;
					int fy = dy;
					int fz = dz;

					BlockPos fragmentMin = new BlockPos(fx, fy, fz);
					BlueprintRegion fragment = new BlueprintRegion(fragmentMin, rotation, blueprint);

					PathwayNode node = new PathwayNode(parentState, fragment, endConnection, PathwayUtil.getRotated(exit.getFacing(), rotation), exit);

					if (this.isSuccessor(node, parentState))
					{
						successors.add(node);
					}
				}
			}
		}

		return successors;
	}

	@Override
	public List<PathwayNode> viableStarts()
	{
		List<PathwayNode> viable = Lists.newArrayList();

		BlueprintData d = this.startFragment.getData();

		for (int i = 0; i < d.entrances().size(); i++)
		{
			IEntrance e = d.entrances().get(i);

			Region r = new Region(e.getBounds());

			r.add(this.start.getX(), this.start.getY(), this.start.getZ());

			viable.add(new PathwayNode(null, this.startFragment, r, e.getFacing(), e));
		}

		return viable;
	}

	protected boolean isSuccessor(PathwayNode node, PathwayNode parent)
	{
		if (this.isGoal(node))
		{
			return true;
		}

		for (BlockPos.MutableBlockPos pos : node.getShapeData())
		{
			if (!this.world.isAirBlock(pos))
			{
				return false;
			}
		}

		// Can improve performance (I think?) by chunking up fragment references into chunk coords, then finding fragments in the chunk of the node we're checking against
		/*for (BlueprintRegion fragment : this.fragments)
		{
			if (RegionHelp.intersects(fragment, node))
			{
				return false;
			}
		}

		for (PathwayNode s : parent.fullPath())
		{
			if (RegionHelp.intersects(node, s))
			{
				return false;
			}
		}*/

		return true;
	}

	private boolean hasVerticalEntrances()
	{
		if (!this.checkedForVertical)
		{
			this.hasVerticalEntrances = false;

			boolean hasDown = false, hasUp = false;

			for (BlueprintData d : this.pieces)
			{
				for (IEntrance e : d.entrances())
				{
					for (EnumFacing side : e.getFacing().getFacings())
					{
						if (side == EnumFacing.DOWN)
						{
							hasDown = true;
						}

						if (side == EnumFacing.UP)
						{
							hasUp = true;
						}

						if (hasDown && hasUp)
						{
							this.hasVerticalEntrances = true;
							break;
						}
					}
				}
			}

			this.checkedForVertical = true;
		}

		return this.hasVerticalEntrances;
	}

	@Override
	public double heuristic(PathwayNode state)
	{
		if (this.isGoal(state))
		{
			//return 0;
		}

		IRegion exit = state.endConnection;

		double h = Math.abs(exit.getMin().getX() - this.end.getX()) + Math.abs(exit.getMin().getY() - this.end.getY()) + Math
				.abs(exit.getMin().getZ() - this.end.getZ());

		double ax = exit.getMin().getX() - this.end.getX();
		double az = exit.getMin().getZ() - this.end.getZ();
		double bx = this.start.getX() - this.end.getX();
		double bz = this.start.getZ() - this.end.getZ();
		double cross = Math.abs(ax * bz - bx * az);

		if (this.hasVerticalEntrances())
		{
			double ay = exit.getMin().getY() - this.end.getY();
			double by = this.start.getY() - this.end.getY();

			//cross = Math.abs((ay * bz - az * by) + (ax * bz - az * bx) + (ax * bx - ay * bx));
		}

		h += cross * 0.001;

		h += state.getData().entrances().size() * 2.5;

		/*if (state.parent != null && state.parent.getData() == state.getData())
		{
			//h -= 2.5;
		}

		for (IEntrance e : state.getData().entrances())
		{
			if (e.getFacing().hasPlane(EnumFacingMultiple.Plane.HORIZONTAL))
			{
				h += 250;
			}
		}*/

		return h;
	}

	@Override
	public double costBetween(PathwayNode parent, PathwayNode child)
	{
		IRegion exit = child.endConnection;

		/*if (this.isGoal(child))
		{
			double c = Math.abs(this.end.getX() - exit.getMin().getX()) + Math
					.abs(this.end.getZ() - exit.getMin().getZ());

			if (this.hasVerticalEntrances())
			{
				c += Math.abs(this.end.getY() - exit.getMin().getY());
			}

			return c;
		}*/

		IRegion entrance = parent.endConnection;

		double dx = entrance.getMin().getX() - exit.getMin().getX();
		double dz = entrance.getMin().getZ() - exit.getMin().getZ();

		double g = Math.abs(dx) + Math.abs(dz);

		if (this.hasVerticalEntrances())
		{
			double dy = entrance.getMin().getY() - exit.getMin().getY();

			g += Math.abs(dy);
		}

		return g;
	}

	@Override
	public boolean shouldTerminate(PathwayNode currentState)
	{
		return !RegionHelp.intersects(currentState, this.boundingBox);
	}

	@Override
	public boolean contains(Collection<PathwayNode> visitedStates, PathwayNode currentState)
	{
		for (PathwayNode visitedState : visitedStates)
		{
			if (visitedState.parent != currentState.parent)
			{
				if (visitedState.endConnection.equals(currentState.endConnection))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isGoal(PathwayNode state)
	{
		if (!this.hasVerticalEntrances())
		{
			return RegionHelp.containsIgnoreY(state, this.end);
		}

		return RegionHelp.contains(state, this.end);
	}

}
