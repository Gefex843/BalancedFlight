package com.hoc.balancedflight;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.hoc.balancedflight.content.angelRing.FlightRing;
import com.hoc.balancedflight.content.flightAnchor.FlightAnchorBlock;
import com.hoc.balancedflight.content.flightAnchor.FlightAnchorItem;
import com.hoc.balancedflight.content.flightAnchor.entity.FlightAnchorEntity;
import com.hoc.balancedflight.content.flightAnchor.render.FlightAnchorKineticInstance;
import com.hoc.balancedflight.foundation.RegistrateExtensions;
import com.hoc.balancedflight.foundation.config.BalancedFlightConfig;
import com.hoc.balancedflight.foundation.data.recipe.BalancedFlightRecipeGen;
import lombok.experimental.ExtensionMethod;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ExtensionMethod({ RegistrateExtensions.class})
@Mod(BalancedFlight.MODID)
public class BalancedFlight {
    public static final String MODID = "balancedflight";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final CreateRegistrate CREATE_REGISTRATE = com.simibubi.create.foundation.data.CreateRegistrate.create(BalancedFlight.MODID);

    public static final BlockEntry<? extends Block> FLIGHT_ANCHOR_BLOCK = BalancedFlight.CREATE_REGISTRATE
            .object("flight_anchor")
            .block(FlightAnchorBlock::new)
            .properties(properties -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(10).sound(SoundType.NETHERITE_BLOCK).noOcclusion())
            .defaultLoot()
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .geckoItem(FlightAnchorItem::new)
            .initialProperties(() -> new Item.Properties().stacksTo(1))
            .build()
            .register();

    public static final BlockEntityEntry<FlightAnchorEntity> FLIGHT_ANCHOR_BLOCK_ENTITY = BalancedFlight.CREATE_REGISTRATE
            .blockEntity("flight_anchor", FlightAnchorEntity::new)
            .visual(() -> FlightAnchorKineticInstance::new)
            .validBlock(FLIGHT_ANCHOR_BLOCK)
            .renderer(() -> AllGeckoRenderers.FlightAnchorGeckoRenderer.TileRenderer::apply)
            .register();

    public static final ItemEntry<? extends Item> ASCENDED_FLIGHT_RING = BalancedFlight.CREATE_REGISTRATE
            .item("ascended_flight_ring", FlightRing::new)
            .initialProperties(() -> new Item.Properties().stacksTo(1))
            .register();

    public BalancedFlight() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        CREATE_REGISTRATE.registerEventListeners(modEventBus);

        BalancedFlightConfig.init();
        MinecraftForge.EVENT_BUS.register(this);

        AllLangMessages.init();
        AllCreativeTabs.register(modEventBus);

        modEventBus.addListener(EventPriority.LOWEST, BalancedFlight::gatherData);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> BalancedFlightClient.onCtorClient(modEventBus, forgeEventBus));
    }

    public static void gatherData(GatherDataEvent event) {
        CREATE_REGISTRATE.addDataGenerator(ProviderType.LANG, prov -> {
            prov.add(AllCreativeTabs.CREATIVE_TAB.get(), "Create: Balanced Flight");
            prov.add("curios.identifier.flight_ring", "Flight Ring");
        });

        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        if (event.includeServer()) {
            gen.addProvider(true, new BalancedFlightRecipeGen(output));
        }
    }

    static {
        CREATE_REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB);
        CREATE_REGISTRATE.setTooltipModifierFactory((item) -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
        );
    }
}