package com.gildedgames.orbis_api.preparation.impl.util;

import com.gildedgames.orbis_api.preparation.IChunkMaskTransformer;
import com.gildedgames.orbis_api.preparation.impl.ChunkSegmentMask;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ChunkHelper
{
	public static <T extends Chunk> T fillChunkFromMaskSegments(T chunk, ChunkSegmentMask[] masks, IChunkMaskTransformer maskTransformer)
	{
		if (masks.length != 16)
		{
			throw new RuntimeException("Masks array must have a length of 16");
		}

		boolean flag = chunk.getWorld().provider.hasSkyLight();

		for (int chunkY = 0; chunkY < 16; chunkY++)
		{
			ChunkSegmentMask mask = masks[chunkY];

			if (mask == null)
			{
				continue;
			}

			ExtendedBlockStorage blocks = chunk.getBlockStorageArray()[chunkY];

			// Chunks store data in Y->Z->X order... improve cache locality
			for (int y = 0; y < 16; y++)
			{
				for (int z = 0; z < 16; z++)
				{
					for (int x = 0; x < 16; x++)
					{
						int blockId = mask.getBlock(x, y, z);

						if (blockId > 0)
						{
							if (blocks == Chunk.NULL_BLOCK_STORAGE)
							{
								chunk.getBlockStorageArray()[chunkY] = blocks = new ExtendedBlockStorage(chunkY << 4, flag);
							}

							IBlockState blockState = maskTransformer.remapBlock(blockId);

							blocks.set(x, y, z, blockState);
						}
					}
				}
			}
		}

		return chunk;
	}

	public static Chunk createChunk(World world, ChunkSegmentMask[] masks, IChunkMaskTransformer maskTransformer, int chunkX, int chunkZ)
	{
		Chunk chunk = new Chunk(world, chunkX, chunkZ);

		ChunkHelper.fillChunkFromMaskSegments(chunk, masks, maskTransformer);

		return chunk;
	}
}
