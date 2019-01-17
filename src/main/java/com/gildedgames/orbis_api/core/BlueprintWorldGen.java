package com.gildedgames.orbis_api.core;

import com.gildedgames.orbis_api.core.baking.BakedBlueprint;
import com.gildedgames.orbis_api.core.util.BlueprintPlacer;
import com.gildedgames.orbis_api.processing.BlockAccessWorldSlice;
import com.gildedgames.orbis_api.processing.DataPrimer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class BlueprintWorldGen extends WorldGenerator
{
	private final BlueprintDefinition def;

	private BakedBlueprint baked;

	public BlueprintWorldGen(final BlueprintDefinition def)
	{
		this.def = def;
	}

	@Override
	public boolean generate(final World world, final Random rand, final BlockPos position)
	{
		if (this.baked == null)
		{
			final Rotation rotation = this.def.hasRandomRotation() ? BlueprintPlacer.getRandomRotation(rand) : Rotation.NONE;

			final ICreationData<CreationData> data = new CreationData(world);
			data.rotation(rotation);

			this.baked = new BakedBlueprint(this.def, data);
		}

		// We should choose a better location for blueprints which generate at an offset
		BlockPos offsetPosition = position.up(this.def.getFloorHeight());

		DataPrimer primer = new DataPrimer(new BlockAccessWorldSlice(world, new ChunkPos(offsetPosition)));
		boolean result = primer.canGenerate(this.baked, offsetPosition);

		if (result)
		{
			primer.place(this.baked, offsetPosition);

			this.baked = null;
		}

		return result;
	}
}