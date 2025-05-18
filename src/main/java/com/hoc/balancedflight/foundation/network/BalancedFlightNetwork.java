package com.hoc.balancedflight.foundation.network;

import com.hoc.balancedflight.BalancedFlight;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class BalancedFlightNetwork {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int ID = 0;

    private static int nextID() {
        return ID++;
    }

    public static void registerMessage() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                ResourceLocation.fromNamespaceAndPath(BalancedFlight.MODID, "main_network"), // <-- correcto para versiones nuevas
                () -> VERSION,
                VERSION::equals,
                VERSION::equals
        );

        INSTANCE.messageBuilder(CustomNetworkMessage.class, nextID())
                .encoder(CustomNetworkMessage::toBytes)
                .decoder(CustomNetworkMessage::new)
                .consumerNetworkThread(CustomNetworkMessage::handler)
                .add(); // asegúrate de que usas una versión de Forge que tenga .add()
    }
}
