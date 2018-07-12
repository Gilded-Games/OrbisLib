package com.gildedgames.orbis_api.core.variables.conditions;

import com.gildedgames.orbis_api.client.gui.data.DropdownElementWithData;
import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.variables.GuiVarBlueprintVariable;
import com.gildedgames.orbis_api.core.variables.GuiVarDropdown;
import com.gildedgames.orbis_api.core.variables.IGuiVar;
import com.gildedgames.orbis_api.core.variables.IGuiVarCompareExpression;
import com.gildedgames.orbis_api.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis_api.data.IDataChild;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;
import com.gildedgames.orbis_api.data.blueprint.BlueprintVariable;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GuiConditionCheckBlueprintVariable implements IGuiCondition, IDataChild<BlueprintData>
{
	private List<IGuiVar> variables = Lists.newArrayList();

	private GuiVarBlueprintVariable guiVarBlueprintVariable;

	private GuiVarDropdown<IGuiVarCompareExpression<?>> compareDropdown;

	private Pos2D guiPos = Pos2D.ORIGIN;

	private GuiVarDisplay parentDisplay;

	private BlueprintData dataParent;

	private Consumer<IGuiVarCompareExpression<?>> onSetCompareDropdown = (e) ->
	{
		this.variables.clear();

		this.variables.add(this.guiVarBlueprintVariable);
		this.variables.add(this.compareDropdown);

		for (IGuiVar var : e.getInputs())
		{
			this.variables.add(var);
		}

		this.parentDisplay.refresh();
	};

	private Consumer<BlueprintVariable<?>> onSetBlueprintVariable = (b) ->
	{
		if (b == null)
		{
			this.compareDropdown = null;

			this.variables.clear();

			this.variables.add(this.guiVarBlueprintVariable);

			if (this.parentDisplay != null)
			{
				this.parentDisplay.refresh();
			}
		}
		else
		{
			this.compareDropdown = new GuiVarDropdown<>("orbis.gui.compare", this.createCompareDropdownStringElements(b),
					this.createCompareDropdownDataFactory(b));

			this.compareDropdown.setOnSetData(this.onSetCompareDropdown);

			this.variables.clear();

			this.variables.add(this.guiVarBlueprintVariable);
			this.variables.add(this.compareDropdown);

			if (this.parentDisplay != null)
			{
				this.parentDisplay.refresh();
			}
		}
	};

	public GuiConditionCheckBlueprintVariable()
	{
		this.guiVarBlueprintVariable = new GuiVarBlueprintVariable("orbis.gui.selected_variable", this.onSetBlueprintVariable);

		this.variables.add(this.guiVarBlueprintVariable);
	}

	private List<String> createCompareDropdownStringElements(BlueprintVariable<?> variable)
	{
		List<String> compareElements = Lists.newArrayList();

		for (DropdownElementWithData<Supplier<IGuiVarCompareExpression>> expression : variable.getVar().getCompareExpressions())
		{
			compareElements.add(expression.text().getUnformattedComponentText());
		}

		return compareElements;
	}

	private Function<String, IGuiVarCompareExpression<?>> createCompareDropdownDataFactory(BlueprintVariable<?> variable)
	{
		return (s) ->
		{
			for (DropdownElementWithData<Supplier<IGuiVarCompareExpression>> expression : variable.getVar().getCompareExpressions())
			{
				if (expression.text().getUnformattedComponentText().equals(s))
				{
					return expression.getData().get();
				}
			}

			return null;
		};
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
		this.dataParent = blueprintData;

		this.guiVarBlueprintVariable.setDataParent(blueprintData);

		if (this.guiVarBlueprintVariable.getData() != null)
		{
			BlueprintVariable b = this.guiVarBlueprintVariable.getData().getData();

			if (this.compareDropdown != null)
			{
				this.compareDropdown.setStringElements(this.createCompareDropdownStringElements(b));
				this.compareDropdown.setStringToDataFactory(this.createCompareDropdownDataFactory(b));

				if (this.compareDropdown.getData() != null)
				{
					for (IGuiVar var : this.compareDropdown.getData().getInputs())
					{
						this.variables.add(var);
					}
				}
			}
			else
			{
				this.onSetBlueprintVariable.accept(b);
			}
		}
	}

	@Override
	public String getName()
	{
		return "orbis.gui.check_blueprint_variable";
	}

	@Override
	public List<IGuiVar> getVariables()
	{
		return this.variables;
	}

	@Override
	public boolean resolve(Random rand)
	{
		return this.compareDropdown.getData().compare(this.guiVarBlueprintVariable.getData().getData().getVar().getData());
	}

	@Override
	public Pos2D getGuiPos()
	{
		return this.guiPos;
	}

	@Override
	public void setGuiPos(Pos2D pos)
	{
		this.guiPos = pos;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("guiVarBlueprintVariable", this.guiVarBlueprintVariable);
		funnel.set("compareDropdown", this.compareDropdown);
		funnel.set("guiPos", this.guiPos, NBTFunnel.POS2D_SETTER);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.guiVarBlueprintVariable = funnel.get("guiVarBlueprintVariable");
		this.compareDropdown = funnel.get("compareDropdown");
		this.guiPos = funnel.getWithDefault("guiPos", NBTFunnel.POS2D_GETTER, () -> this.guiPos);

		this.guiVarBlueprintVariable.setOnSetData(this.onSetBlueprintVariable);

		if (this.compareDropdown != null)
		{
			this.compareDropdown.setOnSetData(this.onSetCompareDropdown);
		}

		this.variables.clear();

		this.variables.add(this.guiVarBlueprintVariable);
		this.variables.add(this.compareDropdown);
	}

	@Override
	public void setParentDisplay(GuiVarDisplay parentDisplay)
	{
		this.parentDisplay = parentDisplay;
	}
}
