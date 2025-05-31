package com.hoc.balancedflight.content.flightAnchor.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.hoc.balancedflight.content.flightAnchor.entity.FlightAnchorEntity;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FlightAnchorBeamRenderer implements BlockEntityRenderer<FlightAnchorEntity> {

    @Override
    public void render(FlightAnchorEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(-0.5D, 0, -0.5D);

        long gameTime = entity.getLevel().getGameTime();
        List<BeaconBlockEntity.BeaconBeamSection> beamSections = entity.getBeamSections();
        int yOffset = 0;

        for (int i = 0; i < beamSections.size(); ++i) {
            BeaconBlockEntity.BeaconBeamSection section = beamSections.get(i);
            int sectionHeight = section.getHeight();
            boolean isLastSection = (i == beamSections.size() - 1);
            int maxY = isLastSection ? 1024 : sectionHeight;

            BeaconRenderer.renderBeaconBeam(
                    poseStack,
                    bufferSource,
                    AnimationTickHolder.getPartialTicks(entity.getLevel()),
                    AnimationTickHolder.getTicks(entity.getLevel()),
                    yOffset,
                    maxY,
                    section.getColor()
            );

            yOffset += sectionHeight;
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(FlightAnchorEntity entity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRender(FlightAnchorEntity entity, Vec3 cameraPos) {
        Vec3 anchorCenter = Vec3.atCenterOf(entity.getBlockPos()).multiply(1.0D, 0.0D, 1.0D);
        Vec3 flatCameraPos = cameraPos.multiply(1.0D, 0.0D, 1.0D);
        return anchorCenter.closerThan(flatCameraPos, getViewDistance());
    }
}