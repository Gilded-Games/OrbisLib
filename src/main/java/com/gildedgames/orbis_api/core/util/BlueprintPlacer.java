package com.gildedgames.orbis_api.core.util;

import com.gildedgames.orbis_api.core.baking.BakedBlueprint;
import com.gildedgames.orbis_api.processing.BlockAccessExtendedWrapper;
import com.gildedgames.orbis_api.processing.DataPrimer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlueprintPlacer
{

	public static final Rotation[] ROTATIONS = Rotation.values();


	public static Rotation getRandomRotation(final Random rand)
	{
		return BlueprintPlacer.ROTATIONS[rand.nextInt(BlueprintPlacer.ROTATIONS.length)];
	}

	public static boolean place(final World placeWith, BakedBlueprint baked, BlockPos relocateTo)
	{
		final DataPrimer placeWith1 = new DataPrimer(new BlockAccessExtendedWrapper(placeWith));
		final boolean result = placeWith1.canGenerate(baked, relocateTo);

		if (result)
		{
			placeWith1.place(baked, relocateTo);
		}

		return result;
	}

}
