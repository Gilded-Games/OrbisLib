package com.gildedgames.orbis_api.preparation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Implementations will handle the creation of IPrepSectors.
 * Example is a registration which finds locations to place Blueprints, then
 * schedules them into the sector.
 */
public interface IPrepRegistryEntry
{
	ResourceLocation getUniqueId();

	/**
	 * @return The area size of each IPrepSectorData created by this registration.
	 */
	int getSectorChunkArea();

	boolean shouldAttachTo(World world);

	void preSectorDataSave(World world, IPrepSectorData data, IPrepChunkManager chunkManager);

	void postSectorDataSave(World world, IPrepSectorData data, IPrepChunkManager chunkManager);

	IPrepSectorData createData(World world, long seed, int sectorX, int sectorY);

	IPrepSectorData createDataAndRead(World world, NBTTagCompound tag);
}
