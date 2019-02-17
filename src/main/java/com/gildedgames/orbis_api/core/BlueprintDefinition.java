package com.gildedgames.orbis_api.core;

import com.gildedgames.orbis_api.core.registry.IOrbisDefinitionRegistry;
import com.gildedgames.orbis_api.data.blueprint.BlueprintData;

import java.util.Arrays;
import java.util.Comparator;

public class BlueprintDefinition
{

	private final BlueprintData data;

	private IOrbisDefinitionRegistry registry;

	private PlacementCondition[] conditions = new PlacementCondition[0];

	private boolean randomRotation = true;

	private int floorHeight;

	public BlueprintDefinition(final BlueprintData data)
	{
		this.data = data;
	}

	public BlueprintDefinition(final BlueprintData data, int floorHeight)
	{
		this.data = data;
		this.floorHeight = floorHeight;
	}

	public int getFloorHeight()
	{
		return this.floorHeight;
	}

	public BlueprintDefinition setConditions(final PlacementCondition... conditions)
	{
		this.conditions = conditions;

		Arrays.sort(this.conditions, Comparator.comparingInt(PlacementCondition::getPriority));

		return this;
	}

	public PlacementCondition[] getConditions()
	{
		return this.conditions;
	}

	public BlueprintData getData()
	{
		return this.data;
	}

	public BlueprintDefinition setRandomRotation(final boolean flag)
	{
		this.randomRotation = flag;

		return this;
	}

	public boolean hasRandomRotation()
	{
		return this.randomRotation;
	}

	public IOrbisDefinitionRegistry getRegistry()
	{
		return this.registry;
	}

	public BlueprintDefinition setRegistry(final IOrbisDefinitionRegistry registry)
	{
		this.registry = registry;

		return this;
	}

	@Override
	public BlueprintDefinition clone()
	{
		return new BlueprintDefinition(this.data)
				.setRegistry(this.registry)
				.setRandomRotation(this.randomRotation)
				.setConditions(this.conditions)
				.setFloorHeight(this.floorHeight);
	}

	private BlueprintDefinition setFloorHeight(int floorHeight)
	{
		this.floorHeight = floorHeight;

		return this;
	}
}
