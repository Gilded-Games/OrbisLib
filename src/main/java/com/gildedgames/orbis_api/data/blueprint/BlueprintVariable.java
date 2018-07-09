package com.gildedgames.orbis_api.data.blueprint;

import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.variables.GuiVarString;
import com.gildedgames.orbis_api.core.variables.IGuiVar;
import com.gildedgames.orbis_api.core.variables.IGuiVarDisplayContents;
import com.gildedgames.orbis_api.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.gildedgames.orbis_api.world.IWorldObject;
import com.gildedgames.orbis_api.world.IWorldObjectChild;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class BlueprintVariable<DATA> implements NBT, IWorldObjectChild, IGuiVarDisplayContents
{
	private IGuiVar<DATA, ?> variable;

	private GuiVarString name;

	private Pos2D pos = Pos2D.ORIGIN;

	private List<IGuiVar> variables = Lists.newArrayList();

	private IWorldObject worldObjectParent;

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

		if (this.worldObjectParent != null)
		{
			this.worldObjectParent.markDirty();
		}
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("variable", this.variable);
		funnel.set("name", this.name);
		funnel.set("pos", this.pos, NBTFunnel.POS2D_SETTER);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.variable = funnel.get("variable");
		this.name = funnel.get("name");
		this.pos = funnel.getWithDefault("pos", NBTFunnel.POS2D_GETTER, () -> this.pos);

		this.variables.add(this.name);
		this.variables.add(this.variable);
	}

	@Override
	public IWorldObject getWorldObjectParent()
	{
		return this.worldObjectParent;
	}

	@Override
	public void setWorldObjectParent(IWorldObject parent)
	{
		this.worldObjectParent = parent;
	}

	@Override
	public void setParentDisplay(GuiVarDisplay parentDisplay)
	{

	}
}
