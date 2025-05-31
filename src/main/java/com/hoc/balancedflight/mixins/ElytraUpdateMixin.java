package com.hoc.balancedflight.mixins;

import com.hoc.balancedflight.content.flightAnchor.FlightController;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class ElytraUpdateMixin {

    @Inject(
            method = "updateFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
            ),
            cancellable = true
    )
    private void updateFallFlying(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof ServerPlayer serverPlayer))
            return;

        if (FlightController.allowedFlightModes(serverPlayer, true).canElytraFly())
            ci.cancel();
    }
}