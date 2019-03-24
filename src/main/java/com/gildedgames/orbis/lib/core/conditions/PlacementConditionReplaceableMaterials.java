package com.gildedgames.orbis.lib.core.conditions;

import com.gildedgames.orbis.lib.block.BlockDataContainer;
import com.gildedgames.orbis.lib.core.PlacementCondition;
import com.gildedgames.orbis.lib.core.baking.BakedBlueprint;
import com.gildedgames.orbis.lib.core.util.BlueprintUtil;
import com.gildedgames.orbis.lib.util.ArrayHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class PlacementConditionReplaceableMaterials implements PlacementCondition
{
	private final Material[] acceptedMaterials;

	public PlacementConditionReplaceableMaterials(Material... acceptedMaterials)
	{
		this.acceptedMaterials = acceptedMaterials;
	}

	@Override
	public boolean validate(IBlockReader access, BakedBlueprint blueprint, BlockPos offset)
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

					if (!blueprintBlock.getMaterial().isSolid())
					{
						continue;
					}

					pos.setPos(offset.getX() + x, offset.getY() + y, offset.getZ() + z);

					final IBlockState worldBlock = access.getBlockState(pos);

					if (BlueprintUtil.isReplaceable(worldBlock) || ArrayHelper.contains(this.acceptedMaterials, worldBlock.getMaterial()))
					{
						continue;
					}

					if (blueprintBlock == worldBlock)
					{
						continue;
					}

					return false;
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
