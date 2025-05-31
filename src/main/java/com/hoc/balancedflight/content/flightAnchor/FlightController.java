package com.hoc.balancedflight.content.flightAnchor;

import com.hoc.balancedflight.content.flightAnchor.entity.FlightAnchorEntity;
import com.hoc.balancedflight.foundation.compat.AscendedRingCurio;
import com.hoc.balancedflight.foundation.config.BalancedFlightConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class FlightController {

    public static void tick(Player player) {
        FlightMode allowed = allowedFlightModes(player, false);

        switch (allowed) {
            case None, Elytra -> {
                if (!player.isCreative() && player.getAbilities().mayfly) {
                    stopFlying(player);
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200));
                }
            }
            case Creative, Both -> {
                BlockPos posBelow = player.blockPosition().below();
                BlockState blockState = player.level().getBlockState(posBelow);
                int amplifier = BalancedFlightConfig.miningSpeedAmplifier.get();

                if (!player.getAbilities().mayfly) {
                    startFlying(player);
                    if (player.hasEffect(MobEffects.SLOW_FALLING))
                        player.removeEffect(MobEffects.SLOW_FALLING);
                }
                if (BalancedFlightConfig.isEnableMiningSpeedAmplifier.get() && player.getAbilities().mayfly && blockState.isAir()) {
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 5, amplifier, false, false));
                }
            }
        }
    }

    public static void startFlying(Player player) {
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }
    }

    public static void stopFlying(Player player) {
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().flying = false;
            player.getAbilities().mayfly = false;
            player.onUpdateAbilities();
        }
    }

    public static FlightMode allowedFlightModes(Player player, boolean onlyCareAboutElytra) {
        boolean hasAscended = AscendedRingCurio.HasAscendedRing(player);

        if (hasAscended) {
            boolean canElytraFly = BalancedFlightConfig.ElytraAscended.get();
            boolean canCreativeFly = BalancedFlightConfig.CreativeAscended.get();
            FlightMode allowedModes = FlightMode.fromBools(canElytraFly, canCreativeFly);

            if (allowedModes != FlightMode.Elytra)
                return allowedModes;

            if (!canCreativeFly && BalancedFlightConfig.CreativeAnchor.get() && isWithinFlightRange(player)) {
                return FlightMode.fromBools(canElytraFly, true);
            }

            return allowedModes;
        }

        boolean canElytraFly = BalancedFlightConfig.ElytraAnchor.get();
        boolean canCreativeFly = BalancedFlightConfig.CreativeAnchor.get();

        if (onlyCareAboutElytra && !canElytraFly)
            return FlightMode.None;

        return isWithinFlightRange(player) ? FlightMode.fromBools(canElytraFly, canCreativeFly) : FlightMode.None;
    }

    private static boolean isWithinFlightRange(Player player) {
        if (player.level().dimension() != Level.OVERWORLD)
            return false;

        double multiplier = BalancedFlightConfig.anchorDistanceMultiplier.get();

        return FlightAnchorEntity.ActiveAnchors.entrySet().stream()
                .anyMatch(anchor -> distSqr(anchor.getKey(), player.position()) < Math.pow(multiplier * anchor.getValue().getSpeed(), 2));
    }

    private static double distSqr(Vec3i vec, Vec3 other) {
        double dx = vec.getX() - other.x;
        double dz = vec.getZ() - other.z;
        return dx * dx + dz * dz;
    }

    public enum FlightMode {
        None,
        Elytra,
        Creative,
        Both;

        public static FlightMode fromBools(boolean elytraAllowed, boolean creativeAllowed) {
            if (elytraAllowed && creativeAllowed)
                return Both;
            if (elytraAllowed)
                return Elytra;
            if (creativeAllowed)
                return Creative;
            return None;
        }

        public boolean canElytraFly() {
            return this == Elytra || this == Both;
        }

        public boolean canCreativeFly() {
            return this == Creative || this == Both;
        }
    }
}