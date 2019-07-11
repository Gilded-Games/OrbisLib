package com.gildedgames.orbis.lib.util.mc;

import com.gildedgames.orbis.lib.processing.DataPrimer;
import net.minecraft.entity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityUtil
{
	public static <T extends Entity> T spawnCreature(DataPrimer primer, World world, @Nullable EntityType<T> entityID, double x, double y, double z,
			SpawnReason reason, boolean customRotation, float rotationDegrees)
	{
		if (entityID != null)
		{
			T entity = null;

			for (int i = 0; i < 1; ++i)
			{
				entity = entityID.create(world);

				if (entity instanceof LivingEntity)
				{
					LivingEntity entityliving = (LivingEntity) entity;
					entity.setLocationAndAngles(x, y, z,
							customRotation ? MathHelper.wrapDegrees(rotationDegrees) : MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
					entityliving.rotationYawHead = entityliving.rotationYaw;
					entityliving.renderYawOffset = entityliving.rotationYaw;

					if (entityliving instanceof MobEntity)
					{
						((MobEntity) entityliving).onInitialSpawn(world, world.getDifficultyForLocation(new BlockPos(entityliving)), reason, null, null);
					}

					primer.spawn(entity);
				}
			}

			return entity;
		}
		else
		{
			return null;
		}
	}

}
