package com.gildedgames.orbis.lib.processing;

import com.gildedgames.orbis.lib.OrbisLib;
import com.gildedgames.orbis.lib.core.CreationData;
import com.gildedgames.orbis.lib.core.ICreationData;
import com.gildedgames.orbis.lib.core.baking.*;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.blueprint.BlueprintNetworkData;
import com.gildedgames.orbis.lib.data.framework.generation.searching.PathwayUtil;
import com.gildedgames.orbis.lib.data.framework.interfaces.EnumFacingMultiple;
import com.gildedgames.orbis.lib.data.management.IDataIdentifier;
import com.gildedgames.orbis.lib.data.region.IRegion;
import com.gildedgames.orbis.lib.data.region.Region;
import com.gildedgames.orbis.lib.data.schedules.ScheduleEntranceHolder;
import com.gildedgames.orbis.lib.util.RotationHelp;
import com.google.common.collect.Lists;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;



public class BlueprintNetworkGenerator
{
    public interface IDebugNetworkPainter {
        void paint(IRegion region, int color, int zLevel);
    }

    public enum BlueprintNetworkGenerationStep {
        START,
        GENERATE_NODES,
        FINISH,
    }

    private IDebugNetworkPainter debugPainter;
    private BiConsumer<BakedBlueprint, BlockPos> nodeGenerator;

    private BlueprintNetworkData networkData;
    private BakedBlueprintNetwork bakedNetwork;

    private List<BlueprintData> potentialRooms;

    private BlueprintNetworkGenerationStep currentStep = BlueprintNetworkGenerationStep.START;

    private BlockPos pos;
    private World world;
    private Random rand;

    private List<BlueprintNetworkNode> unresolvedNodes = Lists.newArrayList();
    private List<BlueprintNetworkNode> queuedNodes = Lists.newArrayList();

    public BlueprintNetworkGenerator(BlueprintNetworkData networkData, ICreationData<?> creationData,IDebugNetworkPainter debugPainter, BiConsumer<BakedBlueprint, BlockPos> nodeGenerator) {
        this.networkData = networkData;
        this.debugPainter = debugPainter;
        this.nodeGenerator = nodeGenerator;

        this.pos = creationData.getPos();
        this.world = creationData.getWorld();
        this.rand = creationData.getRandom();

        this.bakedNetwork = new BakedBlueprintNetwork();
    }

    public boolean step() {
        this.unresolvedNodes.addAll(this.queuedNodes);
        this.queuedNodes.clear();

        switch (this.currentStep) {
            case START: {
                this.start();
                this.currentStep = BlueprintNetworkGenerationStep.GENERATE_NODES;

                return false;
            }
            case GENERATE_NODES: {
                Iterator<BlueprintNetworkNode> nodes = this.unresolvedNodes.iterator();

                if (nodes.hasNext()) {
                    BlueprintNetworkNode node = nodes.next();

                    if (this.generateNode(this.potentialRooms, this.rand, node)) {
                        nodes.remove();
                    }
                } else {
                    this.currentStep = BlueprintNetworkGenerationStep.FINISH;
                }

                return false;
            }
            case FINISH: {
                System.out.println("Finished!");
                break;
            }
        }

        return true;
    }

    private void start() {
        this.potentialRooms = fetchBlueprints(this.networkData.getRooms());

        BlueprintData start = this.potentialRooms.get(this.rand.nextInt(this.potentialRooms.size()));
        BakedBlueprint bakedStart = new BakedBlueprint(start, new CreationData(this.world).pos(pos));
        BlueprintNetworkNode startNode = new BlueprintNetworkNode(bakedStart, 0, this.rand);

        this.addNodeForGeneration(startNode);
    }

    private void addNodeForGeneration(BlueprintNetworkNode node) {
        this.bakedNetwork.addBakedNode(node);

        BakedBlueprint bakedNode = node.getBakedData();

        this.debugPainter.paint(bakedNode.getBakedRegion(), 0xFF474747, 0);
        this.nodeGenerator.accept(bakedNode, BlockPos.ORIGIN);

        this.queuedNodes.add(node);
    }

    private boolean generateNode(List<BlueprintData> potentialRooms, Random rand, BlueprintNetworkNode node) {
        if (node.getDepth() < this.networkData.getTargetDepth() && node.getChildrenNodeCount() < 3) {
            if (node.getEntrancesToConnect().hasNext()) {
                PotentialEntrance potentialEntrance = node.getEntrancesToConnect().next();

                if (!node.getUsedEntrances().contains(potentialEntrance)) {
                    BlueprintData nextRoom = this.randRoom(potentialRooms, rand);

                    boolean generated = this.generateNextNode(potentialEntrance, nextRoom, node);
                    int entranceColor = generated ? 0xFFce6dd1 : 0xFFc7202e;

                    Region region = new Region(potentialEntrance.getHolder().getBounds());
                    region.add(node.getBakedData().getBakedRegion().getMin());

                    if (generated) {
                        BakedBlueprint entranceToGenerate = new BakedBlueprint(potentialEntrance.getData(), new CreationData(this.world).rotation(potentialEntrance.getHolder().getRotation()));
                        entranceToGenerate.getBakedRegion().relocate(region.getMin());

                        this.nodeGenerator.accept(entranceToGenerate, BlockPos.ORIGIN);
                        this.debugPainter.paint(entranceToGenerate.getBakedRegion(), entranceColor, 2);
                    }
                }

                return false;
            }
        }

        return true; // Return true when node finished with generation
    }

    private boolean generateNextNode(PotentialEntrance potentialEntrance, BlueprintData nextRoom, BlueprintNetworkNode comingFrom) {
        BakedBlueprint bakedComingFrom = comingFrom.getBakedData();

        BlueprintData fromEntrance = potentialEntrance.getData();
        ScheduleEntranceHolder fromEntranceSchedule = potentialEntrance.getHolder();

        BakedScheduleLayers scheduleLayers = new BakedScheduleLayers(nextRoom, this.world.rand);
        ConnectionData connectionData = this.connectBlueprints(bakedComingFrom, fromEntrance, fromEntranceSchedule, nextRoom, scheduleLayers);

        if (connectionData != null) {
            comingFrom.addChildrenNodeCount(1);

            CreationData creationData = new CreationData(this.world).pos(connectionData.pos.add(bakedComingFrom.getBakedRegion().getMin())).rotation(connectionData.rotation);
            BakedBlueprint bakedNextRoom = new BakedBlueprint(nextRoom, scheduleLayers, creationData);

            bakedNextRoom.getScheduleLayers().bakePotentialEntrances(connectionData.rotation);

            BlueprintNetworkNode nextNode = new BlueprintNetworkNode(bakedNextRoom, comingFrom.getDepth() + 1, this.rand);
            nextNode.addUsedEntrance(connectionData.usedEntrance);

            this.addNodeForGeneration(nextNode);

            BakedBlueprint connectToEntrance = new BakedBlueprint(connectionData.usedEntrance.getData(), new CreationData(this.world).rotation(connectionData.usedEntranceRotation));
            connectToEntrance.getBakedRegion().relocate(connectionData.usedEntrancePos);

            this.nodeGenerator.accept(connectToEntrance, BlockPos.ORIGIN);
        }

        return connectionData != null;
    }

    private BlueprintData randRoom(List<BlueprintData> blueprints, Random rand) {
        return blueprints.get(rand.nextInt(blueprints.size()));
    }

    private ConnectionData connectBlueprints(BakedBlueprint fromRoom, BlueprintData fromEntrance, ScheduleEntranceHolder fromEntranceSchedule, BlueprintData connectingToRoom, BakedScheduleLayers roomScheduleLayers) {
        BlockPos fromMin = fromRoom.getBakedRegion().getMin();

        EnumFacingMultiple connectingFrom = PathwayUtil.getRotated(fromEntrance.getEntrance().getFacing(), fromEntranceSchedule.getRotation());

        Collections.shuffle(roomScheduleLayers.getPotentialEntrances(), rand);

        Region rect = new Region(new BlockPos(0, 0, 0),
                new BlockPos(connectingToRoom.getWidth() - 1, connectingToRoom.getHeight() - 1, connectingToRoom.getLength() - 1));

        for (PotentialEntrance potentialEntrance : roomScheduleLayers.getPotentialEntrances()) {
            BlueprintData toEntrance = potentialEntrance.getData();
            ScheduleEntranceHolder toEntranceSchedule = potentialEntrance.getHolder();

            EnumFacingMultiple connectingTo = PathwayUtil.getRotated(toEntrance.getEntrance().getFacing(), toEntranceSchedule.getRotation());

            Rotation rotation = Rotation.NONE;

            Rotation connectingFromRot = RotationHelp.fromFacing(connectingFrom.getOpposite());
            Rotation connectingToRot = RotationHelp.fromFacing(connectingTo);

            if (connectingFrom.canRotateToFaceEachother(connectingTo))
            {
                rotation = RotationHelp.getRotationDifference(connectingToRot, connectingFromRot);
            }
            else if (connectingTo != connectingFrom)
            {
                continue;
            }

            Region trEntrance = new Region(toEntranceSchedule.getBounds());
            RotationHelp.rotateNew(trEntrance, rotation);

            IRegion fromEntranceRect = fromEntranceSchedule.getBounds();
            Region adjacentEntrance = (Region) PathwayUtil.adjacent(fromEntranceRect, connectingFrom);

            int dx = adjacentEntrance.getMin().getX() - trEntrance.getMin().getX();
            int dy = adjacentEntrance.getMin().getY() - trEntrance.getMin().getY();
            int dz = adjacentEntrance.getMin().getZ() - trEntrance.getMin().getZ();

            Region finalRotatedRect = new Region(rect);
            RotationHelp.rotateNew(finalRotatedRect, rotation);

            finalRotatedRect.add(dx, dy, dz);
            finalRotatedRect.add(fromMin);

            if (this.collidesWithExistingNodes(finalRotatedRect)) {
                continue;
            }

            adjacentEntrance.add(fromMin);

            trEntrance.add(dx, dy, dz);
            trEntrance.add(fromMin);

            //this.debugPainter.paint(finalRotatedRect, 0xFF171717, 1);
            //this.nodeGenerator.accept(rotated, 0xFF171717);

            //this.debugPainter.paint(adjacentEntrance, 0xFFcc8521, 1);

            return new ConnectionData(rotation, new BlockPos(dx, dy, dz), potentialEntrance, trEntrance.getMin(), connectingToRot.add(rotation));
        }

        return null;
    }

    private boolean collidesWithExistingNodes(IRegion region) {
        for (BlueprintNetworkNode node : this.bakedNetwork.getNodes()) {
            BakedBlueprint baked = node.getBakedData();

            if (baked.getBakedRegion().intersectsWith(region)) {
                return true;
            }
        }

        return false;
    }

    private List<BlueprintData> fetchBlueprints(List<IDataIdentifier> ids) {
        return ids.stream()
                .map((id) ->  OrbisLib.services().getProjectManager().findData(id))
                .filter((opt) -> opt.isPresent() && opt.get() instanceof BlueprintData)
                .map((opt) -> (BlueprintData)opt.get()).collect(Collectors.toList());
    }

    public static class ConnectionData {
        public Rotation rotation;
        public BlockPos pos;

        public PotentialEntrance usedEntrance;
        public BlockPos usedEntrancePos;
        public Rotation usedEntranceRotation;

        public ConnectionData(Rotation rotation, BlockPos pos, PotentialEntrance usedEntrance, BlockPos usedEntrancePos, Rotation usedEntranceRotation) {
            this.rotation = rotation;
            this.pos = pos;
            this.usedEntrance = usedEntrance;
            this.usedEntrancePos = usedEntrancePos;
            this.usedEntranceRotation = usedEntranceRotation;
        }
    }
}
