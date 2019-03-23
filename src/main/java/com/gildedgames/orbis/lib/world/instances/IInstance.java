package com.gildedgames.orbis.lib.world.instances;

import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.dimension.DimensionType;

import java.util.List;

public interface IInstance extends NBT
{

	void onJoin(EntityPlayer player);

	void onLeave(EntityPlayer player);

	void onRespawn(EntityPlayer player);

	List<EntityPlayer> getPlayers();

	DimensionType getDimensionType();

	int getDimensionId();

	/**
	 * Decides whether or not a dimension will be deleted after all players have left.
	 *
	 * @return Whether or not this instance is temporary.
	 */
	boolean isTemporary();
}
