package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CarverCraft.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CARVERCRAFT_TAB =
            CREATIVE_MODE_TABS.register("carvercraft", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.carvercraft"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.FACETED_STAR_GARNET.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // Ores and machines
                        output.accept(ModBlocks.SILVER_ORE.get());
                        output.accept(ModBlocks.DEEPSLATE_SILVER_ORE.get());
                        output.accept(ModBlocks.ROCK_TUMBLER.get());
                        output.accept(ModBlocks.TRIM_SAW.get());
                        output.accept(ModBlocks.FACETING_STATION.get());

                        // Metals
                        output.accept(ModItems.RAW_SILVER.get());
                        output.accept(ModItems.SILVER_INGOT.get());
                        output.accept(ModItems.STERLING_SILVER_INGOT.get());
                        output.accept(ModItems.ELECTRUM_INGOT.get());
                        output.accept(ModItems.ROSE_GOLD_INGOT.get());

                        // Opaque stones, in tumbler order
                        output.accept(ModItems.ROUGH_AGATE.get());
                        output.accept(ModItems.POLISHED_AGATE.get());
                        output.accept(ModItems.ROUGH_JASPER.get());
                        output.accept(ModItems.POLISHED_JASPER.get());
                        output.accept(ModItems.ROUGH_CARNELIAN.get());
                        output.accept(ModItems.POLISHED_CARNELIAN.get());
                        output.accept(ModItems.ROUGH_ROSE_QUARTZ.get());
                        output.accept(ModItems.POLISHED_ROSE_QUARTZ.get());
                        output.accept(ModItems.ROUGH_MALACHITE.get());
                        output.accept(ModItems.POLISHED_MALACHITE.get());

                        // Transparent stones, rough -> preform -> faceted
                        output.accept(ModItems.ROUGH_GARNET.get());
                        output.accept(ModItems.GARNET_PREFORM.get());
                        output.accept(ModItems.FACETED_GARNET.get());
                        output.accept(ModItems.ROUGH_PERIDOT.get());
                        output.accept(ModItems.PERIDOT_PREFORM.get());
                        output.accept(ModItems.FACETED_PERIDOT.get());
                        output.accept(ModItems.ROUGH_TOPAZ.get());
                        output.accept(ModItems.TOPAZ_PREFORM.get());
                        output.accept(ModItems.FACETED_TOPAZ.get());
                        output.accept(ModItems.ROUGH_SAPPHIRE.get());
                        output.accept(ModItems.SAPPHIRE_PREFORM.get());
                        output.accept(ModItems.FACETED_SAPPHIRE.get());
                        output.accept(ModItems.ROUGH_RUBY.get());
                        output.accept(ModItems.RUBY_PREFORM.get());
                        output.accept(ModItems.RUBY.get());
                        output.accept(ModItems.ROUGH_STAR_GARNET.get());
                        output.accept(ModItems.STAR_GARNET_PREFORM.get());
                        output.accept(ModItems.FACETED_STAR_GARNET.get());

                        // Jewelry
                        output.accept(ModItems.SILVER_RING.get());
                        output.accept(ModItems.GOLD_RING.get());
                        output.accept(ModItems.AGATE_RING.get());
                        output.accept(ModItems.JASPER_RING.get());
                        output.accept(ModItems.CARNELIAN_RING.get());
                        output.accept(ModItems.ROSE_QUARTZ_RING.get());
                        output.accept(ModItems.MALACHITE_RING.get());
                        output.accept(ModItems.GARNET_RING.get());
                        output.accept(ModItems.PERIDOT_RING.get());
                        output.accept(ModItems.TOPAZ_RING.get());
                        output.accept(ModItems.RUBY_RING.get());
                        output.accept(ModItems.SAPPHIRE_RING.get());
                        output.accept(ModItems.STAR_GARNET_RING.get());
                    }).build());
}
