package com.gildedgames.orbis.lib.core;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class GameRegistrar
{
	private final IForgeRegistry<Block> blockRegistry;

	private final IForgeRegistry<Item> itemRegistry;

	public GameRegistrar()
	{
		this.blockRegistry = GameRegistry.findRegistry(Block.class);
		this.itemRegistry = GameRegistry.findRegistry(Item.class);
	}

	public ResourceLocation getIdentifierFor(final Block block)
	{
		return this.blockRegistry.getKey(block);
	}

	public ResourceLocation getIdentifierFor(final Item item)
	{
		return this.itemRegistry.getKey(item);
	}

	public Block findBlock(final ResourceLocation identifier)
	{
		final String modId = identifier.getNamespace();
		final String name = identifier.getPath();

		if (modId.equals("minecraft") || modId.equals(""))
		{
			return Block.getBlockFromName(name);
		}

		return this.blockRegistry.getValue(identifier);
	}

	public Item findItem(final ResourceLocation identifier)
	{
		final String modId = identifier.getNamespace();

		if (modId.equals("minecraft") || modId.equals(""))
		{
			return Item.getByNameOrId(identifier.toString());
		}

		return this.itemRegistry.getValue(identifier);
	}

	public int getBlockId(final Block block)
	{
		return Block.getIdFromBlock(block);
	}

	public IBlockState getStateFromMeta(final Block block, final int meta)
	{
		return block.getStateFromMeta(meta);
	}

	public IBlockState getStateFromId(final int id)
	{
		return Block.getStateById(id);
	}

	public int getStateId(final IBlockState blockState)
	{
		return Block.getStateId(blockState);
	}

}