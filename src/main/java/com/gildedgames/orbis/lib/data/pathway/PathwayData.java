package com.gildedgames.orbis.lib.data.pathway;

import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.util.mc.NBT;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class PathwayData implements NBT
{

	private List<BlueprintData> pieces = new ArrayList<>();

	public PathwayData()
	{
		super();
	}

	public PathwayData(List<BlueprintData> pieces)
	{
		super();

		this.pieces = new ArrayList<>(pieces);

		//this.pieces.removeIf(b -> b.getEntrance().size() < 2); TODO: Entrances
	}

	public void addPiece(BlueprintData piece)
	{
		//		if (piece.getEntrance().size() < 2) TODO: Entrances
		//		{
		//			throw new IllegalStateException("You can only add blueprints with at least two entrances to a pathway");
		//		}
		this.pieces.add(piece);
	}

	public List<BlueprintData> pieces()
	{
		return this.pieces;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);
		funnel.setList("blueprints", this.pieces);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);
		this.pieces = funnel.getList("blueprints");
	}

	//TODO: Properly compute a logical tolerance dist;
	public int getToleranceDist()
	{
		return this.pieces.get(0).getWidth();
	}
}
