package com.vice.balancedflight;

import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.*;
import net.minecraft.resources.ResourceLocation;

public class BalancedFlightPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return BalancedFlight.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        AllPonderScenes.registerScenes(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        AllPonderScenes.registerTags(helper);
    }

    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {
    }

    @Override
    public void onPonderLevelRestore(PonderLevel ponderLevel) {
    }

    @Override
    public void indexExclusions(IndexExclusionHelper helper) {
    }
}
