package com.hoc.balancedflight.foundation.events;

import com.hoc.balancedflight.content.flightAnchor.FlightController;
import com.hoc.balancedflight.foundation.compat.AscendedRingCurio;
import com.hoc.balancedflight.foundation.config.BalancedFlightConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber
public class CommonEvents
{
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        Player player = event.player;
        FlightController.tick(player);
    }


    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (Objects.equals(event.getSource().getMsgId(), "flyIntoWall") && BalancedFlightConfig.disableElytraDamage.get()) {
            if (event.getEntity() instanceof Player player) {
                if (FlightController.allowedFlightModes(player, true) != FlightController.FlightMode.None)
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (Objects.equals(event.getSource().getMsgId(), "fall")) {
            if (event.getEntity() instanceof Player player) {
                if (AscendedRingCurio.HasAscendedRing(player) && BalancedFlightConfig.disableFallDamageWhenWearingRing.get())
                    event.setCanceled(true);

                if (BalancedFlightConfig.disableFallDamageNearAnchor.get() && FlightController.allowedFlightModes(player, false) != FlightController.FlightMode.None)
                    event.setCanceled(true);
            }
        }
    }
}