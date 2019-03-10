package com.gildedgames.orbis.lib.core.baking;

import com.gildedgames.orbis.lib.processing.DataPrimer;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.util.mc.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BakedEntitySpawn implements IBakedPosAction
{
	private ItemStack egg;

	private BlockPos pos;

	private boolean customRotation;

	private float rotationDegrees;

	private BakedEntitySpawn()
	{

	}

	public BakedEntitySpawn(ItemStack egg, BlockPos pos, boolean customRotation, float rotationDegrees)
	{
		this.egg = egg;
		this.pos = pos;
		this.customRotation = customRotation;
		this.rotationDegrees = rotationDegrees;
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

		Entity entity = EntityUtil
				.spawnCreature(primer, world, ItemMonsterPlacer.getNamedIdFrom(this.egg), (double) this.pos.getX() + 0.5D, (double) this.pos.getY(),
						(double) this.pos.getZ() + 0.5, this.customRotation, this.rotationDegrees);

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
	public IBakedPosAction copy()
	{
		BakedEntitySpawn spawn = new BakedEntitySpawn();
		spawn.customRotation = this.customRotation;
		spawn.egg = this.egg;
		spawn.pos = this.pos;
		spawn.rotationDegrees = this.rotationDegrees;

		return spawn;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.setStack("egg", this.egg);
		funnel.setPos("pos", this.pos);
		tag.setBoolean("customRotation", this.customRotation);
		tag.setFloat("rotationDegrees", this.rotationDegrees);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.egg = funnel.getStack("egg");
		this.pos = funnel.getPos("pos");
		this.customRotation = tag.getBoolean("customRotation");
		this.rotationDegrees = tag.getFloat("rotationDegrees");
	}
}
