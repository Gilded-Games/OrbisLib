package com.gildedgames.orbis.lib.core.util;

import com.gildedgames.orbis.lib.core.PostPlacement;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Function;

public class PostPlacements
{

	public static PostPlacement spawnEntity(final Function<World, LivingEntity> e, final BlockPos offset)
	{
		return (world, rand, data, container) ->
		{
			if (world.isRemote)
			{
				return;
			}

			final BlockPos spawnAt = data.getPos().add(container.getWidth() / 2, 0, container.getLength() / 2).add(offset);

			final LivingEntity entity = e.apply(world);

			entity.setPositionAndUpdate(spawnAt.getX(), spawnAt.getY(), spawnAt.getZ());

			world.addEntity(entity);
		};
	}

}
