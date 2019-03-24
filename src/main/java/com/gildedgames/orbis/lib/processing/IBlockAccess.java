package com.gildedgames.orbis.lib.processing;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.World;

public interface IBlockAccess extends IWorldWriter, IBlockReader
{
	boolean isAreaLoaded(int minX, int minY, int minZ, int maxX, int maxY, int maxZ);

	World getWorld();

	void setTileEntity(BlockPos pos, TileEntity te);
}
