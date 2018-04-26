package com.gildedgames.orbis_api.preparation;

import java.util.List;

public interface IPrepRegistry
{

	void register(IPrepRegistryEntry registration);

	List<IPrepRegistryEntry> getEntries();

}
