package com.gildedgames.orbis_api.data.framework.generation.searching;

import com.gildedgames.orbis_api.data.framework.interfaces.EnumFacingMultiple;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.data.region.Region;
import com.gildedgames.orbis_api.util.RotationHelp;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class PathwayUtil
{
	public static EnumFacingMultiple getRotated(EnumFacingMultiple face, Rotation rotation)
	{
		int rotAmount = RotationHelp.getRotationAmount(rotation, Rotation.NONE);
		boolean goClockwise = RotationHelp.getGoClockwise(rotation, Rotation.NONE);

		if (face == EnumFacingMultiple.DOWN || face == EnumFacingMultiple.UP)
		{
			return face;
		}

		for (int j = 0; j < rotAmount; j++)
		{
			face = goClockwise ? face.rotateY() : face.rotateYCCW();
		}

		return face;
	}

	public static EnumFacingMultiple[] getRotated(EnumFacingMultiple[] faces, Rotation rotation)
	{
		EnumFacingMultiple[] newFaces = new EnumFacingMultiple[faces.length];

		for (int i = 0; i < faces.length; i++)
		{
			newFaces[i] = getRotated(faces[i], rotation);
		}

		return newFaces;
	}

	public static EnumFacingMultiple[] getOpposites(EnumFacingMultiple[] faces)
	{
		EnumFacingMultiple[] opposites = new EnumFacingMultiple[faces.length];

		for (int i = 0; i < faces.length; i++)
		{
			opposites[i] = faces[i].getOpposite();
		}

		return opposites;
	}

	public static BlockPos adjacent(BlockPos pos, EnumFacingMultiple facing)
	{
		return new BlockPos(pos.getX() + facing.getDirectionVec().getX(), pos.getY() + facing.getDirectionVec().getY(),
				pos.getZ() + facing.getDirectionVec().getZ());
	}

	public static IRegion adjacent(IRegion region, EnumFacingMultiple facing)
	{
		return new Region(adjacent(region.getMin(), facing), adjacent(region.getMax(), facing));
	}

}