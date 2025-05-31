package com.hoc.balancedflight.content.flightAnchor;

import com.hoc.balancedflight.foundation.config.BalancedFlightConfig;
import com.hoc.balancedflight.foundation.render.AnimatedBlockItem;
import com.hoc.balancedflight.AllGeckoRenderers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class FlightAnchorItem extends AnimatedBlockItem<FlightAnchorItem> {

    public FlightAnchorItem(Block block, Properties props) {
        super(block, props, () -> AllGeckoRenderers.FlightAnchorGeckoRenderer.ItemRenderer);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        tooltip.add(Component.translatable("tooltip.balancedflight.flight_anchor_text2").withStyle(ChatFormatting.WHITE));
        if (BalancedFlightConfig.additionalAllowedDimensions.get().isEmpty()) {
            tooltip.add(Component.translatable("tooltip.balancedflight.flight_anchor_text3").withStyle(ChatFormatting.RED));
        }
    }
}