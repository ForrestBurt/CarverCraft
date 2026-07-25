package com.forrestb.carvercraft.client;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.client.screen.RockTumblerScreen;
import com.forrestb.carvercraft.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = CarverCraft.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CarverCraftClientEvents {

    @SubscribeEvent
    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ROCK_TUMBLER.get(), RockTumblerScreen::new);
    }
}
