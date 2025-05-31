package com.hoc.balancedflight.content.angelRing;

import com.hoc.balancedflight.BalancedFlight;
import com.hoc.balancedflight.foundation.compat.AscendedRingCurio;
import com.hoc.balancedflight.foundation.compat.ExternalMods;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = BalancedFlight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FlightRing extends Item {

    public FlightRing(Item.Properties properties) {
        super(properties);
    }

    @SubscribeEvent
    public static void sendInterModCommunication(InterModEnqueueEvent event) {
        if (ExternalMods.CURIOS.isLoaded()) {
            AscendedRingCurio.sendImc();
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return ExternalMods.CURIOS.isLoaded()
                ? AscendedRingCurio.initCapabilities((FlightRing) stack.getItem())
                : super.initCapabilities(stack, nbt);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.balancedflight.ascended_flight_ring").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.balancedflight.ascended_flight_ring_text2").withStyle(ChatFormatting.WHITE));
    }
}