package com.gildedgames.orbis_api.data.framework.interfaces;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nullable;
import java.util.*;

public enum EnumFacingMultiple implements IStringSerializable
{
	UP(0, 1, "up", new EnumFacing[] { EnumFacing.UP }, new Plane[] { Plane.VERTICAL_POSITIVE }),
	DOWN(1, 0, "down", new EnumFacing[] { EnumFacing.DOWN }, new Plane[] { Plane.VERTICAL_NEGATIVE }),
	NORTH(2, 4, "north", new EnumFacing[] { EnumFacing.NORTH }, new Plane[] { Plane.HORIZONTAL }),
	EAST(3, 5, "east", new EnumFacing[] { EnumFacing.EAST }, new Plane[] { Plane.HORIZONTAL }),
	SOUTH(4, 2, "south", new EnumFacing[] { EnumFacing.SOUTH }, new Plane[] { Plane.HORIZONTAL }),
	WEST(5, 3, "west", new EnumFacing[] { EnumFacing.WEST }, new Plane[] { Plane.HORIZONTAL }),
	NORTH_EAST(6, 9, "north_east", new EnumFacing[] { EnumFacing.NORTH, EnumFacing.EAST }, new Plane[] { Plane.DIAGONAL }),
	NORTH_WEST(7, 8, "north_west", new EnumFacing[] { EnumFacing.NORTH, EnumFacing.WEST }, new Plane[] { Plane.DIAGONAL }),
	SOUTH_EAST(8, 7, "south_east", new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.EAST }, new Plane[] { Plane.DIAGONAL }),
	SOUTH_WEST(9, 6, "south_west", new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.WEST }, new Plane[] { Plane.DIAGONAL }),
	UP_NORTH(10, 20, "up_north", new EnumFacing[] { EnumFacing.UP, EnumFacing.NORTH }, new Plane[] { Plane.VERTICAL_POSITIVE, Plane.HORIZONTAL }),
	UP_EAST(11, 21, "up_east", new EnumFacing[] { EnumFacing.UP, EnumFacing.EAST }, new Plane[] { Plane.VERTICAL_POSITIVE, Plane.HORIZONTAL }),
	UP_SOUTH(12, 18, "up_south", new EnumFacing[] { EnumFacing.UP, EnumFacing.SOUTH }, new Plane[] { Plane.VERTICAL_POSITIVE, Plane.HORIZONTAL }),
	UP_WEST(13, 19, "up_west", new EnumFacing[] { EnumFacing.UP, EnumFacing.WEST }, new Plane[] { Plane.VERTICAL_POSITIVE, Plane.HORIZONTAL }),
	UP_NORTH_EAST(14, 25, "up_north_east", new EnumFacing[] { EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST },
			new Plane[] { Plane.VERTICAL_POSITIVE, Plane.DIAGONAL }),
	UP_NORTH_WEST(15, 24, "up_north_west", new EnumFacing[] { EnumFacing.UP, EnumFacing.NORTH, EnumFacing.WEST },
			new Plane[] { Plane.VERTICAL_POSITIVE, Plane.DIAGONAL }),
	UP_SOUTH_EAST(16, 23, "up_south_east", new EnumFacing[] { EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.EAST },
			new Plane[] { Plane.VERTICAL_POSITIVE, Plane.DIAGONAL }),
	UP_SOUTH_WEST(17, 22, "up_south_west", new EnumFacing[] { EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.WEST },
			new Plane[] { Plane.VERTICAL_POSITIVE, Plane.DIAGONAL }),
	DOWN_NORTH(18, 12, "down_north", new EnumFacing[] { EnumFacing.DOWN, EnumFacing.NORTH }, new Plane[] { Plane.VERTICAL_NEGATIVE, Plane.HORIZONTAL }),
	DOWN_EAST(19, 13, "down_east", new EnumFacing[] { EnumFacing.DOWN, EnumFacing.EAST }, new Plane[] { Plane.VERTICAL_NEGATIVE, Plane.HORIZONTAL }),
	DOWN_SOUTH(20, 10, "down_south", new EnumFacing[] { EnumFacing.DOWN, EnumFacing.SOUTH }, new Plane[] { Plane.VERTICAL_NEGATIVE, Plane.HORIZONTAL }),
	DOWN_WEST(21, 11, "down_west", new EnumFacing[] { EnumFacing.DOWN, EnumFacing.WEST }, new Plane[] { Plane.VERTICAL_NEGATIVE, Plane.HORIZONTAL }),
	DOWN_NORTH_EAST(22, 17, "down_north_east", new EnumFacing[] { EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST },
			new Plane[] { Plane.VERTICAL_NEGATIVE, Plane.DIAGONAL }),
	DOWN_NORTH_WEST(23, 16, "down_north_west", new EnumFacing[] { EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.WEST },
			new Plane[] { Plane.VERTICAL_NEGATIVE, Plane.DIAGONAL }),
	DOWN_SOUTH_EAST(24, 15, "down_south_east", new EnumFacing[] { EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.EAST },
			new Plane[] { Plane.VERTICAL_NEGATIVE, Plane.DIAGONAL }),
	DOWN_SOUTH_WEST(25, 14, "down_south_west", new EnumFacing[] { EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.WEST },
			new Plane[] { Plane.VERTICAL_NEGATIVE, Plane.DIAGONAL });

	public static final EnumFacingMultiple[] VALUES = new EnumFacingMultiple[26];

	private static final Map<String, EnumFacingMultiple> NAME_LOOKUP = Maps.newHashMap();

	static
	{
		for (EnumFacingMultiple enumfacing : values())
		{
			VALUES[enumfacing.index] = enumfacing;

			NAME_LOOKUP.put(enumfacing.getName().toLowerCase(Locale.ROOT), enumfacing);
		}
	}

	private final int index;

	/** Index of the opposite Facing in the VALUES array */
	private final int opposite;

	private final List<EnumFacing> facings = Lists.newArrayList();

	private final Plane[] planes;

	private final String name;

	private final Vec3i directionVec;

	EnumFacingMultiple(int index, int opposite, String name, EnumFacing[] facings, Plane[] planes)
	{
		this.index = index;
		this.opposite = opposite;
		this.name = name;
		this.facings.addAll(Arrays.asList(facings));
		this.planes = planes;

		int x = 0;
		int y = 0;
		int z = 0;

		for (EnumFacing face : facings)
		{
			x += face.getDirectionVec().getX();
			y += face.getDirectionVec().getY();
			z += face.getDirectionVec().getZ();
		}

		this.directionVec = new Vec3i(x, y, z);
	}

	@Nullable
	public static EnumFacingMultiple byName(String name)
	{
		return name == null ? null : NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
	}

	public static EnumFacingMultiple getFront(int index)
	{
		return VALUES[MathHelper.abs(index % VALUES.length)];
	}

	public static EnumFacingMultiple getFromMultiple(EnumFacing... facings)
	{
		Collection<EnumFacing> facingsCol = Arrays.asList(facings);

		for (EnumFacingMultiple m : VALUES)
		{
			if (m.facings.containsAll(facingsCol))
			{
				return m;
			}
		}

		throw new IllegalStateException("Facings parameters does not equal any combinations: " + Arrays.toString(facings));
	}

	public Vec3i getDirectionVec()
	{
		return this.directionVec;
	}

	public EnumFacingMultiple getOpposite()
	{
		return getFront(this.opposite);
	}

	public List<EnumFacing> getFacings()
	{
		return Collections.unmodifiableList(this.facings);
	}

	public Plane[] getPlanes()
	{
		return this.planes;
	}

	public boolean canRotateToFaceEachother(EnumFacingMultiple facing)
	{
		Plane[] highest = facing.getPlanes().length > this.getPlanes().length ? facing.getPlanes() : this.getPlanes();
		Plane[] lowest = highest == facing.getPlanes() ? this.getPlanes() : facing.getPlanes();

		for (Plane p1 : highest)
		{
			boolean found = false;

			for (Plane p2 : lowest)
			{
				if (p2 == p1)
				{
					found = true;
					break;
				}
			}

			if (!found)
			{
				return false;
			}
		}

		return true;
	}

	public boolean hasPlane(Plane plane)
	{
		for (Plane p : this.planes)
		{
			if (p == plane)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Rotate this Facing around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
	 */
	public EnumFacingMultiple rotateY()
	{
		switch (this)
		{
			case UP:
				return UP;
			case DOWN:
				return DOWN;
			case NORTH_EAST:
				return SOUTH_EAST;
			case SOUTH_EAST:
				return SOUTH_WEST;
			case SOUTH_WEST:
				return NORTH_WEST;
			case NORTH_WEST:
				return NORTH_EAST;
			case UP_NORTH:
				return UP_EAST;
			case UP_EAST:
				return UP_SOUTH;
			case UP_SOUTH:
				return UP_WEST;
			case UP_WEST:
				return UP_NORTH;
			case DOWN_NORTH:
				return DOWN_EAST;
			case DOWN_EAST:
				return DOWN_SOUTH;
			case DOWN_SOUTH:
				return DOWN_WEST;
			case DOWN_WEST:
				return DOWN_NORTH;
			case DOWN_NORTH_EAST:
				return DOWN_SOUTH_EAST;
			case DOWN_SOUTH_EAST:
				return DOWN_SOUTH_WEST;
			case DOWN_SOUTH_WEST:
				return DOWN_NORTH_WEST;
			case DOWN_NORTH_WEST:
				return DOWN_NORTH_EAST;
			case UP_NORTH_EAST:
				return UP_SOUTH_EAST;
			case UP_SOUTH_EAST:
				return UP_SOUTH_WEST;
			case UP_SOUTH_WEST:
				return UP_NORTH_WEST;
			case UP_NORTH_WEST:
				return UP_NORTH_EAST;
			case NORTH:
				return EAST;
			case EAST:
				return SOUTH;
			case SOUTH:
				return WEST;
			case WEST:
				return NORTH;
			default:
				throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		}
	}

	public EnumFacingMultiple rotateYCCW()
	{
		switch (this)
		{
			case UP:
				return UP;
			case DOWN:
				return DOWN;
			case NORTH_EAST:
				return NORTH_WEST;
			case NORTH_WEST:
				return SOUTH_WEST;
			case SOUTH_WEST:
				return SOUTH_EAST;
			case SOUTH_EAST:
				return NORTH_EAST;
			case UP_NORTH:
				return UP_WEST;
			case UP_WEST:
				return UP_SOUTH;
			case UP_SOUTH:
				return UP_EAST;
			case UP_EAST:
				return UP_NORTH;
			case DOWN_NORTH:
				return DOWN_WEST;
			case DOWN_WEST:
				return DOWN_SOUTH;
			case DOWN_SOUTH:
				return DOWN_EAST;
			case DOWN_EAST:
				return DOWN_NORTH;
			case DOWN_NORTH_EAST:
				return DOWN_NORTH_WEST;
			case DOWN_NORTH_WEST:
				return DOWN_SOUTH_WEST;
			case DOWN_SOUTH_WEST:
				return DOWN_SOUTH_EAST;
			case DOWN_SOUTH_EAST:
				return DOWN_NORTH_EAST;
			case UP_NORTH_EAST:
				return UP_NORTH_WEST;
			case UP_NORTH_WEST:
				return UP_SOUTH_WEST;
			case UP_SOUTH_WEST:
				return UP_SOUTH_EAST;
			case UP_SOUTH_EAST:
				return UP_NORTH_EAST;
			case NORTH:
				return WEST;
			case EAST:
				return NORTH;
			case SOUTH:
				return EAST;
			case WEST:
				return SOUTH;
			default:
				throw new IllegalStateException("Unable to get CCW facing of " + this);
		}
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	public enum Plane
	{
		HORIZONTAL, VERTICAL_POSITIVE, VERTICAL_NEGATIVE, DIAGONAL
	}
}
