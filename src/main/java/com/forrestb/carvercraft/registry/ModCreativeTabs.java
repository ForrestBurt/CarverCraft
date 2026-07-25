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
                    .icon(() -> ModItems.POLISHED_GARNET.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.SILVER_ORE.get());
                        output.accept(ModBlocks.DEEPSLATE_SILVER_ORE.get());
                        output.accept(ModItems.RAW_SILVER.get());
                        output.accept(ModItems.SILVER_INGOT.get());
                        output.accept(ModBlocks.ROCK_TUMBLER.get());
                        output.accept(ModItems.ROUGH_JASPER.get());
                        output.accept(ModItems.POLISHED_JASPER.get());
                        output.accept(ModItems.ROUGH_GARNET.get());
                        output.accept(ModItems.POLISHED_GARNET.get());
                        output.accept(ModItems.ROUGH_AGATE.get());
                        output.accept(ModItems.POLISHED_AGATE.get());
                        output.accept(ModItems.RUBY.get());
                        output.accept(ModItems.SILVER_RING.get());
                        output.accept(ModItems.JASPER_RING.get());
                        output.accept(ModItems.GARNET_RING.get());
                        output.accept(ModItems.AGATE_RING.get());
                        output.accept(ModItems.RUBY_RING.get());
                    }).build());
}
