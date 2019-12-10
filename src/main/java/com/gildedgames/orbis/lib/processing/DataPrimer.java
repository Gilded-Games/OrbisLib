package com.gildedgames.orbis.lib.processing;

import com.gildedgames.orbis.lib.block.BlockData;
import com.gildedgames.orbis.lib.block.BlockDataContainer;
import com.gildedgames.orbis.lib.core.ICreationData;
import com.gildedgames.orbis.lib.core.PlacedBlueprint;
import com.gildedgames.orbis.lib.core.PlacementCondition;
import com.gildedgames.orbis.lib.core.baking.BakedBlueprint;
import com.gildedgames.orbis.lib.core.baking.IBakedPosAction;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintDataPalette;
import com.gildedgames.orbis.lib.data.region.IRegion;
import com.gildedgames.orbis.lib.data.region.Region;
import com.gildedgames.orbis.lib.util.RotationHelp;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DataPrimer
{
	private final IBlockAccessExtended access;

	public DataPrimer(final IBlockAccessExtended primer)
	{
		this.access = primer;
	}

	@Nullable
	public World getWorld()
	{
		return this.access.getWorld();
	}

	public IBlockAccessExtended getAccess()
	{
		return this.access;
	}

	public void spawn(Entity entity)
	{
		this.access.spawnEntity(entity);
	}

	public boolean canGenerate(BakedBlueprint blueprint, BlockPos offset)
	{
		Region region = new Region(blueprint.getBakedRegion());
		region.add(offset);

		if (!this.access.canAccess(region.getMin().getX() - 2, region.getMin().getY() - 2, region.getMin().getZ() - 2,
				region.getMax().getX() + 2, region.getMax().getY() + 2, region.getMax().getZ() + 2))
		{
			return false;
		}

		for (final PlacementCondition condition : blueprint.getDefinition().getConditions())
		{
			if (!condition.validate(this.access, blueprint, region.getMin()))
			{
				return false;
			}
		}

		return true;
	}

    public void setBlockInWorld(BlockData data, ICreationData<?> creationData) {
        this.setBlockInWorld(data.getBlockState(), data.getTileEntity(), creationData.getPos(), creationData);
    }

	public void setBlockInWorld(final IBlockState state, final NBTTagCompound entity, final BlockPos pos, final ICreationData<?> creationData)
	{
		if (state.getMaterial() == Material.AIR && !creationData.placeAir())
		{
			return;
		}

		if (state.getBlock() == Blocks.STRUCTURE_VOID && !creationData.placesVoid())
		{
			return;
		}

		this.access.setBlockState(pos, state, 2 | 16);

		if (entity != null && this.access.getWorld() != null)
		{
			TileEntity te = TileEntity.create(this.access.getWorld(), entity);

			this.access.setTileEntity(pos, te);
		}

		// TODO: Re-enable event.
		/*final ChangeBlockEvent changeBlockEvent = new ChangeBlockEvent(creationData.getWorld(), min, creationData.getCreator());
		MinecraftForge.EVENT_BUS.post(changeBlockEvent);*/
	}

	public void place(BakedBlueprint baked) {
		this.place(baked, BlockPos.ORIGIN);
	}

	public void place(BakedBlueprint baked, BlockPos relocateTo)
	{
		if (this.copyBlocksIntoWorld(relocateTo, baked, null, baked.getCreationData()))
		{
            for (BakedBlueprint child : baked.getBakedBlueprintChildren()) {
                this.place(child, relocateTo);
            }

            for (IBakedPosAction action : baked.getBakedPositionActions())
            {
                action.call(this);
            }
		}
	}

	public void place(PlacedBlueprint placed, IRegion region)
	{
		BakedBlueprint baked = placed.getBaked();

		BlockPos offset = placed.getCreationData().getPos();

		if (this.copyBlocksIntoWorld(offset, baked, region, placed.getCreationData()))
		{
            for (BakedBlueprint child : baked.getBakedBlueprintChildren()) {
                this.place(child, offset);
            }

            Region intersection = placed.getRegion().fromIntersection(region);

            for (IBakedPosAction action : placed.getPendingPosActions())
            {
                if (intersection.contains(action.getPos()))
                {
                    action.call(this);
                }
            }
		}
	}

	public boolean create(BlockDataContainer blocks, ICreationData<?> data) {
		BlockPos min = data.getPos();
		BlockPos max = data.getPos().add(blocks.getWidth() - 1, blocks.getHeight() - 1, blocks.getLength() - 1);

		return this.create(blocks, data, BlockPos.getAllInBox(min, max), min);
	}

    public boolean create(BlockDataContainer blocks, ICreationData<?> data, Iterable<? extends BlockPos> toCreate, BlockPos min) {
        for (BlockPos pos : toCreate)
        {
            int x = pos.getX() - min.getX();
            int y = pos.getY() - min.getY();
            int z = pos.getZ() - min.getZ();

            final IBlockState state = blocks.getBlockState(x, y, z);
            final NBTTagCompound entity;

            if (state.getBlock().hasTileEntity(state))
            {
                entity = blocks.getTileEntity(x, y, z).data;
            }
            else
            {
                entity = null;
            }

            this.setBlockInWorld(state, entity, pos, data);
        }

        return true;
    }

    public void create(BlueprintDataPalette palette, ICreationData<?> data)
    {
        final BlueprintData b = palette.fetchRandom(data.getWorld(), data.getRandom());
        final Rotation rotation = data.getRotation();
        final IRegion region = RotationHelp.regionFromCenter(data.getPos(), b, rotation);

        BakedBlueprint baked = new BakedBlueprint(b, data.clone().pos(region.getMin()));

        this.place(baked, data.getPos());
    }

	private boolean copyBlocksIntoWorld(BlockPos offset, BakedBlueprint blueprint, IRegion bounds, ICreationData<?> data)
	{
		return copyBlocksIntoWorld(offset, blueprint.getBlockData(), blueprint.getBakedRegion(), bounds, data);
	}

    public boolean copyBlocksIntoWorld(BlockPos offset, BlockDataContainer blocks, IRegion origin, IRegion bounds, ICreationData<?> data)
    {
		final Region region = new Region(origin);
		region.add(offset);

		final Region intersection;

		if (bounds != null)
		{
			if (!region.intersectsWith(bounds))
			{
				return false;
			}

			intersection = region.fromIntersection(bounds);
		}
		else
		{
			intersection = region;
		}

		for (BlockPos pos : BlockPos.getAllInBoxMutable(intersection.getMin(), intersection.getMax()))
		{
			if (!data.shouldCreate(pos))
			{
				continue;
			}

			int x = pos.getX() - region.getMin().getX();
			int y = pos.getY() - region.getMin().getY();
			int z = pos.getZ() - region.getMin().getZ();

			final IBlockState state = blocks.getBlockState(x, y, z);
			final NBTTagCompound entity;

			if (state.getBlock().hasTileEntity(state))
			{
				entity = blocks.getTileEntity(x, y, z).data;
			}
			else
			{
				entity = null;
			}

			this.setBlockInWorld(state, entity, pos, data);
		}

		return true;
	}

}
