package com.gildedgames.orbis_api.core.conditions;

import com.gildedgames.orbis_api.block.BlockDataContainer;
import com.gildedgames.orbis_api.core.PlacementCondition;
import com.gildedgames.orbis_api.core.baking.BakedBlueprint;
import com.gildedgames.orbis_api.core.util.BlueprintUtil;
import com.gildedgames.orbis_api.processing.IBlockAccessExtended;
import com.gildedgames.orbis_api.util.mc.BlockUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.ArrayUtils;

public class PlacementConditionReplaceableMaterials implements PlacementCondition
{
	private final Material[] acceptedMaterials;

	public PlacementConditionReplaceableMaterials(Material... acceptedMaterials)
	{
		this.acceptedMaterials = acceptedMaterials;
	}

	@Override
	public boolean validate(IBlockAccessExtended access, BakedBlueprint blueprint, BlockPos offset)
	{
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

					boolean validMaterial = ArrayUtils.contains(this.acceptedMaterials, worldBlock.getMaterial());

					if (!BlueprintUtil.isReplaceable(worldBlock) && !validMaterial)
					{
						return false;
					}

					if (blueprintBlock != worldBlock && !validMaterial)
					{
						return false;
					}
				}
			}
		}

		return true;
	}

	@Override
	public int getPriority()
	{
		return 10;
	}
}
