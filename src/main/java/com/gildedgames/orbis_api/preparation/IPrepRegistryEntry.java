package com.gildedgames.orbis_api.preparation;

import com.gildedgames.orbis_api.preparation.impl.ChunkMask;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * Implementations will handle the creation of IPrepSectors.
 * Example is a registration which finds locations to place Blueprints, then
 * schedules them into the sector.
 */
public interface IPrepRegistryEntry<T>
{
	ResourceLocation getUniqueId();

	/**
	 * @return The area size of each IPrepSectorData created by this registration.
	 */
	int getSectorChunkArea();

	boolean shouldAttachTo(World world);

	void postSectorDataCreate(World world, IPrepSectorData data);

	IPrepSectorData createData(World world, long seed, int sectorX, int sectorY);

	IPrepSectorData createDataAndRead(World world, NBTTagCompound tag);

	/**
	 * It is ABSOLUTELY VITAL that the implementation of this method is thread-safe.
	 * Pay close attention to the way you implement this, it is a very sensitive method,
	 * but an important aspect of your prep registration. You should be writing a thread-safe
	 * version of your chunk generation code for all the dimensions this is registered to.
	 *
	 * Some things to keep in mind: keep interaction with the rest of Minecraft's code TO A MINIMUM.
	 * This includes fetching biomes. The biomes provided here are specifically passed in a manner
	 * that will not interrupt the game's main thread. MANY ISSUES WILL ARISE IF YOU ATTEMPT TO FETCH
	 * BIOMES OR ANY OTHER DATA OUTSIDE OF THE PASSED PARAMETERS.
	 *
	 * If you absolutely must interact with your own code or Minecraft's code, make sure to use
	 * MinecraftServer.addScheduledTask() and query back the FutureTask once it is complete. It's vital
	 * that MC's data is handled on the main thread as they are not synchronized or prepared for thread
	 * communication.
	 *
	 * This should only generate the base terrain generation, not the decorations. This data is used
	 * to place Blueprints and other structure data on top.
	 * @param biomes Biomes passed through in a thread-safe manner. Do not fetch biomes any other way.
	 * @param primer The primer that this chunk will use to generate data.
	 * @param chunkX The chunk's x position.
	 * @param chunkZ The chunk's z position.
	 */
	void threadSafeGenerateChunk(World world, IPrepSectorData sectorData, Biome[] biomes, ChunkPrimer primer, int chunkX, int chunkZ);

	void threadSafeGenerateMask(T info, World world, IPrepSectorData sectorData, Biome[] biomes, ChunkMask mask, int x, int y, int z);

	IChunkMaskTransformer createMaskTransformer();

	T generateChunkColumnInfo(World world, IPrepSectorData sectorData, Biome[] biomes, int chunkX, int chunkY);
}
