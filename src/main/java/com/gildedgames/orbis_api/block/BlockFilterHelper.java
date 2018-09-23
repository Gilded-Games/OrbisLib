package com.gildedgames.orbis_api.block;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.util.mc.BlockUtil;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;

import java.util.Collections;
import java.util.List;

public class BlockFilterHelper
{
	private static List<IBlockRecognition> blockRecognitions = Lists.newArrayList();

	static
	{
		blockRecognitions.add(new IBlockRecognition()
		{
			@Override
			public List<BlockDataWithConditions> recognize(ItemStack stack)
			{
				List<BlockDataWithConditions> blocks = Lists.newArrayList();

				if (stack.getItem() == Items.STRING)
				{
					blocks.add(new BlockDataWithConditions(Blocks.AIR.getDefaultState(), stack.getCount()));
				}
				else if (stack.getItem() instanceof ItemBlock || stack.getItem() instanceof ItemMultiTexture)
				{
					final IBlockState state = BlockUtil.getBlockState(stack);

					if (state != null)
					{
						blocks.add(new BlockDataWithConditions(state, stack.getCount()));
					}
				}
				else if (stack.getItem() == Items.LAVA_BUCKET)
				{
					blocks.add(new BlockDataWithConditions(Blocks.LAVA.getDefaultState(), stack.getCount()));
					blocks.add(new BlockDataWithConditions(Blocks.FLOWING_LAVA.getDefaultState(), stack.getCount()));
				}
				else if (stack.getItem() == Items.WATER_BUCKET)
				{
					blocks.add(new BlockDataWithConditions(Blocks.WATER.getDefaultState(), stack.getCount()));
					blocks.add(new BlockDataWithConditions(Blocks.FLOWING_WATER.getDefaultState(), stack.getCount()));
				}

				return blocks;
			}

			@Override
			public boolean isCompatible(Class<? extends Item> clazz)
			{
				return !(ItemBlock.class.isAssignableFrom(clazz)) && !(ItemBucket.class.isAssignableFrom(clazz)) && !(ItemMultiTexture.class
						.isAssignableFrom(clazz));
			}
		});
	}

	public static void registerBlockRecognition(IBlockRecognition blockRecognition)
	{
		if (!blockRecognitions.contains(blockRecognition))
		{
			blockRecognitions.add(blockRecognition);
		}
		else
		{
			OrbisAPI.LOGGER
					.info("WARNING: A mod is trying to register a particular block recognition implementation more than once. Something is wrong. Please notify the mod author.");
		}
	}

	public static List<BlockDataWithConditions> getBlocksFromStack(final ItemStack stack)
	{
		for (IBlockRecognition recognition : blockRecognitions)
		{
			List<BlockDataWithConditions> found = recognition.recognize(stack);

			if (found != null && !found.isEmpty())
			{
				return found;
			}
		}

		return Collections.emptyList();
	}

	public static BlockFilterLayer getNewDeleteLayer(final ItemStack stack)
	{
		final List<BlockDataWithConditions> blocks = getBlocksFromStack(stack);

		if (blocks.isEmpty())
		{
			final BlockFilterLayer layer = new BlockFilterLayer();

			layer.setFilterType(BlockFilterType.ALL);

			layer.getReplacementBlocks().add(new BlockDataWithConditions(Blocks.AIR.getDefaultState(), 1.0f));

			return layer;
		}

		final BlockFilterLayer layer = new BlockFilterLayer();

		layer.setFilterType(BlockFilterType.ONLY);

		layer.getRequiredBlocks().addAll(blocks);

		layer.getReplacementBlocks().add(new BlockDataWithConditions(Blocks.AIR.getDefaultState(), 1.0f));

		return layer;
	}

	public static BlockFilterLayer getNewFillLayer()
	{
		final BlockFilterLayer layer = new BlockFilterLayer();

		layer.setFilterType(BlockFilterType.ALL);
		layer.getReplacementBlocks().add(new BlockDataWithConditions(Blocks.STONE.getDefaultState(), 1.0f));

		return layer;
	}

	public static BlockFilterLayer createFillLayer(final List<ItemStack> stacks)
	{
		final BlockFilterLayer filterLayer = BlockFilterHelper.getNewFillLayer();

		final List<BlockDataWithConditions> blocks = Lists.newArrayList();

		for (final ItemStack stack : stacks)
		{
			final IBlockState state = BlockUtil.getBlockState(stack);

			final BlockDataWithConditions block = new BlockDataWithConditions(state, stack.getCount());

			if (blocks.contains(block))
			{
				for (final BlockDataWithConditions b : blocks)
				{
					if (block.equals(b))
					{
						b.getReplaceCondition().setWeight(b.getReplaceCondition().getWeight() + stack.getCount());
						break;
					}
				}
			}
			else
			{
				block.getReplaceCondition().setWeight(stack.getCount());

				blocks.add(block);
			}
		}

		filterLayer.setReplacementBlocks(blocks);

		return filterLayer;
	}

	public static BlockFilterLayer createFill(final List<BlockDataWithConditions> blocks)
	{
		final BlockFilterLayer filterLayer = BlockFilterHelper.getNewFillLayer();

		filterLayer.setReplacementBlocks(blocks);

		return filterLayer;
	}

	/**
	 * @return The default fill layer
	 */

	public static BlockFilterLayer getNewFillLayer(final ItemStack stack)
	{
		final BlockFilterLayer layer = new BlockFilterLayer();

		layer.setFilterType(BlockFilterType.ALL);

		final List<BlockDataWithConditions> blocks = getBlocksFromStack(stack);

		if (blocks != null)
		{
			layer.getReplacementBlocks().addAll(blocks);
		}

		return layer;
	}

	public static BlockFilterLayer getNewReplaceLayer(final ItemStack mainHand, final ItemStack offHand)
	{
		final BlockFilterLayer layer = new BlockFilterLayer();

		final List<BlockDataWithConditions> mainStates = getBlocksFromStack(mainHand);
		final List<BlockDataWithConditions> offStates = getBlocksFromStack(offHand);

		layer.setFilterType(BlockFilterType.ALL_EXCEPT);

		if (offStates == null)
		{
			if (mainStates != null)
			{
				layer.getReplacementBlocks().addAll(mainStates);
			}

			layer.getRequiredBlocks().add(new BlockDataWithConditions(Blocks.AIR.getDefaultState(), 1.0f));
		}
		else
		{
			layer.setFilterType(BlockFilterType.ONLY);

			if (mainStates != null)
			{
				layer.getReplacementBlocks().addAll(mainStates);
			}

			layer.getRequiredBlocks().addAll(offStates);
		}

		return layer;
	}

	public static BlockFilterLayer getNewVoidLayer()
	{
		final IBlockState state = Blocks.STRUCTURE_VOID.getDefaultState();

		final BlockFilterLayer layer = new BlockFilterLayer();

		layer.setFilterType(BlockFilterType.ONLY);

		layer.getRequiredBlocks().add(new BlockDataWithConditions(Blocks.AIR.getDefaultState(), 1.0f));
		layer.getReplacementBlocks().add(new BlockDataWithConditions(state, 1.0f));

		return layer;
	}

	public interface IBlockRecognition
	{
		List<BlockDataWithConditions> recognize(ItemStack stack);

		boolean isCompatible(Class<? extends Item> clazz);
	}

	/**
	 * The default Delete layer
	 *
	 */
	public static class BlockDeleteFilter extends BlockFilter
	{
		public BlockDeleteFilter()
		{
			final BlockFilterLayer layer = new BlockFilterLayer();

			layer.setFilterType(BlockFilterType.ALL);
			layer.getReplacementBlocks().add(new BlockDataWithConditions(Blocks.AIR.getDefaultState(), 1.0f));
			this.add(layer);
		}
	}

}
