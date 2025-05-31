package com.hoc.balancedflight.content.flightAnchor.entity;

import com.google.common.collect.Lists;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.hoc.balancedflight.foundation.config.BalancedFlightConfig;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightAnchorEntity extends KineticBlockEntity implements GeoBlockEntity {
    public static final Map<BlockPos, FlightAnchorEntity> ActiveAnchors = new HashMap<>();

    @Getter
    List<BeaconBlockEntity.BeaconBeamSection> beamSections = Lists.newArrayList();
    List<BeaconBlockEntity.BeaconBeamSection> checkingBeamSections = Lists.newArrayList();
    int lastCheckY;
    boolean isActive;
    public float placedRenderTime;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation ANIMATION =
            RawAnimation.begin().then("animation.flight_anchor.deploy", Animation.LoopType.PLAY_ONCE);

    public FlightAnchorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new FlightAnchorBehaviour(this));
    }

    @Override
    public boolean shouldPlayAnimsWhileGamePaused() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, state -> state.setAndContinue(ANIMATION)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object entity) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public float calculateStressApplied() {
        float stress = BalancedFlightConfig.anchorStress.get();
        this.lastStressApplied = stress;
        return stress;
    }
}
