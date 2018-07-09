package com.gildedgames.orbis_api.core.variables;

import com.gildedgames.orbis_api.client.gui.data.DropdownElement;
import com.gildedgames.orbis_api.client.gui.data.DropdownElementWithData;
import com.gildedgames.orbis_api.client.gui.data.IDropdownElement;
import com.gildedgames.orbis_api.client.gui.util.GuiDropdown;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.gildedgames.orbis_api.util.mc.NBT;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GuiVarDropdown<DATA extends NBT> implements IGuiVar<DATA, GuiDropdown<IDropdownElement>>
{
	private GuiVarDisplay parentDisplay;

	private String name;

	private DATA data;

	private int dataIndex;

	private List<String> elements;

	private Function<String, DATA> stringToDataFactory;

	private Consumer<DATA> onSetData;

	private GuiVarDropdown()
	{

	}

	public GuiVarDropdown(String name, List<String> elements, Function<String, DATA> stringToDataFactory)
	{
		this.name = name;
		this.elements = elements;
		this.stringToDataFactory = stringToDataFactory;
	}

	public void setStringElements(List<String> elements)
	{
		this.elements = elements;
	}

	public void setStringToDataFactory(Function<String, DATA> stringToDataFactory)
	{
		this.stringToDataFactory = stringToDataFactory;
	}

	public void setOnSetData(Consumer<DATA> onSetData)
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
		return "orbis.gui.dropdown";
	}

	@Override
	public DATA getData()
	{
		return this.data;
	}

	@Override
	public void setData(DATA data)
	{
		this.data = data;

		this.onSetData.accept(data);
	}

	@Override
	public GuiDropdown<IDropdownElement> createDisplay(int maxWidth)
	{
		List<IDropdownElement> elements = Lists.newArrayList();

		this.elements.forEach((elementName) ->
		{
			IDropdownElement element = new DropdownElement(new TextComponentTranslation(elementName));

			elements.add(element);
		});

		GuiDropdown<IDropdownElement> dropdown = new GuiDropdown<>(Dim2D.build().width(maxWidth).flush(),
				(e) ->
				{
					DATA data = this.stringToDataFactory.apply(e.text().getUnformattedComponentText());

					GuiVarDropdown.this.setData(data);

					this.dataIndex = this.elements.indexOf(e.text().getUnformattedComponentText());
				});

		dropdown.getList().setDropdownElements(elements);

		if (elements.size() > this.dataIndex)
		{
			IDropdownElement element = elements.get(this.dataIndex);

			dropdown.setChosenElement(element);

			if (this.data == null)
			{
				DATA data = this.stringToDataFactory.apply(element.text().getUnformattedComponentText());

				this.setData(data);
			}
		}

		return dropdown;
	}

	@Override
	public void updateDataFromDisplay(GuiDropdown<IDropdownElement> dropdown)
	{
		/*if (dropdown.getChosenElement() != null)
		{
			DATA data = this.stringToDataFactory.apply(dropdown.getChosenElement().text().getUnformattedComponentText());

			this.setData(data);

			this.dataIndex = this.elements.indexOf(dropdown.getChosenElement().text().getUnformattedComponentText());
		}*/
	}

	@Override
	public void resetDisplayFromData(GuiDropdown<IDropdownElement> dropdown)
	{
		IDropdownElement element = null;

		for (IDropdownElement e : dropdown.getList().getElements())
		{
			if (e.text().getUnformattedComponentText().equals(this.elements.get(this.dataIndex)))
			{
				element = e;
				break;
			}
		}

		dropdown.setChosenElement(element);
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
		NBTFunnel funnel = new NBTFunnel(tag);

		tag.setString("name", this.name);
		tag.setInteger("dataIndex", this.dataIndex);
		funnel.set("data", this.data);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.name = tag.getString("name");
		this.dataIndex = tag.getInteger("dataIndex");
		this.data = funnel.get("data");
	}
}
