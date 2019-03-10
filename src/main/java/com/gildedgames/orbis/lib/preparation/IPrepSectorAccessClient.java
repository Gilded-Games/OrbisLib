package com.gildedgames.orbis.lib.preparation;

public interface IPrepSectorAccessClient extends IPrepSectorAccess
{
	void addSector(IPrepSector sector);

	void removeSector(IPrepSector sector);
}
