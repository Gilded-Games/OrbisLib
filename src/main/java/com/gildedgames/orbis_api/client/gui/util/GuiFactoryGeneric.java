package com.gildedgames.orbis_api.client.gui.util;

import com.gildedgames.orbis_api.OrbisAPI;
import com.gildedgames.orbis_api.client.gui.data.DropdownElement;
import com.gildedgames.orbis_api.client.gui.data.IDropdownElement;
import com.gildedgames.orbis_api.client.gui.data.directory.IDirectoryNavigator;
import com.gildedgames.orbis_api.client.rect.Dim2D;
import com.gildedgames.orbis_api.client.rect.Rect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.io.File;
import java.util.Collections;

public class GuiFactoryGeneric
{

	public static final ResourceLocation DELETE = OrbisAPI.getResource("list/delete.png");

	public static final ResourceLocation DELETE_CLICKED = OrbisAPI.getResource("list/delete_clicked.png");

	public static final ResourceLocation DELETE_DISABLED = OrbisAPI.getResource("list/delete_disabled.png");

	public static final ResourceLocation DELETE_HOVERED = OrbisAPI.getResource("list/delete_hovered.png");

	public static final ResourceLocation ADD = OrbisAPI.getResource("list/add.png");

	public static final ResourceLocation ADD_CLICKED = OrbisAPI.getResource("list/add_clicked.png");

	public static final ResourceLocation ADD_DISABLED = OrbisAPI.getResource("list/add_disabled.png");

	public static final ResourceLocation ADD_HOVERED = OrbisAPI.getResource("list/add_hovered.png");

	private GuiFactoryGeneric()
	{

	}

	public static IDropdownElement createCloseDropdownElement(final File file, final IDirectoryNavigator navigator)
	{
		return new DropdownElement(new TextComponentString("Close"))
		{
			@Override
			public void onClick(final GuiDropdownList list, final EntityPlayer player)
			{
				list.setDropdownElements(Collections.emptyList());
			}
		};
	}

	public static IDropdownElement createDeleteFileDropdownElement(final File file, final IDirectoryNavigator navigator)
	{
		return new DropdownElement(new TextComponentString("Delete"))
		{
			@Override
			public void onClick(final GuiDropdownList list, final EntityPlayer player)
			{
				if (file.isDirectory())
				{
					//FileHelper.deleteDirectory(file);
				}
				else
				{
					//file.delete();
				}

				list.setDropdownElements(Collections.emptyList());
				list.state().setVisible(false);

				navigator.refresh();
			}
		};
	}

	public static GuiAbstractButton createDeleteButton()
	{
		final Rect rect = Dim2D.build().width(20).height(20).flush();

		final GuiTexture defaultState = new GuiTexture(rect, DELETE);
		final GuiTexture hoveredState = new GuiTexture(rect, DELETE_HOVERED);
		final GuiTexture clickedState = new GuiTexture(rect, DELETE_CLICKED);
		final GuiTexture disabledState = new GuiTexture(rect, DELETE_DISABLED);

		final GuiAbstractButton button = new GuiAbstractButton(rect, defaultState, hoveredState, clickedState, disabledState);

		return button;
	}

	public static GuiAbstractButton createAddButton()
	{
		final Rect rect = Dim2D.build().width(20).height(20).flush();

		final GuiTexture defaultState = new GuiTexture(rect, ADD);
		final GuiTexture hoveredState = new GuiTexture(rect, ADD_HOVERED);
		final GuiTexture clickedState = new GuiTexture(rect, ADD_CLICKED);
		final GuiTexture disabledState = new GuiTexture(rect, ADD_DISABLED);

		final GuiAbstractButton button = new GuiAbstractButton(rect, defaultState, hoveredState, clickedState, disabledState);

		return button;
	}

}
