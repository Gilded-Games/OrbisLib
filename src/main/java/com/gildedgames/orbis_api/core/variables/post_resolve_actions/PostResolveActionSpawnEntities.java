package com.gildedgames.orbis_api.core.variables.post_resolve_actions;

import com.gildedgames.orbis_api.client.rect.Pos2D;
import com.gildedgames.orbis_api.core.baking.BakedEntitySpawn;
import com.gildedgames.orbis_api.core.baking.IBakedPosAction;
import com.gildedgames.orbis_api.core.variables.GuiVarBoolean;
import com.gildedgames.orbis_api.core.variables.GuiVarFloat;
import com.gildedgames.orbis_api.core.variables.GuiVarItemStack;
import com.gildedgames.orbis_api.core.variables.IGuiVar;
import com.gildedgames.orbis_api.core.variables.displays.GuiVarDisplay;
import com.gildedgames.orbis_api.data.IDataUser;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.data.schedules.IPosActionBaker;
import com.gildedgames.orbis_api.data.schedules.ScheduleRegion;
import com.gildedgames.orbis_api.util.io.NBTFunnel;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class PostResolveActionSpawnEntities implements IPostResolveAction, IDataUser<ScheduleRegion>, IPosActionBaker
{
	private static final Function<ItemStack, Boolean> SPAWN_EGG_VALIDATOR = (itemStack -> itemStack.getItem() instanceof ItemMonsterPlacer);

	private List<IGuiVar> variables = Lists.newArrayList();

	private GuiVarItemStack itemStackVariable;

	private GuiVarBoolean customRotation;

	private GuiVarFloat rotationDegrees;

	private Pos2D guiPos = Pos2D.ORIGIN;

	private GuiVarDisplay parentDisplay;

	private ScheduleRegion scheduleRegion;

	public PostResolveActionSpawnEntities()
	{
		this.itemStackVariable = new GuiVarItemStack("orbis.gui.spawn_egg", SPAWN_EGG_VALIDATOR);
		this.customRotation = new GuiVarBoolean("orbis.gui.custom_rotation");
		this.rotationDegrees = new GuiVarFloat("orbis.gui.rotation_degrees");

		this.variables.add(this.itemStackVariable);
		this.variables.add(this.customRotation);
		this.variables.add(this.rotationDegrees);
	}

	@Override
	public String getName()
	{
		return "orbis.gui.spawn_entities";
	}

	@Override
	public List<IGuiVar> getVariables()
	{
		return this.variables;
	}

	@Override
	public void resolve(Random rand)
	{

	}

	@Override
	public Pos2D getGuiPos()
	{
		return this.guiPos;
	}

	@Override
	public void setGuiPos(Pos2D pos)
	{
		this.guiPos = pos;
	}

	@Override
	public void write(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		funnel.set("itemStackVariable", this.itemStackVariable);
		funnel.set("customRotation", this.customRotation);
		funnel.set("rotationDegrees", this.rotationDegrees);

		funnel.set("guiPos", this.guiPos, NBTFunnel.POS2D_SETTER);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		NBTFunnel funnel = new NBTFunnel(tag);

		this.itemStackVariable = funnel.getWithDefault("itemStackVariable", () -> this.itemStackVariable);
		this.customRotation = funnel.getWithDefault("customRotation", () -> this.customRotation);
		this.rotationDegrees = funnel.getWithDefault("rotationDegrees", () -> this.rotationDegrees);

		this.guiPos = funnel.getWithDefault("guiPos", NBTFunnel.POS2D_GETTER, () -> this.guiPos);

		this.itemStackVariable.setStackValidator(SPAWN_EGG_VALIDATOR);

		this.variables.clear();

		this.variables.add(this.itemStackVariable);
		this.variables.add(this.customRotation);
		this.variables.add(this.rotationDegrees);
	}

	@Override
	public void setParentDisplay(GuiVarDisplay parentDisplay)
	{
		this.parentDisplay = parentDisplay;
	}

	@Override
	public String getDataIdentifier()
	{
		return "scheduleRegion";
	}

	@Override
	public void setUsedData(ScheduleRegion scheduleRegion)
	{
		this.scheduleRegion = scheduleRegion;
	}

	@Override
	public List<IBakedPosAction> bakeActions(IRegion bounds, Random rand, Rotation rotation)
	{
		List<IBakedPosAction> actions = Lists.newArrayList();
		float rotationOffset = (rotation.compareTo(Rotation.NONE) * 90.0F);

		if (this.itemStackVariable.getData().getItem() instanceof ItemMonsterPlacer)
		{
			for (int i = 0; i < this.itemStackVariable.getData().getCount(); i++)
			{
				BlockPos pos = bounds.getMin();

				BakedEntitySpawn placedEntity = new BakedEntitySpawn(this.itemStackVariable.getData(), pos.add(rand.nextInt(bounds.getWidth()), 0,
						rand.nextInt(bounds.getLength())), this.customRotation.getData(), this.rotationDegrees.getData() + rotationOffset);

				actions.add(placedEntity);
			}
		}

		return actions;
	}
}
