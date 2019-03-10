package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.core.variables.GuiVarBoolean;
import com.gildedgames.orbis.lib.core.variables.GuiVarString;
import com.gildedgames.orbis.lib.core.variables.IGuiVarDisplayContents;
import com.gildedgames.orbis.lib.util.mc.NBT;

public interface IScheduleLayerOptions extends IGuiVarDisplayContents, NBT
{
	GuiVarString getDisplayNameVar();

	GuiVarBoolean getReplacesSolidBlocksVar();

	void copyFrom(IScheduleLayerOptions other);
}
