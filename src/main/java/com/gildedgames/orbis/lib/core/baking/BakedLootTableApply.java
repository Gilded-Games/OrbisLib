package com.gildedgames.orbis.lib.core.baking;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.data.management.IDataIdentifier;
import com.gildedgames.orbis.lib.processing.DataPrimer;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

import java.util.Random;

public class BakedLootTableApply implements IBakedPosAction
{
	private IDataIdentifier lootTable;

	private BlockPos pos;

	private long lootTableSeed;

	private BakedLootTableApply()
	{

	}

	public BakedLootTableApply(IDataIdentifier lootTable, long lootTableSeed, BlockPos pos)
	{
		this.lootTable = lootTable;
		this.lootTableSeed = lootTableSeed;
		this.pos = pos;
	}

	@Override
	public BlockPos getPos()
	{
		return this.pos;
	}

	@Override
	public void setPos(BlockPos pos)
	{
		this.pos = pos;
	}

	@Override
	public void call(DataPrimer primer)
	{
		TileEntity te = primer.getAccess().getTileEntity(this.pos);

		if (te instanceof IInventory && primer.getWorld() instanceof WorldServer)
		{
			IInventory inventory = (IInventory) te;

			LootTable loot = OrbisLib.services().lootTableCache().getLootTableFromLocation(this.lootTable);

			Random random;

			if (this.lootTableSeed == 0L)
			{
				random = new Random();
			}
			else
			{
				random = new Random(this.lootTableSeed);
			}

			LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) primer.getWorld());

			loot.fillInventory(inventory, random, lootcontext$builder.build());
		}
	}

	@Override
	public IBakedPosAction copy()
	{
		BakedLootTableApply action = new BakedLootTableApply();
		action.lootTable = this.lootTable;
		action.lootTableSeed = this.lootTableSeed;
		action.pos = this.pos;

		return action;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("lootTable", this.lootTable);
		tag.putLong("lootTableSeed", this.lootTableSeed);
		funnel.setPos("pos", this.pos);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.lootTable = funnel.get("lootTable");
		this.lootTableSeed = tag.getLong("lootTableSeed");
		this.pos = funnel.getPos("pos");
	}
}
