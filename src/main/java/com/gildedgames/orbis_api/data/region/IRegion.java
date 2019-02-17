package com.gildedgames.orbis_api.data.region;

import net.minecraft.util.math.BlockPos;

public interface IRegion extends IDimensions, IShape, Iterable<BlockPos.MutableBlockPos>
{

	BlockPos getMin();

	BlockPos getMax();

}
