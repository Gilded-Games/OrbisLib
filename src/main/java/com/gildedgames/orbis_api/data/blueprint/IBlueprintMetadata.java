package com.gildedgames.orbis_api.data.blueprint;

import com.gildedgames.orbis_api.core.variables.GuiVarBoolean;
import com.gildedgames.orbis_api.core.variables.IGuiVarDisplayContents;
import com.gildedgames.orbis_api.util.mc.NBT;

public interface IBlueprintMetadata extends IGuiVarDisplayContents, NBT
{
	GuiVarBoolean getLayerTransparencyVar();

	GuiVarBoolean getChoosePerBlockOnPostGenVar();
}
