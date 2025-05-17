package com.vice.balancedflight;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.vice.balancedflight.content.flightAnchor.FlightAnchorPonderScene;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class AllPonderScenes
{
    public static final ResourceLocation FLIGHT_ANCHOR = new ResourceLocation(BalancedFlight.MODID, "flight_anchor");

    public static void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.registerTag(FLIGHT_ANCHOR)
                .addToIndex()
                .item(BalancedFlight.FLIGHT_ANCHOR_BLOCK.get())
                .title("Flight Anchor")
                .description("Powered flight with Rotational Force")
                .register();

        HELPER.addToTag(FLIGHT_ANCHOR)
                .add(BalancedFlight.FLIGHT_ANCHOR_BLOCK);
    }

    public static void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(BalancedFlight.FLIGHT_ANCHOR_BLOCK)
                .addStoryBoard("flight_anchor", FlightAnchorPonderScene::ponderScene, FLIGHT_ANCHOR);
    }
}