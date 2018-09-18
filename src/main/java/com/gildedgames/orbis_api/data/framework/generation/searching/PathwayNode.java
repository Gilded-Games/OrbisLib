package com.gildedgames.orbis_api.data.framework.generation.searching;

import com.gildedgames.orbis_api.core.world_objects.BlueprintRegion;
import com.gildedgames.orbis_api.data.framework.interfaces.EnumFacingMultiple;
import com.gildedgames.orbis_api.data.pathway.IEntrance;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

public class PathwayNode extends BlueprintRegion implements AStarNode
{

	public final PathwayNode parent;

	public final IRegion endConnection;

	private double h, g;

	private EnumFacingMultiple sideOfConnection;

	private IEntrance nonRotatedEntrance;

	public PathwayNode(PathwayNode parent, BlueprintRegion rect, IRegion endConnection, EnumFacingMultiple sideOfConnection, IEntrance nonRotatedEntrance)
	{
		super(rect.getMin(), rect.getRotation(), rect.getData());

		this.parent = parent;

		this.endConnection = endConnection;
		this.sideOfConnection = sideOfConnection;
		this.nonRotatedEntrance = nonRotatedEntrance;
	}

	public Iterable<PathwayNode> fullPath()
	{
		return new Iterable<PathwayNode>()
		{
			@Override
			public Iterator<PathwayNode> iterator()
			{
				return new AbstractIterator<PathwayNode>()
				{
					PathwayNode node;

					@Override
					protected PathwayNode computeNext()
					{
						if (this.node == null)
						{
							this.node = PathwayNode.this;
							return this.node;
						}
						if (this.node.parent != null)
						{
							this.node = this.node.parent;
							return this.node;
						}
						return this.endOfData();
					}
				};
			}
		};
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof PathwayNode))
		{
			return false;
		}

		IRegion p = ((PathwayNode) obj).endConnection;
		return p.equals(this.endConnection);
	}

	@Override
	public int hashCode()
	{
		return this.endConnection.hashCode();
	}

	public IEntrance getNonRotatedEntrance()
	{
		return this.nonRotatedEntrance;
	}

	public EnumFacingMultiple sideOfConnection()
	{
		return this.sideOfConnection;
	}

	@Override
	public double getG()
	{
		return this.g;
	}

	@Override
	public void setG(double g)
	{
		this.g = g;
	}

	@Override
	public double getH()
	{
		return this.h;
	}

	@Override
	public void setH(double h)
	{
		this.h = h;
	}

	@Override
	public double getF()
	{
		return this.getG() + this.getH();
	}

	@Override
	public int compareTo(AStarNode o)
	{
		if (o.getF() == this.getF())
		{
			return Double.compare(this.getH(), o.getH());
		}

		return Double.compare(this.getF(), o.getF());
	}
}
