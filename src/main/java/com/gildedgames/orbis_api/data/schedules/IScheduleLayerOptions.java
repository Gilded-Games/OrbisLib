package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.core.variables.GuiVarBoolean;
import com.gildedgames.orbis_api.core.variables.GuiVarString;
import com.gildedgames.orbis_api.core.variables.IGuiVarDisplayContents;
import com.gildedgames.orbis_api.util.mc.NBT;

public interface IScheduleLayerOptions extends IGuiVarDisplayContents, NBT
{
	GuiVarString getDisplayNameVar();

	GuiVarBoolean getReplacesSolidBlocksVar();

	void copyFrom(IScheduleLayerOptions other);
}
