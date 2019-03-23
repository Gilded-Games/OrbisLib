package com.gildedgames.orbis.lib.core.variables;

import com.gildedgames.orbis.lib.client.gui.data.DropdownElementWithData;
import com.gildedgames.orbis.lib.client.gui.util.GuiDropdown;
import com.gildedgames.orbis.lib.client.rect.Dim2D;
import com.gildedgames.orbis.lib.core.tree.INode;
import com.gildedgames.orbis.lib.core.tree.INodeTreeListener;
import com.gildedgames.orbis.lib.core.tree.NodeTree;
import com.gildedgames.orbis.lib.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis.lib.data.IDataUser;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintVariable;
import com.gildedgames.orbis.lib.util.mc.NBT;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiVarBlueprintVariable implements IGuiVar<INode<BlueprintVariable, NBT>, GuiDropdown<DropdownElementWithData<INode<BlueprintVariable, NBT>>>>,
		IDataUser<NodeTree<BlueprintVariable, NBT>>, INodeTreeListener<BlueprintVariable, NBT>
{
	private GuiVarDisplay parentDisplay;

	private NodeTree<BlueprintVariable, NBT> blueprintVariables;

	private String name;

	private INode<BlueprintVariable, NBT> data;

	private int dataIndex;

	private Consumer<BlueprintVariable<?>> onSetData;

	private GuiVarBlueprintVariable()
	{

	}

	public GuiVarBlueprintVariable(String name, Consumer<BlueprintVariable<?>> onSetData)
	{
		this.name = name;
		this.onSetData = onSetData;
	}

	public void setOnSetData(Consumer<BlueprintVariable<?>> onSetData)
	{
		this.onSetData = onSetData;
	}

	@Override
	public void setParentDisplay(GuiVarDisplay parentDisplay)
	{
		this.parentDisplay = parentDisplay;
	}

	@Override
	public String getVariableName()
	{
		return this.name;
	}

	@Override
	public String getDataName()
	{
		return "orbis.gui.blueprint_variable";
	}

	@Override
	public INode<BlueprintVariable, NBT> getData()
	{
		return this.data;
	}

	@Override
	public void setData(INode<BlueprintVariable, NBT> data)
	{
		this.data = data;

		if (this.blueprintVariables != null)
		{
			this.dataIndex = this.blueprintVariables.get(this.data);
		}
	}

	@Override
	public GuiDropdown<DropdownElementWithData<INode<BlueprintVariable, NBT>>> createDisplay(int maxWidth)
	{
		List<DropdownElementWithData<INode<BlueprintVariable, NBT>>> elements = Lists.newArrayList();

		if (this.blueprintVariables != null)
		{
			this.blueprintVariables.getNodes().forEach((n) ->
			{
				DropdownElementWithData<INode<BlueprintVariable, NBT>> element = new DropdownElementWithData<>(
						new TextComponentTranslation(n.getData().getUniqueNameVar().getData()), n);

				elements.add(element);
			});
		}

		GuiDropdown<DropdownElementWithData<INode<BlueprintVariable, NBT>>> dropdown = new GuiDropdown<>(Dim2D.build().width(maxWidth).flush(),
				(e) ->
				{
					this.setData(e.getData());

					if (this.onSetData != null)
					{
						this.onSetData.accept(this.data.getData());
					}
				});

		dropdown.getList().setDropdownElements(elements);

		if (this.blueprintVariables != null && elements.size() > this.dataIndex)
		{
			DropdownElementWithData<INode<BlueprintVariable, NBT>> element = elements.get(this.dataIndex);

			dropdown.setChosenElement(element);

			if (this.data == null)
			{
				this.setData(element.getData());

				if (this.onSetData != null)
				{
					this.onSetData.accept(this.data.getData());
				}
			}
		}

		return dropdown;
	}

	@Override
	public void updateDataFromDisplay(GuiDropdown<DropdownElementWithData<INode<BlueprintVariable, NBT>>> blueprintVariableGuiDropdown)
	{
		if (blueprintVariableGuiDropdown.getChosenElement() != null && blueprintVariableGuiDropdown.getChosenElement().getData() != null)
		{
			this.setData(blueprintVariableGuiDropdown.getChosenElement().getData());
		}
	}

	@Override
	public void resetDisplayFromData(GuiDropdown<DropdownElementWithData<INode<BlueprintVariable, NBT>>> blueprintVariableGuiDropdown)
	{
		DropdownElementWithData<INode<BlueprintVariable, NBT>> element = null;

		for (DropdownElementWithData<INode<BlueprintVariable, NBT>> e : blueprintVariableGuiDropdown.getList().getElements())
		{
			if (e.getData() == this.data)
			{
				element = e;
				break;
			}
		}

		blueprintVariableGuiDropdown.setChosenElement(element);
	}

	@Override
	public List<DropdownElementWithData<Supplier<IGuiVarCompareExpression>>> getCompareExpressions()
	{
		return Collections.emptyList();
	}

	@Override
	public List<DropdownElementWithData<Supplier<IGuiVarMutateExpression>>> getMutateExpressions()
	{
		return Collections.emptyList();
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		tag.putString("name", this.name);
		tag.putInt("dataIndex", this.dataIndex);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		this.name = tag.getString("name");
		this.dataIndex = tag.getInt("dataIndex");
	}

	@Override
	public void onSetData(INode<BlueprintVariable, NBT> node, BlueprintVariable variable, int id)
	{
		if (id == this.dataIndex)
		{
			this.setData(node);

			if (this.onSetData != null)
			{
				this.onSetData.accept(this.data.getData());
			}
		}
	}

	@Override
	public void onPut(INode<BlueprintVariable, NBT> node, int id)
	{
		if (id == this.dataIndex)
		{
			this.setData(node);

			if (this.onSetData != null)
			{
				this.onSetData.accept(this.data.getData());
			}
		}
	}

	@Override
	public void onRemove(INode<BlueprintVariable, NBT> node, int id)
	{
		if (id == this.dataIndex)
		{
			this.setData(null);

			if (this.onSetData != null)
			{
				this.onSetData.accept(null);
			}
		}
	}

	@Override
	public String getDataIdentifier()
	{
		return "blueprintVariables";
	}

	@Override
	public void setUsedData(NodeTree<BlueprintVariable, NBT> data)
	{
		this.blueprintVariables = data;

		this.data = this.blueprintVariables.get(this.dataIndex);

		this.blueprintVariables.listen(this);
	}
}
