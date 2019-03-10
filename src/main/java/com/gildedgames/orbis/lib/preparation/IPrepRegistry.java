package com.gildedgames.orbis.lib.preparation;

import java.util.List;

public interface IPrepRegistry
{

	void register(IPrepRegistryEntry registration);

	List<IPrepRegistryEntry> getEntries();

}
