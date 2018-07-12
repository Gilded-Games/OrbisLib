package com.gildedgames.orbis_api.block;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.util.mc.BlockUtil;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class BlockFilterHelper
{
	private static List<IBlockRecognition> blockRecognitions = Lists.newArrayList();

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

	public static List<BlockDataWithConditions> convertToBlockData(IBlockState[] states)
	{
		if (states == null)
		{
			return Collections.emptyList();
		}

		List<BlockDataWithConditions> blockData = Lists.newArrayList();

		for (IBlockState state : states)
		{
			blockData.add(new BlockDataWithConditions(state, 1.0F));
		}

		return blockData;
	}

	public static IBlockState[] getBlocksFromStack(final ItemStack stack)
	{
		IBlockState[] blocks = null;

		if (stack.getItem() == Items.STRING)
		{
			blocks = new IBlockState[1];

			blocks[0] = Blocks.AIR.getDefaultState();
		}
		else if (stack.getItem() instanceof ItemBlock || stack.getItem() instanceof ItemMultiTexture)
		{
			final IBlockState state = BlockUtil.getBlockState(stack);

			if (state != null)
			{
				blocks = new IBlockState[1];

				blocks[0] = state;
			}
		}
		else if (stack.getItem() == Items.LAVA_BUCKET)
		{
			blocks = new IBlockState[2];

			blocks[0] = Blocks.LAVA.getDefaultState();
			blocks[1] = Blocks.FLOWING_LAVA.getDefaultState();
		}
		else if (stack.getItem() == Items.WATER_BUCKET)
		{
			blocks = new IBlockState[2];

			blocks[0] = Blocks.WATER.getDefaultState();
			blocks[1] = Blocks.FLOWING_WATER.getDefaultState();
		}

		for (IBlockRecognition recognition : blockRecognitions)
		{
			IBlockState[] found = recognition.recognize(stack);

			if (found != null)
			{
				return found;
			}
		}

		return blocks;
	}

	public static BlockFilterLayer getNewDeleteLayer(final ItemStack stack)
	{
		if (!(ItemBlock.class.isAssignableFrom(stack.getItem().getClass())) && !(stack.getItem() instanceof ItemBucket) && !(ItemMultiTexture.class
				.isAssignableFrom(stack.getItem().getClass())))
		{
			final BlockFilterLayer layer = new BlockFilterLayer();

			layer.setFilterType(BlockFilterType.ALL);

			layer.getReplacementBlocks().add(new BlockDataWithConditions(Blocks.AIR.getDefaultState(), 1.0f));

			return layer;
		}

		final BlockFilterLayer layer = new BlockFilterLayer();

		layer.setFilterType(BlockFilterType.ONLY);

		final IBlockState[] blocks = getBlocksFromStack(stack);

		for (final IBlockState s : blocks)
		{
			layer.getRequiredBlocks().add(new BlockDataWithConditions(s, 1.0f));
		}

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
		if (!(ItemBlock.class.isAssignableFrom(stack.getItem().getClass())) && !(stack.getItem() instanceof ItemBucket) && !(ItemMultiTexture.class
				.isAssignableFrom(stack.getItem().getClass())))
		{
			throw new NullPointerException("ItemStack given to getNewFillLayer() is not a Block. Aborting." + stack.getItem());
		}

		final BlockFilterLayer layer = new BlockFilterLayer();

		layer.setFilterType(BlockFilterType.ALL);

		final IBlockState[] blocks = getBlocksFromStack(stack);

		for (final IBlockState s : blocks)
		{
			layer.getReplacementBlocks().add(new BlockDataWithConditions(s, 1.0f));
		}

		return layer;
	}

	public static BlockFilterLayer getNewReplaceLayer(final ItemStack mainHand, final ItemStack offHand)
	{
		if (!(mainHand.getItem() instanceof ItemBlock) && !(mainHand.getItem() instanceof ItemBucket)
				&& !(offHand.getItem() instanceof ItemBlock) && !(offHand.getItem() instanceof ItemBucket))
		{
			throw new NullPointerException("ItemStack given to getNewFillLayer() is not a Block. Aborting.");
		}
		final BlockFilterLayer layer = new BlockFilterLayer();

		final IBlockState[] mainStates = getBlocksFromStack(mainHand);
		final IBlockState[] offStates = getBlocksFromStack(offHand);

		layer.setFilterType(BlockFilterType.ALL_EXCEPT);

		if (offStates == null)
		{
			for (final IBlockState s : mainStates)
			{
				layer.getReplacementBlocks().add(new BlockDataWithConditions(s, 1.0f));
			}

			layer.getRequiredBlocks().add(new BlockDataWithConditions(Blocks.AIR.getDefaultState(), 1.0f));
		}
		else
		{
			layer.setFilterType(BlockFilterType.ONLY);

			for (final IBlockState s : mainStates)
			{
				layer.getReplacementBlocks().add(new BlockDataWithConditions(s, 1.0f));
			}

			for (final IBlockState s : offStates)
			{
				layer.getRequiredBlocks().add(new BlockDataWithConditions(s, 1.0f));
			}
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
		IBlockState[] recognize(ItemStack stack);
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
