package com.forrestb.carvercraft;

import com.forrestb.carvercraft.client.screen.AlloyerScreen;
import com.forrestb.carvercraft.client.screen.LapidaryScreen;
import com.forrestb.carvercraft.item.RingItem;
import com.forrestb.carvercraft.registry.ModItems;
import com.forrestb.carvercraft.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Client-only setup. Never loaded on a dedicated server. */
@EventBusSubscriber(modid = CarverCraft.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CarverCraftClient {

    @SubscribeEvent
    static void onRegisterScreens(RegisterMenuScreensEvent event) {
        // All machines share one screen class; the texture comes from the block entity.
        event.register(ModMenus.ROCK_TUMBLER.get(), LapidaryScreen::new);
        event.register(ModMenus.TRIM_SAW.get(), LapidaryScreen::new);
        event.register(ModMenus.CABBING_MACHINE.get(), LapidaryScreen::new);
        event.register(ModMenus.FACETING_MACHINE.get(), LapidaryScreen::new);
        event.register(ModMenus.ALLOYER.get(), AlloyerScreen::new);
    }

    /**
     * Faceted rings are drawn in two layers: a band normalised for tinting, and the
     * stone on top. Tint index 0 is the band — coloured by whichever metal it was set
     * in — and index 1 is the stone, which is never tinted.
     */
    @SubscribeEvent
    static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex == 0 ? RingItem.bandTint(stack) : 0xFFFFFF,
                ModItems.PERIDOT_RING.get(),
                ModItems.GARNET_RING.get(),
                ModItems.TOPAZ_RING.get(),
                ModItems.RUBY_RING.get(),
                ModItems.SAPPHIRE_RING.get(),
                ModItems.STAR_GARNET_RING.get(),
                ModItems.DIAMOND_RING.get(),
                ModItems.EMERALD_RING.get(),
                ModItems.AMETHYST_RING.get());
    }
}
