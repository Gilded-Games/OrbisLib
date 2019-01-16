package com.gildedgames.orbis_api.core;

import com.gildedgames.orbis_api.core.baking.BakedBlueprint;
import com.gildedgames.orbis_api.core.util.BlueprintPlacer;
import com.gildedgames.orbis_api.processing.DataPrimer;
import com.gildedgames.orbis_api.processing.IBlockAccessExtended;
import com.gildedgames.orbis_api.world.IWorldGen;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlueprintWorldGen implements IWorldGen
{
	private final BlueprintDefinition def;

	private BakedBlueprint baked;

	private final BakedBlueprint[] cachedRotations = new BakedBlueprint[4];

	public BlueprintWorldGen(final BlueprintDefinition def)
	{
		this.def = def;
	}

	@Override
	public boolean generate(final IBlockAccessExtended blockAccess, final World world, final Random rand, final BlockPos position, final boolean centered)
	{
		if (this.baked == null)
		{
			final Rotation rotation = this.def.hasRandomRotation() ? BlueprintPlacer.getRandomRotation(rand) : Rotation.NONE;

			BakedBlueprint cachedBlueprint = this.cachedRotations[rotation.ordinal()];

			if (cachedBlueprint == null)
			{
				final ICreationData<CreationData> data = new CreationData(world);
				data.rotation(rotation);

				cachedBlueprint = new BakedBlueprint(this.def, data);

				this.cachedRotations[rotation.ordinal()] = cachedBlueprint;
			}

			this.baked = cachedBlueprint;
		}

		DataPrimer primer = new DataPrimer(blockAccess);

		boolean result = primer.canGenerate(this.baked, position, true);

		if (result)
		{
			primer.place(this.baked, position);

			this.baked = null;
		}

		return result;
	}

	@Override
	public boolean generate(final IBlockAccessExtended blockAccess, final World world, final Random rand, final BlockPos position)
	{
		return this.generate(blockAccess, world, rand, position, false);
	}
}