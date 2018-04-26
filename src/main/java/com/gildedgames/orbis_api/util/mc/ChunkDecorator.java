package com.gildedgames.orbis_api.util.mc;

import com.gildedgames.orbis_api.ReflectionOrbis;
import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChunkDecorator extends Chunk
{
	private Chunk c;

	public ChunkDecorator(Chunk chunk)
	{
		super(null, 0, 0);

		this.c = chunk;
	}

	@Override
	public boolean isAtLocation(int x, int z)
	{
		return this.c.isAtLocation(x, z);
	}

	@Override
	public int getHeight(BlockPos pos)
	{
		return this.c.getHeight(pos);
	}

	@Override
	public int getHeightValue(int x, int z)
	{
		return this.c.getHeightValue(x, z);
	}

	@Override
	public int getTopFilledSegment()
	{
		return this.c.getTopFilledSegment();
	}

	@Override
	public ExtendedBlockStorage[] getBlockStorageArray()
	{
		return this.c.getBlockStorageArray();
	}

	@Override
	protected void generateHeightMap()
	{
		ReflectionOrbis.invokeMethod(ReflectionOrbis.GENERATE_HEIGHT_MAP, this.c);
	}

	@Override
	public void generateSkylightMap()
	{
		this.c.generateSkylightMap();
	}

	@Override
	public int getBlockLightOpacity(BlockPos pos)
	{
		return this.c.getBlockLightOpacity(pos);
	}

	@Override
	public IBlockState getBlockState(BlockPos pos)
	{
		return this.c.getBlockState(pos);
	}

	@Override
	public IBlockState getBlockState(final int x, final int y, final int z)
	{
		return this.c.getBlockState(x, y, z);
	}

	@Override
	public IBlockState setBlockState(BlockPos pos, IBlockState state)
	{
		return this.c.setBlockState(pos, state);
	}

	@Override
	public int getLightFor(EnumSkyBlock type, BlockPos pos)
	{
		return this.c.getLightFor(type, pos);
	}

	@Override
	public void setLightFor(EnumSkyBlock type, BlockPos pos, int value)
	{
		this.c.setLightFor(type, pos, value);
	}

	@Override
	public int getLightSubtracted(BlockPos pos, int amount)
	{
		return this.c.getLightSubtracted(pos, amount);
	}

	@Override
	public void addEntity(Entity entityIn)
	{
		this.c.addEntity(entityIn);
	}

	@Override
	public void removeEntity(Entity entityIn)
	{
		this.c.removeEntity(entityIn);
	}

	@Override
	public void removeEntityAtIndex(Entity entityIn, int index)
	{
		this.c.removeEntityAtIndex(entityIn, index);
	}

	@Override
	public boolean canSeeSky(BlockPos pos)
	{
		return this.c.canSeeSky(pos);
	}

	@Override
	public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType creationMode)
	{
		return this.c.getTileEntity(pos, creationMode);
	}

	@Override
	public void addTileEntity(TileEntity tileEntityIn)
	{
		this.c.addTileEntity(tileEntityIn);
	}

	@Override
	public void addTileEntity(BlockPos pos, TileEntity tileEntityIn)
	{
		this.c.addTileEntity(pos, tileEntityIn);
	}

	@Override
	public void removeTileEntity(BlockPos pos)
	{
		this.c.removeTileEntity(pos);
	}

	@Override
	public void onLoad()
	{
		this.c.onLoad();
	}

	@Override
	public void onUnload()
	{
		this.c.onUnload();
	}

	@Override
	public void markDirty()
	{
		this.c.markDirty();
	}

	@Override
	public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> filter)
	{
		this.c.getEntitiesWithinAABBForEntity(entityIn, aabb, listToFill, filter);
	}

	@Override
	public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill,
			Predicate<? super T> filter)
	{
		this.c.getEntitiesOfTypeWithinAABB(entityClass, aabb, listToFill, filter);
	}

	@Override
	public boolean needsSaving(boolean p_76601_1_)
	{
		return this.c.needsSaving(p_76601_1_);
	}

	@Override
	public Random getRandomWithSeed(long seed)
	{
		return this.c.getRandomWithSeed(seed);
	}

	@Override
	public boolean isEmpty()
	{
		return this.c.isEmpty();
	}

	@Override
	public void populate(IChunkProvider chunkProvider, IChunkGenerator chunkGenrator)
	{
		this.c.populate(chunkProvider, chunkGenrator);
	}

	@Override
	protected void populate(IChunkGenerator generator)
	{
		ReflectionOrbis.invokeMethod(ReflectionOrbis.POPULATE, this.c, generator);
	}

	@Override
	public BlockPos getPrecipitationHeight(BlockPos pos)
	{
		return this.c.getPrecipitationHeight(pos);
	}

	@Override
	public void onTick(boolean skipRecheckGaps)
	{
		this.c.onTick(skipRecheckGaps);
	}

	@Override
	public boolean isPopulated()
	{
		return this.c.isPopulated();
	}

	@Override
	public boolean wasTicked()
	{
		return this.c.wasTicked();
	}

	@Override
	public ChunkPos getPos()
	{
		return this.c.getPos();
	}

	@Override
	public boolean isEmptyBetween(int startY, int endY)
	{
		return this.c.isEmptyBetween(startY, endY);
	}

	@Override
	public void setStorageArrays(ExtendedBlockStorage[] newStorageArrays)
	{
		this.c.setStorageArrays(newStorageArrays);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void read(PacketBuffer buf, int availableSections, boolean groundUpContinuous)
	{
		this.c.read(buf, availableSections, groundUpContinuous);
	}

	@Override
	public Biome getBiome(BlockPos pos, BiomeProvider provider)
	{
		return this.c.getBiome(pos, provider);
	}

	@Override
	public byte[] getBiomeArray()
	{
		return this.c.getBiomeArray();
	}

	@Override
	public void setBiomeArray(byte[] biomeArray)
	{
		this.c.setBiomeArray(biomeArray);
	}

	@Override
	public void resetRelightChecks()
	{
		this.c.resetRelightChecks();
	}

	@Override
	public void enqueueRelightChecks()
	{
		this.c.enqueueRelightChecks();
	}

	@Override
	public void checkLight()
	{
		this.c.checkLight();
	}

	@Override
	public boolean isLoaded()
	{
		return this.c.isLoaded();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void markLoaded(boolean loaded)
	{
		this.c.markLoaded(loaded);
	}

	@Override
	public World getWorld()
	{
		return this.c.getWorld();
	}

	@Override
	public int[] getHeightMap()
	{
		return this.c.getHeightMap();
	}

	@Override
	public void setHeightMap(int[] newHeightMap)
	{
		this.c.setHeightMap(newHeightMap);
	}

	@Override
	public Map<BlockPos, TileEntity> getTileEntityMap()
	{
		return this.c.getTileEntityMap();
	}

	@Override
	public ClassInheritanceMultiMap<Entity>[] getEntityLists()
	{
		return this.c.getEntityLists();
	}

	@Override
	public boolean isTerrainPopulated()
	{
		return this.c.isTerrainPopulated();
	}

	@Override
	public void setTerrainPopulated(boolean terrainPopulated)
	{
		this.c.setTerrainPopulated(terrainPopulated);
	}

	@Override
	public boolean isLightPopulated()
	{
		return this.c.isLightPopulated();
	}

	@Override
	public void setLightPopulated(boolean lightPopulated)
	{
		this.c.setLightPopulated(lightPopulated);
	}

	@Override
	public void setModified(boolean modified)
	{
		this.c.setModified(modified);
	}

	@Override
	public void setHasEntities(boolean hasEntitiesIn)
	{
		this.c.setHasEntities(hasEntitiesIn);
	}

	@Override
	public void setLastSaveTime(long saveTime)
	{
		this.c.setLastSaveTime(saveTime);
	}

	@Override
	public int getLowestHeight()
	{
		return this.c.getLowestHeight();
	}

	@Override
	public long getInhabitedTime()
	{
		return this.c.getInhabitedTime();
	}

	@Override
	public void setInhabitedTime(long newInhabitedTime)
	{
		this.c.setInhabitedTime(newInhabitedTime);
	}

	@Override
	public void removeInvalidTileEntity(BlockPos pos)
	{
		this.c.removeInvalidTileEntity(pos);
	}

	@Override
	@Nullable
	public net.minecraftforge.common.capabilities.CapabilityDispatcher getCapabilities()
	{
		return this.c.getCapabilities();
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable EnumFacing facing)
	{
		return this.c.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable EnumFacing facing)
	{
		return this.c.getCapability(capability, facing);
	}
}
