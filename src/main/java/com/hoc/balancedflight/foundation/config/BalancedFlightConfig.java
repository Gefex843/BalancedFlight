package com.hoc.balancedflight.foundation.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.Registries;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class BalancedFlightConfig
{
    public static ForgeConfigSpec ConfigSpec;

    public static ConfigValue<Boolean> enableElytraFlightFromGround;
    public static ConfigValue<Boolean> enableTakeOff;
    public static ConfigValue<Boolean> infiniteRockets;

    public static ConfigValue<Boolean> ElytraAnchor;
    public static ConfigValue<Boolean> ElytraAscended;

    public static ConfigValue<Boolean> disableFallDamageWhenWearingRing;
    public static ConfigValue<Boolean> disableFallDamageNearAnchor;
    public static ConfigValue<Boolean> disableElytraDamage;

    public static ConfigValue<Boolean> CreativeAnchor;
    public static ConfigValue<Boolean> CreativeAscended;

    public static ConfigValue<Double> anchorDistanceMultiplier;
    public static ConfigValue<Integer> anchorStress;
    public static ConfigValue<Integer> miningSpeedAmplifier;
    public static ConfigValue<Boolean> isEnableMiningSpeedAmplifier;

    public static ConfigValue<List<? extends String>> additionalAllowedDimensions;

    static
    {
        ConfigBuilder builder = new ConfigBuilder("Balanced Flight Settings");

        builder.Block("Flight Options", b -> {
            CreativeAscended = b.define("Ascended Ring Gives Unlimited Creative Flight (will fall back to Basic tier inside range)", true);
            ElytraAscended = b.define("Ascended Ring Also Works As Elytra", true);

            CreativeAnchor = b.define("Flight Anchor Gives Creative Flight", true);
            ElytraAnchor = b.define("Flight Anchor Gives Elytra Flight", false);
        });

        builder.Block("Balancing Config", b -> {
            anchorDistanceMultiplier = b.defineInRange("Anchor Distance Multiplier (0d -> 10d, default 1d)", 1.0d, 0.0d, 10.0d);
            anchorStress = b.defineInRange("Anchor stress impact", 256, 0, Integer.MAX_VALUE);
            disableFallDamageWhenWearingRing = b.define("Disable Fall Damage When Wearing Ascended Ring", true);
            disableFallDamageNearAnchor = b.define("Disable Fall Damage Near Flight Anchor", true);
            isEnableMiningSpeedAmplifier = b.define("Enable Mining Speed Amplifier While Flying", true);
            miningSpeedAmplifier = b.defineInRange("Mining Speed Amplifier While Flying (1 -> 255, default 25)", 25, 1, 255);
        });

        builder.Block("Enhanced Elytra Mechanics", b -> {
            disableElytraDamage = b.define("Disable Elytra Damage", true);
            enableElytraFlightFromGround = b.define("Enable Elytra Flight From Ground", true);
            enableTakeOff =  b.define("Enable Take Off Mechanic", true);
            infiniteRockets = b.define("Infinite Rockets", true);
        });

        builder.Block("Dimension Restrictions", b -> {
            b.comment(
                    "Additional Allowed Dimensions for Flight (Only works in the Overworld by default)",
                    "Example of a valid list:",
                    "  [\"minecraft:the_nether\", \"minecraft:the_end\", \"mymod:custom_dimension\"]"
            );
            additionalAllowedDimensions = b.defineList(
                    "Additional Allowed Dimensions for Flight",
                    List.of(),
                    o -> o instanceof String
            );
        });

        ConfigSpec = builder.Save();
    }

    public static void init() {
        if (ConfigSpec.isLoaded())
            return;

        loadConfig(FMLPaths.CONFIGDIR.get().resolve("balanced_flight.toml"));
    }

    public static Set<ResourceKey<Level>> getAllowedDimensions() {
        return Stream.concat(
                        Stream.of(Level.OVERWORLD.location().toString()),
                        additionalAllowedDimensions.get().stream()
                )
                .map(ResourceLocation::tryParse)
                .filter(Objects::nonNull)
                .map(loc -> ResourceKey.create(Registries.DIMENSION, loc))
                .collect(Collectors.toSet());
    }

    private static void loadConfig(Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();

        configData.load();
        ConfigSpec.setConfig(configData);
    }
}