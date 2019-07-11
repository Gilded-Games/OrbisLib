package com.gildedgames.orbis.lib.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
		return this.blockRegistry.getValue(identifier);
	}

	public Item findItem(final ResourceLocation identifier)
	{
		return this.itemRegistry.getValue(identifier);
	}

	public int getStateId(final BlockState blockState)
	{
		return Block.getStateId(blockState);
	}

}