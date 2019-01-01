package com.gildedgames.orbis_api.core.util;

import com.gildedgames.orbis_api.core.PlacementCondition;
import com.gildedgames.orbis_api.core.baking.BakedBlueprint;
import com.gildedgames.orbis_api.preparation.impl.util.BlockAccessPrep;
import com.gildedgames.orbis_api.processing.BlockAccessExtendedWrapper;
import com.gildedgames.orbis_api.processing.DataPrimer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlueprintPlacer
{

	public static final Rotation[] ROTATIONS = Rotation.values();


	public static Rotation getRandomRotation(final Random rand)
	{
		return BlueprintPlacer.ROTATIONS[rand.nextInt(BlueprintPlacer.ROTATIONS.length)];
	}

	public static boolean place(final World placeWith, BakedBlueprint baked, List<PlacementCondition> conditions, BlockPos relocateTo,
			boolean checkAreaLoaded)
	{
		return BlueprintPlacer.place(new DataPrimer(new BlockAccessExtendedWrapper(placeWith)), baked, conditions, relocateTo, checkAreaLoaded);
	}

	public static boolean place(final DataPrimer placeWith, BakedBlueprint baked, List<PlacementCondition> conditions, BlockPos relocateTo,
			boolean checkAreaLoaded)
	{
		final boolean result = placeWith.canGenerate(baked, conditions, relocateTo, checkAreaLoaded);

		if (result)
		{
			placeWith.place(baked, relocateTo);
		}

		return result;
	}

//	public static boolean place(final DataPrimer placeWith, BakedBlueprint baked, List<PlacementCondition> conditions,
//			boolean checkAreaLoaded)
//	{
//		final boolean result = placeWith.canGenerate(baked, conditions, checkAreaLoaded);
//
//		if (result)
//		{
//			placeWith.place(baked, relocateTo);
//		}
//
//		return result;
//	}

	public static boolean findPlace(final DataPrimer placeWith, BakedBlueprint baked, List<PlacementCondition> conditions, BlockPos relocateTo, boolean checkAreaLoaded)
	{
		return placeWith.canGenerate(baked, conditions, relocateTo, checkAreaLoaded);
	}

	public static boolean findPlace(final BlockAccessPrep placeWith, BakedBlueprint baked, List<PlacementCondition> conditions, BlockPos relocateTo, boolean checkAreaLoaded)
	{
		return placeWith.canGenerate(baked, conditions, relocateTo, checkAreaLoaded);
	}
}
