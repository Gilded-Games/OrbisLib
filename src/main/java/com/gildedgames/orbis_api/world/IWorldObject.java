package com.gildedgames.orbis_api.world;

import com.gildedgames.orbis_api.data.management.IData;
import com.gildedgames.orbis_api.data.region.IShape;
import com.gildedgames.orbis_api.util.mc.NBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The most basic object the world can hold
 * Each object has a position and a renderer
 */
public interface IWorldObject extends NBT
{

	void markDirty();

	void markClean();

	boolean isDirty();

	World getWorld();

	void setWorld(World world);

	BlockPos getPos();

	void setPos(BlockPos pos);

	IShape getShape();

	@SideOnly(Side.CLIENT)
	IWorldRenderer getRenderer();

	IData getData();

	void onUpdate();

}
