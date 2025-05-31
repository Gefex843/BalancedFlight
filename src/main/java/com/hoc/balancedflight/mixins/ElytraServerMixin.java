package com.hoc.balancedflight.mixins;

import com.hoc.balancedflight.content.flightAnchor.FlightController;
import com.hoc.balancedflight.foundation.compat.AscendedRingCurio;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ElytraServerMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(
            method = "handlePlayerCommand",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/server/level/ServerPlayer;tryToStartFallFlying()Z"
            ),
            cancellable = true
    )
    private void startFallFlying(CallbackInfo ci) {
        boolean canAttemptFlight = !player.isFallFlying()
                && !player.isInWater()
                && !player.hasEffect(MobEffects.LEVITATION);

        if (!canAttemptFlight)
            return;

        if (!FlightController.allowedFlightModes(player, true).canElytraFly())
            return;

        player.startFallFlying();
        ci.cancel();
    }
}