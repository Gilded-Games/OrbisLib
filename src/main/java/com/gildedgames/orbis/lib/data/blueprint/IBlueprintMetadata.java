package com.gildedgames.orbis.lib.data.blueprint;

import com.gildedgames.orbis.lib.core.variables.GuiVarBoolean;
import com.gildedgames.orbis.lib.core.variables.IGuiVarDisplayContents;
import com.gildedgames.orbis.lib.util.mc.NBT;

public interface IBlueprintMetadata extends IGuiVarDisplayContents, NBT
{
	GuiVarBoolean getLayerTransparencyVar();

	GuiVarBoolean getChoosePerBlockOnPostGenVar();
}
