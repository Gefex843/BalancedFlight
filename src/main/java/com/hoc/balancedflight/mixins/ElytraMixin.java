package com.hoc.balancedflight.mixins;

import com.hoc.balancedflight.content.flightAnchor.FlightController;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class ElytraMixin {
    @Inject(
            method = "aiStep()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
            ),
            cancellable = true
    )
    private void tryToStartFallFlying(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;

        boolean canStartFlying = !player.onGround()
                && !player.isFallFlying()
                && !player.isInWater()
                && !player.hasEffect(MobEffects.LEVITATION);

        if (!canStartFlying) return;

        FlightController.FlightMode allowed = FlightController.allowedFlightModes(player, true);

        if (allowed.canElytraFly()) {
            player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            ci.cancel();
        }
    }
}