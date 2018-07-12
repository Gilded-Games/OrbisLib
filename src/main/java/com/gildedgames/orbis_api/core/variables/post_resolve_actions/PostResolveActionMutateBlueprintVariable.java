package com.gildedgames.orbis_api.core.variables.post_resolve_actions;

import com.gildedgames.orbis_api.client.gui.data.DropdownElementWithData;
import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.tree.NodeTree;
import com.gildedgames.orbis_api.core.variables.GuiVarBlueprintVariable;
import com.gildedgames.orbis_api.core.variables.GuiVarDropdown;
import com.gildedgames.orbis_api.core.variables.IGuiVar;
import com.gildedgames.orbis_api.core.variables.IGuiVarMutateExpression;
import com.gildedgames.orbis_api.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis_api.data.IDataUser;
import com.gildedgames.orbis_api.data.blueprint.BlueprintVariable;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PostResolveActionMutateBlueprintVariable implements IPostResolveAction, IDataUser<NodeTree<BlueprintVariable, NBT>>
{
	private List<IGuiVar> variables = Lists.newArrayList();

	private GuiVarBlueprintVariable blueprintVariable;

	private GuiVarDropdown<IGuiVarMutateExpression<?>> mutateDropdown;

	private Pos2D guiPos = Pos2D.ORIGIN;

	private GuiVarDisplay parentDisplay;

	private Consumer<IGuiVarMutateExpression<?>> onSetCompareDropdown = (e) ->
	{
		this.variables.clear();

		this.variables.add(this.blueprintVariable);
		this.variables.add(this.mutateDropdown);

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
			this.mutateDropdown = null;

			this.variables.clear();

			this.variables.add(this.blueprintVariable);

			if (this.parentDisplay != null)
			{
				this.parentDisplay.refresh();
			}
		}
		else
		{
			this.mutateDropdown = new GuiVarDropdown<>("orbis.gui.mutate", this.createMutateDropdownStringElements(b),
					this.createMutateDropdownDataFactory(b));

			this.mutateDropdown.setOnSetData(this.onSetCompareDropdown);

			this.variables.clear();

			this.variables.add(this.blueprintVariable);
			this.variables.add(this.mutateDropdown);

			if (this.parentDisplay != null)
			{
				this.parentDisplay.refresh();
			}
		}
	};

	public PostResolveActionMutateBlueprintVariable()
	{
		this.blueprintVariable = new GuiVarBlueprintVariable("orbis.gui.selected_variable", this.onSetBlueprintVariable);

		this.variables.add(this.blueprintVariable);
	}

	private List<String> createMutateDropdownStringElements(BlueprintVariable<?> variable)
	{
		List<String> mutateElements = Lists.newArrayList();

		for (DropdownElementWithData<Supplier<IGuiVarMutateExpression>> expression : variable.getVar().getMutateExpressions())
		{
			mutateElements.add(expression.text().getUnformattedComponentText());
		}

		return mutateElements;
	}

	private Function<String, IGuiVarMutateExpression<?>> createMutateDropdownDataFactory(BlueprintVariable<?> variable)
	{
		return (s) ->
		{
			for (DropdownElementWithData<Supplier<IGuiVarMutateExpression>> expression : variable.getVar().getMutateExpressions())
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
	public String getName()
	{
		return "orbis.gui.mutate_blueprint_variable";
	}

	@Override
	public List<IGuiVar> getVariables()
	{
		return this.variables;
	}

	@Override
	public void resolve(Random rand)
	{
		ResolveAction action = new ResolveAction(this.blueprintVariable.getData().getData(), this.mutateDropdown.getData());

		action.resolve(rand);
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

		funnel.set("blueprintVariable", this.blueprintVariable);
		funnel.set("mutateDropdown", this.mutateDropdown);
		funnel.set("guiPos", this.guiPos, NBTFunnel.POS2D_SETTER);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.blueprintVariable = funnel.get("blueprintVariable");
		this.mutateDropdown = funnel.get("mutateDropdown");
		this.guiPos = funnel.getWithDefault("guiPos", NBTFunnel.POS2D_GETTER, () -> this.guiPos);

		this.blueprintVariable.setOnSetData(this.onSetBlueprintVariable);

		if (this.mutateDropdown != null)
		{
			this.mutateDropdown.setOnSetData(this.onSetCompareDropdown);
		}

		this.variables.clear();

		this.variables.add(this.blueprintVariable);
		this.variables.add(this.mutateDropdown);
	}

	@Override
	public void setParentDisplay(GuiVarDisplay parentDisplay)
	{
		this.parentDisplay = parentDisplay;
	}

	@Override
	public String getDataIdentifier()
	{
		return "blueprintVariables";
	}

	@Override
	public void setUsedData(NodeTree<BlueprintVariable, NBT> data)
	{
		this.blueprintVariable.setUsedData(data);

		if (this.blueprintVariable.getData() != null)
		{
			BlueprintVariable b = this.blueprintVariable.getData().getData();

			if (this.mutateDropdown != null)
			{
				this.mutateDropdown.setStringElements(this.createMutateDropdownStringElements(b));
				this.mutateDropdown.setStringToDataFactory(this.createMutateDropdownDataFactory(b));

				if (this.mutateDropdown.getData() != null)
				{
					for (IGuiVar var : this.mutateDropdown.getData().getInputs())
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

	private static class ResolveAction<DATA>
	{
		private BlueprintVariable<DATA> var;

		private IGuiVarMutateExpression<DATA> expression;

		public ResolveAction(BlueprintVariable<DATA> var, IGuiVarMutateExpression<DATA> expression)
		{
			this.var = var;
			this.expression = expression;
		}

		public void resolve(Random rand)
		{
			this.var.getVar().setData(this.expression.mutate(this.var.getVar().getData()));
		}
	}
}
