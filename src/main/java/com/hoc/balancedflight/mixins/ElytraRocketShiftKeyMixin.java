package com.hoc.balancedflight.mixins;

import com.hoc.balancedflight.content.flightAnchor.FlightController;
import com.hoc.balancedflight.foundation.config.BalancedFlightConfig;
import com.hoc.balancedflight.foundation.network.CustomNetworkMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;

@Mixin(LocalPlayer.class)
public class ElytraRocketShiftKeyMixin {

    private long lastUsedFireworkTime = 0;

    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;

        if (!player.isFallFlying() || !BalancedFlightConfig.infiniteRockets.get())
            return;

        FlightController.FlightMode allowed = FlightController.allowedFlightModes(player, true);
        if (!allowed.canElytraFly())
            return;

        if (Minecraft.getInstance().options.keySprint.isDown() && player.input.hasForwardImpulse()) {
            Level world = player.level();
            long now = Instant.now().toEpochMilli();

            if (now - lastUsedFireworkTime > 1000) {
                CustomNetworkMessage.Send(world, player, "FIRE_ROCKET");
                lastUsedFireworkTime = now;
            }
        }
    }
}