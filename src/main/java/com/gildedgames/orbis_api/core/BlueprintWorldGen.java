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

	private final BlueprintDefinitionPool defPool;

	private BakedBlueprint lastBake;

	private BlueprintDefinition chosenDef;

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

			final ICreationData<CreationData> data = new CreationData(world);

			data.rotation(rotation);

			this.chosenDef = this.def == null ? this.defPool.getRandomDefinition(rand) : this.def;

			this.lastBake = new BakedBlueprint(this.chosenDef.getData(), data);
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