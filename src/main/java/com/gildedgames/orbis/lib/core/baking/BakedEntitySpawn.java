package com.gildedgames.orbis.lib.core.baking;

import com.gildedgames.orbis.lib.processing.DataPrimer;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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

		EntityType<?> type = ((SpawnEggItem) this.egg.getItem()).getType(this.egg.getTag());
		type.spawn(world, this.egg, null, this.pos, SpawnReason.STRUCTURE, false, false);
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
	public void write(CompoundNBT tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.setStack("egg", this.egg);
		funnel.setPos("pos", this.pos);
		tag.putBoolean("customRotation", this.customRotation);
		tag.putFloat("rotationDegrees", this.rotationDegrees);
	}

	@Override
	public void read(CompoundNBT tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.egg = funnel.getStack("egg");
		this.pos = funnel.getPos("pos");
		this.customRotation = tag.getBoolean("customRotation");
		this.rotationDegrees = tag.getFloat("rotationDegrees");
	}
}
