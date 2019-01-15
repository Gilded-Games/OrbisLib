package com.gildedgames.orbis_api.core.util;

import com.gildedgames.orbis_api.block.BlockDataContainer;
import com.gildedgames.orbis_api.core.PlacementCondition;
import com.gildedgames.orbis_api.util.mc.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.ArrayUtils;

public class PlacementConditions
{

	public static PlacementCondition replaceableGround()
	{
		return replaceable(Material.GROUND, Material.GRASS, Material.AIR, Material.SNOW);
	}

	public static PlacementCondition replaceable(final Material... acceptedMaterials)
	{
		return (access, blueprint, offset) -> {
			BlockDataContainer container = blueprint.getBlockData();

			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

			for (int x = 0; x < container.getWidth(); x++)
			{
				for (int z = 0; z < container.getLength(); z++)
				{
					for (int y = 0; y < container.getHeight(); y++)
					{
						final IBlockState blueprintBlock = container.getBlockState(x, y, z);

						if (blueprintBlock.getBlock() == Blocks.STRUCTURE_VOID)
						{
							continue;
						}

						if (!BlockUtil.isSolid(blueprintBlock) && blueprintBlock.getMaterial() != Material.PORTAL
								&& blueprintBlock.getMaterial() != Material.AIR)
						{
							continue;
						}

						pos.setPos(offset.getX() + x, offset.getY() + y, offset.getZ() + z);

						if (!access.canAccess(pos))
						{
							return false;
						}

						final IBlockState worldBlock = access.getBlockState(pos);

						if (!BlueprintUtil.isReplaceable(worldBlock) && !ArrayUtils.contains(acceptedMaterials, worldBlock.getMaterial()))
						{
							if (blueprintBlock != worldBlock)
							{
								return false;
							}
						}
					}
				}
			}

			return true;
		};
	}

	public static PlacementCondition onSpecificBlock(final int floorHeight, final Block... blocks)
	{
		return (access, blueprint, offset) -> {
			BlockDataContainer container = blueprint.getBlockData();

			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

			for (int x = 0; x < container.getWidth(); x++)
			{
				for (int z = 0; z < container.getLength(); z++)
				{
					IBlockState blueprintBlock = container.getBlockState(x, floorHeight, z);

					if (blueprintBlock.getBlock() == Blocks.AIR || blueprintBlock.getBlock() == Blocks.STRUCTURE_VOID)
					{
						continue;
					}

					pos.setPos(offset.getX() + x, offset.getY() + floorHeight - 1, offset.getZ() + z);

					if (!access.canAccess(pos) || !ArrayUtils.contains(blocks, access.getBlockState(pos).getBlock()))
					{
						return false;
					}
				}
			}

			return true;
		};
	}

	public static PlacementCondition ignoreBlock(final int floorHeight, final IBlockState ignoredState)
	{
		return (access, blueprint, offset) -> {
			BlockDataContainer container = blueprint.getBlockData();

			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

			for (int x = 0; x < container.getWidth(); x++)
			{
				for (int z = 0; z < container.getLength(); z++)
				{
					IBlockState blueprintBlock = container.getBlockState(x, floorHeight, z);

					if (blueprintBlock.getBlock() == Blocks.AIR || blueprintBlock.getBlock() == Blocks.STRUCTURE_VOID)
					{
						continue;
					}

					pos.setPos(offset.getX() + x, offset.getY() + floorHeight - 1, offset.getZ() + z);

					if (!access.canAccess(pos) || access.getBlockState(pos) == ignoredState)
					{
						return false;
					}
				}
			}

			return true;
		};
	}

}
