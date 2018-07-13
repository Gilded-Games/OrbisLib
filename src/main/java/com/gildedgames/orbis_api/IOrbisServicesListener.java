package com.gildedgames.orbis_api;

import com.gildedgames.orbis_api.data.management.IProjectManager;

public interface IOrbisServicesListener
{
	void onStartProjectManager(IProjectManager manager);
}