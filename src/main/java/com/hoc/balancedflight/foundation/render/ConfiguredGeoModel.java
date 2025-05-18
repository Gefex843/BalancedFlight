package com.hoc.balancedflight.foundation.render;

import com.hoc.balancedflight.BalancedFlight;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class ConfiguredGeoModel extends GeoModel
{
    private String name;

    public ConfiguredGeoModel(String name) {

        this.name = name;
    }

    @Override
    public ResourceLocation getModelResource(GeoAnimatable object)
    {
        return ResourceLocation.fromNamespaceAndPath(BalancedFlight.MODID, "geo/" + name + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoAnimatable object)
    {
        return ResourceLocation.fromNamespaceAndPath(BalancedFlight.MODID, "textures/block/" + name + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAnimatable object)
    {
        return ResourceLocation.fromNamespaceAndPath(BalancedFlight.MODID, "animations/" + name + ".animation.json");
    }
}