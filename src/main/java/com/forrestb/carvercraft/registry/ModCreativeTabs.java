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
                        output.accept(ModBlocks.CABBING_MACHINE.get());
                        output.accept(ModBlocks.FACETING_MACHINE.get());
                        output.accept(ModBlocks.ALLOYER.get());
                        output.accept(ModBlocks.CREATIVE_CHARGER.get());

                        // Metals and bands
                        output.accept(ModItems.RAW_SILVER.get());
                        output.accept(ModItems.SILVER_INGOT.get());
                        output.accept(ModItems.STERLING_SILVER_INGOT.get());
                        output.accept(ModItems.ELECTRUM_INGOT.get());
                        output.accept(ModItems.ROSE_GOLD_INGOT.get());
                        output.accept(ModItems.SILVER_BAND.get());
                        output.accept(ModItems.STERLING_SILVER_BAND.get());
                        output.accept(ModItems.GOLD_BAND.get());

                        // Shop supplies
                        output.accept(ModItems.SILICON_CARBIDE_GRIT.get());
                        output.accept(ModItems.POLISHING_COMPOUND.get());

                        // Opaque stones: rough -> tumbled -> slab -> cabochon
                        output.accept(ModItems.ROUGH_AGATE.get());
                        output.accept(ModItems.TUMBLED_AGATE.get());
                        output.accept(ModItems.AGATE_SLAB.get());
                        output.accept(ModItems.AGATE_CABOCHON.get());
                        output.accept(ModItems.ROUGH_JASPER.get());
                        output.accept(ModItems.TUMBLED_JASPER.get());
                        output.accept(ModItems.JASPER_SLAB.get());
                        output.accept(ModItems.JASPER_CABOCHON.get());
                        output.accept(ModItems.ROUGH_BRUNEAU_JASPER.get());
                        output.accept(ModItems.TUMBLED_BRUNEAU_JASPER.get());
                        output.accept(ModItems.BRUNEAU_JASPER_SLAB.get());
                        output.accept(ModItems.BRUNEAU_JASPER_CABOCHON.get());
                        output.accept(ModItems.ROUGH_CARNELIAN.get());
                        output.accept(ModItems.TUMBLED_CARNELIAN.get());
                        output.accept(ModItems.CARNELIAN_SLAB.get());
                        output.accept(ModItems.CARNELIAN_CABOCHON.get());
                        output.accept(ModItems.ROUGH_ROSE_QUARTZ.get());
                        output.accept(ModItems.TUMBLED_ROSE_QUARTZ.get());
                        output.accept(ModItems.ROSE_QUARTZ_SLAB.get());
                        output.accept(ModItems.ROSE_QUARTZ_CABOCHON.get());
                        output.accept(ModItems.ROUGH_MALACHITE.get());
                        output.accept(ModItems.TUMBLED_MALACHITE.get());
                        output.accept(ModItems.MALACHITE_SLAB.get());
                        output.accept(ModItems.MALACHITE_CABOCHON.get());

                        // Transparent stones: rough (-> tumbled) -> faceted
                        output.accept(ModItems.ROUGH_PERIDOT.get());
                        output.accept(ModItems.TUMBLED_PERIDOT.get());
                        output.accept(ModItems.FACETED_PERIDOT.get());
                        output.accept(ModItems.ROUGH_GARNET.get());
                        output.accept(ModItems.FACETED_GARNET.get());
                        output.accept(ModItems.ROUGH_TOPAZ.get());
                        output.accept(ModItems.FACETED_TOPAZ.get());
                        output.accept(ModItems.ROUGH_RUBY.get());
                        output.accept(ModItems.RUBY.get());
                        output.accept(ModItems.ROUGH_SAPPHIRE.get());
                        output.accept(ModItems.FACETED_SAPPHIRE.get());
                        output.accept(ModItems.ROUGH_STAR_GARNET.get());
                        output.accept(ModItems.FACETED_STAR_GARNET.get());

                        // Vanilla gems, faceting machine only
                        output.accept(ModItems.FACETED_DIAMOND.get());
                        output.accept(ModItems.FACETED_EMERALD.get());
                        output.accept(ModItems.FACETED_AMETHYST.get());

                        // Trinkets: tumbled stones in a silver band
                        output.accept(ModItems.AGATE_TRINKET.get());
                        output.accept(ModItems.JASPER_TRINKET.get());
                        output.accept(ModItems.BRUNEAU_JASPER_TRINKET.get());
                        output.accept(ModItems.CARNELIAN_TRINKET.get());
                        output.accept(ModItems.ROSE_QUARTZ_TRINKET.get());
                        output.accept(ModItems.MALACHITE_TRINKET.get());
                        output.accept(ModItems.PERIDOT_TRINKET.get());

                        // Rings: cabochons in sterling, faceted gems in gold
                        output.accept(ModItems.AGATE_RING.get());
                        output.accept(ModItems.JASPER_RING.get());
                        output.accept(ModItems.BRUNEAU_JASPER_RING.get());
                        output.accept(ModItems.CARNELIAN_RING.get());
                        output.accept(ModItems.ROSE_QUARTZ_RING.get());
                        output.accept(ModItems.MALACHITE_RING.get());
                        output.accept(ModItems.PERIDOT_RING.get());
                        output.accept(ModItems.GARNET_RING.get());
                        output.accept(ModItems.TOPAZ_RING.get());
                        output.accept(ModItems.RUBY_RING.get());
                        output.accept(ModItems.SAPPHIRE_RING.get());
                        output.accept(ModItems.STAR_GARNET_RING.get());
                        output.accept(ModItems.DIAMOND_RING.get());
                        output.accept(ModItems.EMERALD_RING.get());
                        output.accept(ModItems.AMETHYST_RING.get());
                    }).build());
}
