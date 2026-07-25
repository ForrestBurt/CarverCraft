package com.forrestb.carvercraft;

import com.forrestb.carvercraft.client.screen.LapidaryScreen;
import com.forrestb.carvercraft.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * Client-only setup. Never loaded on a dedicated server, so touching client
 * classes from here is safe.
 */
@EventBusSubscriber(modid = CarverCraft.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CarverCraftClient {

    @SubscribeEvent
    static void onRegisterScreens(RegisterMenuScreensEvent event) {
        // All three machines share one screen class; the texture comes from the block entity.
        event.register(ModMenus.ROCK_TUMBLER.get(), LapidaryScreen::new);
        event.register(ModMenus.TRIM_SAW.get(), LapidaryScreen::new);
        event.register(ModMenus.FACETING_STATION.get(), LapidaryScreen::new);
    }
}
