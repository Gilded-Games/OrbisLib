package com.gildedgames.orbis_api.client.rect;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.annotation.Nullable;

public class RectModifier
{
	private final RectHolder source;

	private final ModifierType modifying;

	private final RectModification modification;

	private String identifier;

	public RectModifier(String identifier, final RectModification modification, final ModifierType modifying)
	{
		this(identifier, null, modification, modifying);
	}

	public RectModifier(String identifier, final RectHolder source, final RectModification modification, final ModifierType modifying)
	{
		this.identifier = identifier;
		this.source = source;
		this.modification = modification;
		this.modifying = modifying;
	}

	public String getIdentifier()
	{
		return this.identifier;
	}

	@Nullable
	public RectHolder getSource()
	{
		return this.source;
	}

	public RectModification getModification()
	{
		return this.modification;
	}

	public ModifierType getModifying()
	{
		return this.modifying;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (super.equals(obj))
		{
			return true;
		}

		if (!(obj instanceof RectModifier))
		{
			return false;
		}

		final RectModifier modifier = (RectModifier) obj;
		return modifier.identifier.equals(this.identifier);
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(97, 37).append(this.identifier).toHashCode();
	}

	public enum ModifierType
	{
		X
				{
					private RectModification modification = (modifyingWith, modifying) -> modifyingWith.dim().x();

					@Override
					public RectModification getModification()
					{
						return this.modification;
					}
				}, Y
			{
				private RectModification modification = (modifyingWith, modifying) -> modifyingWith.dim().y();

				@Override
				public RectModification getModification()
				{
					return this.modification;
				}
			},
		MAX_X
				{
					private RectModification modification = (modifyingWith, modifying) -> modifyingWith.dim().maxX();

					@Override
					public RectModification getModification()
					{
						return this.modification;
					}
				},
		MAX_Y
				{
					private RectModification modification = (modifyingWith, modifying) -> modifyingWith.dim().maxY();

					@Override
					public RectModification getModification()
					{
						return this.modification;
					}
				}, POS
			{
				private RectModification modification;

				@Override
				public RectModification getModification()
				{
					if (this.modification == null)
					{
						this.modification = (modifyingWith, modifying) ->
						{
							if (modifying == X)
							{
								return modifyingWith.dim().x();
							}

							if (modifying == Y)
							{
								return modifyingWith.dim().y();
							}

							return 0;
						};
					}

					return this.modification;
				}
			}, WIDTH
			{
				private RectModification modification = (modifyingWith, modifying) -> modifyingWith.dim().width();

				@Override
				public RectModification getModification()
				{
					return this.modification;
				}
			}, HEIGHT
			{
				private RectModification modification = (modifyingWith, modifying) -> modifyingWith.dim().height();

				@Override
				public RectModification getModification()
				{
					return this.modification;
				}
			}, AREA
			{
				private RectModification modification;

				@Override
				public RectModification getModification()
				{
					if (this.modification == null)
					{
						this.modification = (modifyingWith, modifying) ->
						{
							if (modifying == WIDTH)
							{
								return modifyingWith.dim().width();
							}

							if (modifying == HEIGHT)
							{
								return modifyingWith.dim().height();
							}

							return 0;
						};
					}

					return this.modification;
				}
			}, SCALE
			{
				private RectModification modification = (modifyingWith, modifying) -> modifyingWith.dim().scale();

				@Override
				public RectModification getModification()
				{
					return this.modification;
				}
			}, ROTATION
			{
				private RectModification modification = (modifyingWith, modifying) -> modifyingWith.dim().degrees();

				@Override
				public RectModification getModification()
				{
					return this.modification;
				}
			};

		public abstract RectModification getModification();
	}

	public interface RectModification
	{
		Object getValue(RectHolder modifyingWith, ModifierType modifying);
	}

}
