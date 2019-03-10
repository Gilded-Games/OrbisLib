package com.gildedgames.orbis.lib.world.instances;

import com.gildedgames.orbis.lib.util.mc.BlockPosDimension;

public interface IPlayerInstances
{

	IInstance getInstance();

	void setInstance(IInstance instance);

	BlockPosDimension getOutside();

	void setReturnPosition(BlockPosDimension pos);

}
