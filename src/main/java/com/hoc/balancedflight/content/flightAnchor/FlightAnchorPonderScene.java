package com.hoc.balancedflight.content.flightAnchor;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.hoc.balancedflight.content.flightAnchor.entity.FlightAnchorBehaviour;
import com.hoc.balancedflight.content.flightAnchor.entity.FlightAnchorEntity;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class FlightAnchorPonderScene {

    public static void ponderScene(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("flight_anchor", "Powered flight with Rotational Force");
        scene.configureBasePlate(1, 0, 5);

        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.world().showSection(util.select().layer(1), Direction.UP);
        scene.world().setKineticSpeed(util.select().everywhere(), 0f);

        Selection flightAnchorSelection = util.select().position(3, 1, 2);

        scene.overlay()
                .showText(90)
                .placeNearTarget()
                .text("The flight anchor requires an immense amount of power to operate.")
                .pointAt(flightAnchorSelection.getCenter());
        scene.idle(100);

        BlockPos flightAnchorPos = util.grid().at(3, 1, 2);
        scene.world().modifyBlockEntity(flightAnchorPos, FlightAnchorEntity.class, entity -> {
            FlightAnchorBehaviour.beaconTick(entity.getLevel(), entity.getBlockPos(), entity);
        });

        scene.world().setKineticSpeed(util.select().everywhere(), 32f);
        scene.overlay()
                .showText(90)
                .placeNearTarget()
                .text("For each RPM, you will be able to fly one block around the anchor.")
                .pointAt(flightAnchorSelection.getCenter());
        scene.idle(100);

        scene.world().setKineticSpeed(util.select().everywhere(), 128f);
        scene.overlay()
                .showText(1000)
                .placeNearTarget()
                .text("Higher speeds cover a much higher surface area.")
                .pointAt(flightAnchorSelection.getCenter());
        scene.idle(100);

        scene.markAsFinished();
    }
}