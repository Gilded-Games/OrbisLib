package com.gildedgames.orbis.lib.core.util;

import com.gildedgames.orbis.lib.data.region.IDimensions;
import com.gildedgames.orbis.lib.data.region.IRegion;
import com.gildedgames.orbis.lib.data.region.Region;
import com.gildedgames.orbis.lib.processing.IBlockAccessExtended;
import com.gildedgames.orbis.lib.util.RotationHelp;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class BlueprintUtil
{

	public static IRegion getRegionFromDefinition(final IDimensions data, BlockPos pos, Rotation rotation)
	{
		final IRegion region =
				rotation == Rotation.NONE ? new Region(data) : RotationHelp.rotate(new Region(data), rotation);

		return (IRegion) region.translate(pos);
	}

	public static ChunkPos[] getChunksInsideTemplate(final IDimensions data, BlockPos pos, Rotation rotation)
	{
		final IRegion bb = BlueprintUtil.getRegionFromDefinition(data, pos, rotation);

		final int minX = Math.min(bb.getMin().getX(), bb.getMax().getX());
		final int minZ = Math.min(bb.getMin().getZ(), bb.getMax().getZ());

		final int maxX = Math.max(bb.getMin().getX(), bb.getMax().getX());
		final int maxZ = Math.max(bb.getMin().getZ(), bb.getMax().getZ());

		final int startChunkX = minX >> 4;
		final int startChunkY = minZ >> 4;

		final int endChunkX = maxX >> 4;
		final int endChunkY = maxZ >> 4;

		final int width = endChunkX - startChunkX + 1;
		final int length = endChunkY - startChunkY + 1;

		final ChunkPos[] chunks = new ChunkPos[width * length];

		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < length; y++)
			{
				chunks[x + (y * width)] = new ChunkPos(startChunkX + x, startChunkY + y);
			}
		}

		return chunks;
	}

	public static boolean canGrowInto(final Block block)
	{
		final Material material = block.getDefaultState().getMaterial();

		return material == Material.AIR || material == Material.LEAVES || material == Material.PLANTS || material == Material.SNOW;
	}

	public static boolean isReplaceable(final IBlockAccessExtended world, final BlockPos pos)
	{
		final IBlockState state = world.getBlockState(pos);

		return state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos)
				|| BlueprintUtil.canGrowInto(state.getBlock());
	}

	public static boolean isReplaceable(final IBlockState state)
	{
		Material material = state.getMaterial();

		return material == Material.AIR || material == Material.LEAVES || material == Material.PLANTS || material == Material.SNOW;
	}

}
