package com.vice.balancedflight;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class BalancedFlightClient
{
    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(BalancedFlightClient::clientInit);
    }

    public static void clientInit(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new BalancedFlightPonderPlugin());
    }
}
