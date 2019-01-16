package com.gildedgames.orbis_api.core.conditions;

import com.gildedgames.orbis_api.block.BlockDataContainer;
import com.gildedgames.orbis_api.core.PlacementCondition;
import com.gildedgames.orbis_api.core.baking.BakedBlueprint;
import com.gildedgames.orbis_api.processing.IBlockAccessExtended;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.ArrayUtils;

public class PlacementConditionOnBlock implements PlacementCondition
{
	private final Block[] blocks;

	public PlacementConditionOnBlock(Block... blocks)
	{
		this.blocks = blocks;
	}

	@Override
	public boolean validate(IBlockAccessExtended access, BakedBlueprint blueprint, BlockPos offset)
	{
		BlockDataContainer container = blueprint.getBlockData();

		int floorHeight = blueprint.getDefinition().getFloorHeight();

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

				if (!access.canAccess(pos) || !ArrayUtils.contains(this.blocks, access.getBlockState(pos).getBlock()))
				{
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public int getPriority()
	{
		return 0;
	}
}
