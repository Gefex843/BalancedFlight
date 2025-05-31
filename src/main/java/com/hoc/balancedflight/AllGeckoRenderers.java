package com.hoc.balancedflight;

import com.hoc.balancedflight.content.flightAnchor.entity.FlightAnchorEntity;
import com.hoc.balancedflight.content.flightAnchor.render.FlightAnchorBeamRenderer;
import com.hoc.balancedflight.content.flightAnchor.render.FlightAnchorSafeRenderer;
import com.hoc.balancedflight.foundation.render.ConfiguredGeoModel;
import com.hoc.balancedflight.foundation.render.KineticGeckoRenderInfo;

public class AllGeckoRenderers {

    public static final KineticGeckoRenderInfo<FlightAnchorEntity, ?> FlightAnchorGeckoRenderer =
            new KineticGeckoRenderInfo<>(
                    new ConfiguredGeoModel("flight_anchor"),
                    new FlightAnchorSafeRenderer(),
                    BalancedFlight.FLIGHT_ANCHOR_BLOCK.get().defaultBlockState(),
                    new FlightAnchorBeamRenderer()
            );

}