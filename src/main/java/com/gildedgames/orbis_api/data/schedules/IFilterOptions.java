package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.core.variables.GuiVarBoolean;
import com.gildedgames.orbis_api.core.variables.GuiVarFloatRange;
import com.gildedgames.orbis_api.core.variables.GuiVarString;
import com.gildedgames.orbis_api.core.variables.IGuiVarDisplayContents;
import com.gildedgames.orbis_api.util.mc.NBT;

public interface IFilterOptions extends NBT, IGuiVarDisplayContents
{

	GuiVarString getDisplayNameVar();

	GuiVarBoolean getChoosesPerBlockVar();

	GuiVarFloatRange getEdgeNoiseVar();

	void copyFrom(IFilterOptions options);

}
