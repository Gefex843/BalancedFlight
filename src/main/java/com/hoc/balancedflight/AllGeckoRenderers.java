package com.hoc.balancedflight;

import com.hoc.balancedflight.content.flightAnchor.entity.FlightAnchorEntity;
import com.hoc.balancedflight.content.flightAnchor.FlightAnchorItem;
import com.hoc.balancedflight.foundation.render.ConfiguredGeoModel;
import com.hoc.balancedflight.foundation.render.KineticGeckoRenderInfo;
import com.hoc.balancedflight.content.flightAnchor.render.*;

public class AllGeckoRenderers
{
    public static KineticGeckoRenderInfo<FlightAnchorEntity, ?> FlightAnchorGeckoRenderer =
            new KineticGeckoRenderInfo<FlightAnchorEntity, FlightAnchorItem>(
                    new ConfiguredGeoModel("flight_anchor"),
                    new FlightAnchorSafeRenderer(),
                    BalancedFlight.FLIGHT_ANCHOR_BLOCK.get().defaultBlockState(),
                    new FlightAnchorBeamRenderer());

}


