package com.hoc.balancedflight.content.flightAnchor.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.hoc.balancedflight.content.flightAnchor.entity.FlightAnchorEntity;
import com.hoc.balancedflight.foundation.render.ICreateSafeRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

public class FlightAnchorSafeRenderer implements ICreateSafeRenderer {

    @Override
    public void renderCreate(@Nullable KineticBlockEntity te, @Nullable BlockPos pos, BlockState blockState, PoseStack ms, MultiBufferSource buffer, int light) {
        Direction.Axis boxAxis = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).getAxis();

        float time = AnimationTickHolder.getRenderTime(te == null ? Minecraft.getInstance().level : te.getLevel());
        float placedTime = te instanceof FlightAnchorEntity ? ((FlightAnchorEntity) te).placedRenderTime : 0f;
        if (time - placedTime < 25f)
            return;
        float animationStart = placedTime + 25f;

        float speed = te == null ? 32 : te.getSpeed();

        for (Direction direction : Iterate.directions) {
            Direction.Axis axis = direction.getAxis();

            if (axis == boxAxis || direction == Direction.UP || direction == Direction.DOWN)
                continue;

            float angle = (time * speed * 0.3f) % 360;

            if (te != null)
                angle += KineticBlockEntityRenderer.getRotationOffsetForPosition(te, pos, axis);

            float angleRad = angle * ((float) Math.PI / 180f);

            SuperByteBuffer shaft = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, blockState, direction);

            ms.pushPose();
            if (te != null) {
                KineticBlockEntityRenderer.kineticRotationTransform(shaft, te, axis, angleRad, light);

                float progress = Mth.clamp(time - animationStart, 0f, 5f) / 5f;
                float scale = Mth.clampedLerp(0.01F, 1F, progress);

                if (axis == Direction.Axis.X) {
                    ms.translate((1 - scale) * 0.5F, 0, 0);
                    ms.scale(scale, 1F, 1F);
                } else {
                    ms.translate(0, 0, (1 - scale) * 0.5F);
                    ms.scale(1F, 1F, scale);
                }
            } else {
                shaft.light(light);
                shaft.rotateCentered(angleRad, Direction.get(Direction.AxisDirection.POSITIVE, axis));
                shaft.color(Color.WHITE);
                ms.translate(0, 0.5D, 0);
            }

            shaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
            ms.popPose();
        }
    }
}