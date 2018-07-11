package com.gildedgames.orbis_api.core.util;

import com.gildedgames.orbis_api.core.BakedBlueprint;
import com.gildedgames.orbis_api.core.PlacementCondition;
import com.gildedgames.orbis_api.processing.BlockAccessExtendedWrapper;
import com.gildedgames.orbis_api.processing.DataPrimer;
import com.google.common.collect.Lists;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlueprintPlacer
{

	public static final Rotation[] ROTATIONS = Rotation.values();

	/** Filled with block access instances for each world
	 */
	private static final List<DataPrimer> primers = Lists.newArrayList();

	public static Rotation getRandomRotation(final Random rand)
	{
		return BlueprintPlacer.ROTATIONS[rand.nextInt(BlueprintPlacer.ROTATIONS.length)];
	}

	public static boolean place(final World placeWith, BakedBlueprint baked, List<PlacementCondition> conditions, BlockPos relocateTo,
			boolean checkAreaLoaded)
	{
		DataPrimer chosen = null;

		for (final DataPrimer primer : BlueprintPlacer.primers)
		{
			if (primer.getAccess().getWorld() == placeWith)
			{
				chosen = primer;
				break;
			}
		}

		if (chosen == null)
		{
			chosen = new DataPrimer(new BlockAccessExtendedWrapper(placeWith));

			BlueprintPlacer.primers.add(chosen);
		}

		return BlueprintPlacer.place(chosen, baked, conditions, relocateTo, checkAreaLoaded);
	}

	public static boolean place(final DataPrimer placeWith, BakedBlueprint baked, List<PlacementCondition> conditions, BlockPos relocateTo,
			boolean checkAreaLoaded)
	{
		final boolean result = placeWith.canGenerate(baked, conditions, relocateTo, checkAreaLoaded);

		if (result)
		{
			baked.rebake(relocateTo);

			placeWith.create(baked);
		}

		return result;
	}

	public static boolean place(final DataPrimer placeWith, BakedBlueprint baked, List<PlacementCondition> conditions,
			boolean checkAreaLoaded)
	{
		final boolean result = placeWith.canGenerate(baked, conditions, checkAreaLoaded);

		if (result)
		{
			placeWith.create(baked);
		}

		return result;
	}

	public static boolean findPlace(final DataPrimer placeWith, BakedBlueprint baked, List<PlacementCondition> conditions, BlockPos relocateTo,
			boolean checkAreaLoaded)
	{
		return placeWith.canGenerate(baked, conditions, relocateTo, checkAreaLoaded);
	}

	public static boolean findPlace(final DataPrimer placeWith, BakedBlueprint baked, List<PlacementCondition> conditions,
			boolean checkAreaLoaded)
	{
		return placeWith.canGenerate(baked, conditions, checkAreaLoaded);
	}

}
