package com.gildedgames.orbis.lib.data.blueprint;

import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.core.variables.GuiVarString;
import com.gildedgames.orbis.lib.core.variables.IGuiVar;
import com.gildedgames.orbis.lib.core.variables.IGuiVarDisplayContents;
import com.gildedgames.orbis.lib.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis.lib.data.IDataChild;
import com.gildedgames.orbis.lib.util.io.NBTFunnel;
import com.gildedgames.orbis.lib.util.mc.NBT;
import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundNBT;

import java.util.List;

public class BlueprintVariable<DATA> implements NBT, IGuiVarDisplayContents, IDataChild<BlueprintData>
{
	private IGuiVar<DATA, ?> variable;

	private GuiVarString name;

	private Pos2D pos = Pos2D.ORIGIN;

	private List<IGuiVar> variables = Lists.newArrayList();

	private BlueprintData dataParent;

	private BlueprintVariable()
	{

	}

	public BlueprintVariable(IGuiVar<DATA, ?> variable, String uniqueName)
	{
		this.variable = variable;
		this.name = new GuiVarString("Unique Name");

		this.name.setData(uniqueName);

		this.variables.add(this.name);
		this.variables.add(this.variable);
	}

	public GuiVarString getUniqueNameVar()
	{
		return this.name;
	}

	public IGuiVar<DATA, ?> getVar()
	{
		return this.variable;
	}

	@Override
	public List<IGuiVar> getVariables()
	{
		return this.variables;
	}

	public Pos2D getGuiPos()
	{
		return this.pos;
	}

	public void setGuiPos(Pos2D pos)
	{
		this.pos = pos;

		if (this.dataParent != null)
		{
			this.dataParent.markDirty();
		}
	}

	@Override
	public void write(CompoundNBT tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("variable", this.variable);
		funnel.set("name", this.name);
		funnel.set("pos", this.pos, NBTFunnel.POS2D_SETTER);
	}

	@Override
	public void read(CompoundNBT tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.variable = funnel.get("variable");
		this.name = funnel.get("name");
		this.pos = funnel.getWithDefault("pos", NBTFunnel.POS2D_GETTER, () -> this.pos);

		this.variables.add(this.name);
		this.variables.add(this.variable);
	}

	@Override
	public void setParentDisplay(GuiVarDisplay parentDisplay)
	{

	}

	@Override
	public Class<? extends BlueprintData> getDataClass()
	{
		return BlueprintData.class;
	}

	@Override
	public BlueprintData getDataParent()
	{
		return this.dataParent;
	}

	@Override
	public void setDataParent(BlueprintData blueprintData)
	{
		this.dataParent = this.dataParent;
	}
}
