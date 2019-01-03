package com.gildedgames.orbis_api.core;

import com.gildedgames.orbis_api.core.baking.BakedBlueprint;
import com.gildedgames.orbis_api.core.util.BlueprintPlacer;
import com.gildedgames.orbis_api.processing.DataPrimer;
import com.gildedgames.orbis_api.processing.IBlockAccessExtended;
import com.gildedgames.orbis_api.world.IWorldGen;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Random;

public class BlueprintWorldGen implements IWorldGen
{

	private final BlueprintDefinition def;

	private final BlueprintDefinitionPool defPool;

	private BakedBlueprint lastBake;

	private BlueprintDefinition chosenDef;

	private final HashMap<BlueprintDefinition, BakedBlueprint[]> cachedRotations = new HashMap<>();

	public BlueprintWorldGen(final BlueprintDefinitionPool defPool)
	{
		this.def = null;
		this.defPool = defPool;
	}

	public BlueprintWorldGen(final BlueprintDefinition def)
	{
		this.def = def;
		this.defPool = null;
	}

	@Override
	public boolean generate(final IBlockAccessExtended blockAccess, final World world, final Random rand, final BlockPos position, final boolean centered)
	{
		if (this.lastBake == null)
		{
			final Rotation rotation = (this.def != null ? this.def.hasRandomRotation() : this.defPool.hasRandomRotation()) ?
					BlueprintPlacer.ROTATIONS[rand.nextInt(BlueprintPlacer.ROTATIONS.length)] :
					BlueprintPlacer.ROTATIONS[0];

			this.chosenDef = this.def == null ? this.defPool.getRandomDefinition(rand) : this.def;

			final BakedBlueprint[] cachedRotations;

			if (this.cachedRotations.containsKey(this.chosenDef))
			{
				cachedRotations = this.cachedRotations.get(this.chosenDef);
			}
			else
			{
				cachedRotations = new BakedBlueprint[4];

				this.cachedRotations.put(this.chosenDef, cachedRotations);
			}

			BakedBlueprint cachedBlueprint = cachedRotations[rotation.ordinal()];

			if (cachedBlueprint == null)
			{
				final ICreationData<CreationData> data = new CreationData(world);
				data.rotation(rotation);

				cachedBlueprint = new BakedBlueprint(this.chosenDef.getData(), data);

				cachedRotations[rotation.ordinal()] = cachedBlueprint;
			}

			this.lastBake = cachedBlueprint;
		}

		DataPrimer primer = new DataPrimer(blockAccess);

		boolean generated = BlueprintPlacer.place(primer, this.lastBake, this.chosenDef.getConditions(), position, true);

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