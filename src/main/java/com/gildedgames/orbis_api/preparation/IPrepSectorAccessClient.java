package com.gildedgames.orbis_api.preparation;

public interface IPrepSectorAccessClient extends IPrepSectorAccess
{
	void addSector(IPrepSector sector);

	void removeSector(IPrepSector sector);
}
