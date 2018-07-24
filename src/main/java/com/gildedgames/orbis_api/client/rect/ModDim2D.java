package com.gildedgames.orbis_api.client.rect;

import com.gildedgames.orbis_api.util.ObjectFilter;
import com.google.common.collect.Lists;

import java.util.*;

/**
 * A wrapper around a Rect object to provide a modified state via RectModifiers.
 * @author Brandon Pearce
 */
public class ModDim2D implements Rect
{

	private final BuildIntoRectHolder buildInto;

	private final RectListener ourListener;

	private List<RectModifier> modifiers = Lists.newArrayList();

	private List<RectListener> listeners = Lists.newArrayList();

	/**
	 * Originalstate: Non-modified rectangle value without modifiers, the base values
	 * modifiedState: original state with modifiers applied.
	 */
	private Rect originalState = Dim2D.flush(), modifiedState = Dim2D.flush();

	private boolean preventRecursion = false;

	public ModDim2D()
	{
		this.buildInto = new BuildIntoRectHolder(this);

		this.ourListener = this.createListener();
	}

	public static ModDim2D build()
	{
		return new ModDim2D();
	}

	public static ModDim2D build(final Rect rect)
	{
		final ModDim2D dim = new ModDim2D();
		dim.set(rect);
		return dim;
	}

	public static ModDim2D clone(final RectHolder owner)
	{
		return owner.dim().clone();
	}

	public static List<RectModifier.ModifierType> getChangedTypes(final Rect r1, final Rect r2)
	{
		final List<RectModifier.ModifierType> types = Lists.newArrayList();
		if (r1.x() != r2.x())
		{
			types.add(RectModifier.ModifierType.X);
		}
		if (r1.y() != r2.y())
		{
			types.add(RectModifier.ModifierType.Y);
		}
		if (r1.width() != r2.width())
		{
			types.add(RectModifier.ModifierType.WIDTH);
		}
		if (r1.height() != r2.height())
		{
			types.add(RectModifier.ModifierType.HEIGHT);
		}
		if (r1.degrees() != r2.degrees())
		{
			types.add(RectModifier.ModifierType.ROTATION);
		}
		return types;
	}

	private RectListener createListener()
	{
		return types -> ModDim2D.this.refreshModifiedState();
	}

	public Rect originalState()
	{
		return this.originalState;
	}

	private Rect modifiedState()
	{
		return this.modifiedState;
	}

	public BuildIntoRectHolder mod()
	{
		return this.buildInto;
	}

	public RectBuilder copyRect()
	{
		return new RectBuilder(this);
	}

	@Override
	public ModDim2D clone()
	{
		final ModDim2D clone = new ModDim2D();
		clone.set(this);
		return clone;
	}

	public ModDim2D set(final RectHolder holder)
	{
		this.set(holder.dim());
		return this;
	}

	public ModDim2D set(final Rect dim)
	{
		this.originalState = dim;
		this.buildInto.set(this.originalState);

		this.refreshModifiedState();
		return this;
	}

	public ModDim2D set(final ModDim2D modDim)
	{
		this.modifiers = new ArrayList<>(modDim.modifiers);
		this.listeners = new ArrayList<>(modDim.listeners);
		this.originalState = modDim.originalState;
		this.modifiedState = modDim.modifiedState;
		this.buildInto.set(this.originalState);
		return this;
	}

	/**
	 * Calculate the values for the modified state of this Dim2D object, based on the provided pool of Modifiers.
	 */
	protected void refreshModifiedState()
	{
		final Rect oldModifiedState = this.modifiedState;

		float degrees = this.originalState.degrees();

		float scale = this.originalState.scale();

		float posX = this.originalState.x();
		float posY = this.originalState.y();

		float width = this.originalState.width();
		float height = this.originalState.height();

		for (final RectModifier modifier : this.mods())
		{
			if (modifier == null)
			{
				continue;
			}

			final RectHolder source = modifier.getSource();

			if (source.dim() == null || source.dim() == this)
			{
				continue;
			}

			if (modifier.getModifying().equals(RectModifier.ModifierType.ROTATION))
			{
				Object modifyingWithValue = modifier.getModification().getValue(source, RectModifier.ModifierType.ROTATION);

				if (modifyingWithValue instanceof Float)
				{
					degrees += (Float) modifyingWithValue;
				}
			}

			if (modifier.getModifying().equals(RectModifier.ModifierType.SCALE))
			{
				Object modifyingWithValue = modifier.getModification().getValue(source, RectModifier.ModifierType.SCALE);

				if (modifyingWithValue instanceof Float)
				{
					scale *= (Float) modifyingWithValue;
				}
			}

			if (modifier.getModifying().equals(RectModifier.ModifierType.X) || modifier.getModifying().equals(RectModifier.ModifierType.POS))
			{
				Object modifyingWithValue = modifier.getModification().getValue(source, RectModifier.ModifierType.X);

				if (modifyingWithValue instanceof Float)
				{
					posX += (Float) modifyingWithValue;
				}
			}

			if (modifier.getModifying().equals(RectModifier.ModifierType.Y) || modifier.getModifying().equals(RectModifier.ModifierType.POS))
			{
				Object modifyingWithValue = modifier.getModification().getValue(source, RectModifier.ModifierType.Y);

				if (modifyingWithValue instanceof Float)
				{
					posY += (Float) modifyingWithValue;
				}
			}

			if (modifier.getModifying().equals(RectModifier.ModifierType.WIDTH) || modifier.getModifying().equals(RectModifier.ModifierType.AREA))
			{
				Object modifyingWithValue = modifier.getModification().getValue(source, RectModifier.ModifierType.WIDTH);

				if (modifyingWithValue instanceof Float)
				{
					width += (Float) modifyingWithValue;
				}
			}

			if (modifier.getModifying().equals(RectModifier.ModifierType.HEIGHT) || modifier.getModifying().equals(RectModifier.ModifierType.AREA))
			{
				Object modifyingWithValue = modifier.getModification().getValue(source, RectModifier.ModifierType.HEIGHT);

				if (modifyingWithValue instanceof Float)
				{
					height += (Float) modifyingWithValue;
				}
			}
		}

		final float offsetX = this.originalState.isCenteredX() ? (width * this.originalState.scale()) / 2 : 0;
		final float offsetY = this.originalState.isCenteredY() ? (height * this.originalState.scale()) / 2 : 0;

		width *= this.originalState.scale();
		height *= this.originalState.scale();

		posX -= offsetX;
		posY -= offsetY;

		this.modifiedState = Dim2D.build(this.originalState).pos(posX, posY).area(width, height).degrees(degrees).scale(scale).flush();

		if (this.preventRecursion)
		{
			return;
		}

		this.preventRecursion = true;

		final List<RectModifier.ModifierType> changedTypes = ModDim2D.getChangedTypes(oldModifiedState, this.modifiedState);

		for (final RectListener listener : this.listeners)
		{
			listener.notifyDimChange(changedTypes);
		}

		this.preventRecursion = false;
	}

	public Collection<RectModifier> mods()
	{
		return this.modifiers;
	}

	public boolean containsModifier(String identifier)
	{
		for (RectModifier modifier : this.modifiers)
		{
			if (modifier.getIdentifier().equals(identifier))
			{
				return true;
			}
		}

		return false;
	}

	public boolean containsModifier(String identifier, final RectHolder source)
	{
		for (RectModifier modifier : this.modifiers)
		{
			if (modifier.getIdentifier().equals(identifier) && modifier.getSource() == source)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Clears the modifiers that you pass along to the method
	 */
	public ModDim2D clear(final RectModifier.ModifierType... types)
	{
		if (types.length == 0)
		{
			for (final RectModifier modifier : this.modifiers)
			{
				modifier.getSource().dim().removeListener(this.ourListener);
			}
			this.modifiers.clear();
			this.refreshModifiedState();
			return this;
		}

		final List<RectModifier.ModifierType> list = Lists.newArrayList();
		Collections.addAll(list, types);

		this.modifiers = ObjectFilter.getTypesFrom(this.modifiers, new ObjectFilter.FilterCondition<RectModifier>(this.modifiers)
		{
			@Override
			public boolean isType(final RectModifier modifier)
			{
				for (final RectModifier.ModifierType type : ObjectFilter.getTypesFrom(list, RectModifier.ModifierType.class))
				{
					if (modifier.getModifying().equals(type))
					{
						return false;
					}
				}

				return true;
			}
		});

		this.refreshModifiedState();
		return this;
	}

	public ModDim2D add(RectModifier rectModifier)
	{
		if (!this.modifiers.contains(rectModifier))
		{
			this.modifiers.add(rectModifier);

			rectModifier.getSource().dim().addListener(this.ourListener);

			this.refreshModifiedState();
		}

		return this;
	}

	public ModDim2D add(String identifier, final RectHolder source, RectModifier.ModifierType mandatory, final RectModifier.ModifierType... modifying)
	{
		List<RectModifier.ModifierType> modifiers = Lists.newArrayList();

		modifiers.add(mandatory);
		modifiers.addAll(Lists.newArrayList(modifying));

		for (RectModifier.ModifierType type : modifiers)
		{
			this.add(identifier, source, new RectModifier.RectModification[] { type.getModification() }, new RectModifier.ModifierType[] { type });
		}

		return this;
	}

	public ModDim2D add(String identifier, final RectHolder source, RectModifier.RectModification[] modifications, final RectModifier.ModifierType[] modifying)
	{
		if (source.dim().equals(this))
		{
			throw new IllegalArgumentException();
		}

		for (final RectModifier.RectModification modificationEntry : modifications)
		{
			for (final RectModifier.ModifierType modifyingEntry : Lists.newArrayList(modifying))
			{
				final RectModifier modifier = new RectModifier(identifier, source, modificationEntry, modifyingEntry);

				if (!this.modifiers.contains(modifier))
				{
					this.modifiers.add(modifier);

					source.dim().addListener(this.ourListener);

					this.refreshModifiedState();
				}
			}
		}

		return this;
	}

	public boolean removeModifiers(String identifier, RectHolder source)
	{
		boolean success = false;

		final Iterator<RectModifier> iter = this.modifiers.iterator();

		while (iter.hasNext())
		{
			final RectModifier modifier = iter.next();

			if (modifier.getIdentifier().equals(identifier) && modifier.getSource() == source)
			{
				iter.remove();

				success = true;
			}
		}

		if (!this.hasModifiersFor(source))
		{
			source.dim().removeListener(this.ourListener);
		}

		this.refreshModifiedState();

		return success;
	}

	public boolean removeModifiers(String identifier)
	{
		boolean success = false;

		final Iterator<RectModifier> iter = this.modifiers.iterator();

		while (iter.hasNext())
		{
			final RectModifier modifier = iter.next();

			if (modifier.getIdentifier().equals(identifier))
			{
				iter.remove();

				if (!this.hasModifiersFor(modifier.getSource()))
				{
					modifier.getSource().dim().removeListener(this.ourListener);
				}

				success = true;
			}
		}

		this.refreshModifiedState();

		return success;
	}

	private boolean hasModifiersFor(final RectHolder holder)
	{
		for (final RectModifier rModifier : this.modifiers)
		{
			if (rModifier.getSource().equals(holder))
			{
				return true;
			}
		}
		return false;
	}

	public void addListener(final RectListener listener)
	{
		if (!this.listeners.contains(listener))
		{
			this.listeners.add(listener);
		}
	}

	public boolean removeListener(final RectListener listener)
	{
		return this.listeners.remove(listener);
	}

	@Override
	public float degrees()
	{
		return this.modifiedState().degrees();
	}

	@Override
	public float originX()
	{
		return this.modifiedState().originX();
	}

	@Override
	public float originY()
	{
		return this.modifiedState().originY();
	}

	@Override
	public float scale()
	{
		return this.modifiedState().scale();
	}

	@Override
	public float maxX()
	{
		return this.modifiedState().maxX();
	}

	@Override
	public float maxY()
	{
		return this.modifiedState().maxY();
	}

	@Override
	public float x()
	{
		return this.modifiedState().x();
	}

	@Override
	public float y()
	{
		return this.modifiedState().y();
	}

	@Override
	public float width()
	{
		return this.modifiedState().width();
	}

	@Override
	public float height()
	{
		return this.modifiedState().height();
	}

	@Override
	public boolean isCenteredX()
	{
		return this.modifiedState().isCenteredX();
	}

	@Override
	public boolean isCenteredY()
	{
		return this.modifiedState().isCenteredY();
	}

	@Override
	public boolean intersects(final float x, final float y)
	{
		return this.modifiedState().intersects(x, y);
	}

	@Override
	public boolean intersects(final Rect dim)
	{
		return this.modifiedState().intersects(dim);
	}

	@Override
	public RectBuilder rebuild()
	{
		return this.modifiedState().rebuild();
	}

	@Override
	public String toString()
	{
		return this.modifiedState.toString();
	}

	@Override
	public float centerX()
	{
		return this.modifiedState.centerX();
	}

	@Override
	public float centerY()
	{
		return this.modifiedState.centerY();
	}

	@Override
	public Pos2D center()
	{
		return this.modifiedState.center();
	}

	@Override
	public Pos2D min()
	{
		return this.modifiedState.min();
	}

}
