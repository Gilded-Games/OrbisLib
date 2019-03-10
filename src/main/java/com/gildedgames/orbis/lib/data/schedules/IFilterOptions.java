package com.gildedgames.orbis.lib.data.schedules;

import com.gildedgames.orbis.lib.core.variables.GuiVarBoolean;
import com.gildedgames.orbis.lib.core.variables.GuiVarFloatRange;
import com.gildedgames.orbis.lib.core.variables.GuiVarString;
import com.gildedgames.orbis.lib.core.variables.IGuiVarDisplayContents;
import com.gildedgames.orbis.lib.util.mc.NBT;

public interface IFilterOptions extends NBT, IGuiVarDisplayContents
{

	GuiVarString getDisplayNameVar();

	GuiVarBoolean getChoosesPerBlockVar();

	GuiVarFloatRange getEdgeNoiseVar();

	void copyFrom(IFilterOptions options);

}
