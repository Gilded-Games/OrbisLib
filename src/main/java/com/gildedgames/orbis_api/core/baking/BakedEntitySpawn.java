package com.gildedgames.orbis_api.core.baking;

import com.gildedgames.orbis_api.processing.DataPrimer;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BakedEntitySpawn implements IBakedPosAction
{
	private ItemStack egg;

	private BlockPos pos;

	private BakedEntitySpawn()
	{

	}

	public BakedEntitySpawn(ItemStack egg, BlockPos pos)
	{
		this.egg = egg;
		this.pos = pos;
	}

	public static Entity spawnCreature(DataPrimer primer, World worldIn, @Nullable ResourceLocation entityID, double x, double y, double z)
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
					entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
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

	@Override
	public BlockPos getPos()
	{
		return this.pos;
	}

	@Override
	public void setPos(BlockPos pos)
	{
		this.pos = pos;
	}

	@Override
	public void call(DataPrimer primer)
	{
		World world = primer.getWorld();
		Entity entity = spawnCreature(primer, world, ItemMonsterPlacer.getNamedIdFrom(this.egg), (double) this.pos.getX() + 0.5D, (double) this.pos.getY(),
				(double) this.pos.getZ() + 0.5D);

		if (entity != null)
		{
			if (entity instanceof EntityLivingBase && this.egg.hasDisplayName())
			{
				entity.setCustomNameTag(this.egg.getDisplayName());
			}

			if (world != null)
			{
				ItemMonsterPlacer.applyItemEntityDataToEntity(world, null, this.egg, entity);
			}
		}
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.setStack("egg", this.egg);
		funnel.setPos("pos", this.pos);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.egg = funnel.getStack("egg");
		this.pos = funnel.getPos("pos");
	}
}
