package com.gildedgames.orbis.lib.client.gui.data;

import com.gildedgames.orbis.lib.util.mc.IText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class Text implements IText
{

	public static final IText EMPTY = new Text(new StringTextComponent(""), 1.0F);

	private static final FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

	private ITextComponent component;

	private float scale;

	public Text(final ITextComponent component, final float scale)
	{
		this.component = component;
		this.scale = scale;
	}

	@Override
	public ITextComponent component()
	{
		return this.component;
	}

	@Override
	public float scaledHeight()
	{
		return (int) (this.height() * this.scale);
	}

	@Override
	public float scaledWidth()
	{
		return (int) (this.width() * this.scale);
	}

	@Override
	public float scale()
	{
		return this.scale;
	}

	@Override
	public float width()
	{
		return fontRenderer.getStringWidth(this.component.getFormattedText());
	}

	@Override
	public float height()
	{
		return fontRenderer.FONT_HEIGHT;
	}

	@Override
	public void write(final CompoundNBT tag)
	{
		tag.putFloat("scale", this.scale);
		tag.putString("text", this.component.getUnformattedComponentText());
	}

	@Override
	public void read(final CompoundNBT tag)
	{
		this.scale = tag.getFloat("Scale");
		this.component = new StringTextComponent(tag.getString("text"));
	}
}
