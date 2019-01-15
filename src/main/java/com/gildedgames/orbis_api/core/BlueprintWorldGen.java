package com.gildedgames.orbis_api.core;

import com.gildedgames.orbis_api.core.baking.BakedBlueprint;
import com.gildedgames.orbis_api.core.util.BlueprintPlacer;
import com.gildedgames.orbis_api.processing.DataPrimer;
import com.gildedgames.orbis_api.processing.IBlockAccessExtended;
import com.gildedgames.orbis_api.world.IWorldGen;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlueprintWorldGen implements IWorldGen
{
	@Nonnull
	private final BlueprintDefinition def;

	private BakedBlueprint lastBake;

	private final BakedBlueprint[] cachedRotations = new BakedBlueprint[4];

	public BlueprintWorldGen(final BlueprintDefinition def)
	{
		this.def = def;
	}

	@Override
	public boolean generate(final IBlockAccessExtended blockAccess, final World world, final Random rand, final BlockPos position, final boolean centered)
	{
		if (this.lastBake == null)
		{
			final Rotation rotation = this.def.hasRandomRotation() ?
					BlueprintPlacer.ROTATIONS[rand.nextInt(BlueprintPlacer.ROTATIONS.length)] :
					BlueprintPlacer.ROTATIONS[0];

			BakedBlueprint cachedBlueprint = this.cachedRotations[rotation.ordinal()];

			if (cachedBlueprint == null)
			{
				final ICreationData<CreationData> data = new CreationData(world);
				data.rotation(rotation);

				cachedBlueprint = new BakedBlueprint(this.def.getData(), data);

				this.cachedRotations[rotation.ordinal()] = cachedBlueprint;
			}

			this.lastBake = cachedBlueprint;
		}

		DataPrimer primer = new DataPrimer(blockAccess);

		boolean generated = BlueprintPlacer.place(primer, this.lastBake, this.def.getConditions(), position, true);

		if (generated)
		{
			this.lastBake = null;
		}

		return generated;
	}

	@Override
	public boolean generate(final IBlockAccessExtended blockAccess, final World world, final Random rand, final BlockPos position)
	{
		return this.generate(blockAccess, world, rand, position, false);
	}
}