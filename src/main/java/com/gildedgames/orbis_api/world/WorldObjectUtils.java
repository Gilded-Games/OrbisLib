package com.gildedgames.orbis_api.world;

import com.gildedgames.orbis_api.data.region.IShape;
import com.gildedgames.orbis_api.util.RegionHelp;
import net.minecraft.world.World;

public class WorldObjectUtils
{

	public static IShape getIntersectingShape(World world, final IShape shape)
	{
		WorldObjectManager manager = WorldObjectManager.get(world);

		for (final IWorldObject obj : manager.getObjects())
		{
			if (obj instanceof IShape)
			{
				final IShape area = (IShape) obj;

				if (RegionHelp.intersects2D(area, shape))
				{
					return area;
				}
			}

		}

		return null;
	}

}
