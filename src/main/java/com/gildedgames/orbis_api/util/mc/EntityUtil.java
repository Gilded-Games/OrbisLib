package com.gildedgames.orbis_api.util.mc;

import com.gildedgames.orbis_api.processing.DataPrimer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityUtil
{
	public static Entity spawnCreature(DataPrimer primer, World worldIn, @Nullable ResourceLocation entityID, double x, double y, double z,
			boolean customRotation, float rotationDegrees)
	{
		if (entityID != null && EntityList.ENTITY_EGGS.containsKey(entityID))
		{
			Entity entity = null;

			for (int i = 0; i < 1; ++i)
			{
				entity = EntityList.createEntityByIDFromName(entityID, worldIn);

				if (entity instanceof EntityLiving)
				{
					EntityLiving entityliving = (EntityLiving) entity;
					entity.setLocationAndAngles(x, y, z,
							customRotation ? MathHelper.wrapDegrees(rotationDegrees) : MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
					entityliving.rotationYawHead = entityliving.rotationYaw;
					entityliving.renderYawOffset = entityliving.rotationYaw;
					entityliving.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityliving)), null);
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
