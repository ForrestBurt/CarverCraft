package com.forrestb.carvercraft;

import com.forrestb.carvercraft.client.screen.RockTumblerScreen;
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
        event.register(ModMenus.ROCK_TUMBLER.get(), RockTumblerScreen::new);
    }
}
